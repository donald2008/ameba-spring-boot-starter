#ambeba-spring-boot-starter 一个神奇的约定工具


##系统需求
![jdk版本](https://img.shields.io/badge/java-17%2B-red.svg?style=for-the-badge&logo=appveyor) ![maven版本](https://img.shields.io/badge/maven-3.2.5%2B-red.svg?style=for-the-badge&logo=appveyor) ![spring boot](https://img.shields.io/badge/spring%20boot-3.0.0%2B-red.svg?style=for-the-badge&logo=appveyor)

## 当前版本

![目前工程版本](https://img.shields.io/badge/version-0.4.0-green.svg?style=for-the-badge&logo=appveyor)


## 前言

基于springboot开发javaweb项目，最最常用的功能无外乎一个写controller的springmvc与数据库相关框架了，javaweb开发往往离不开这两块框架，实际工作中代码工作量最多的工作也就在这两块。

- 对于使用springboot的程序猿们，写接口基本上离不开``spring-boot-starter-web``，针对web项目的安全框架``spring-boot-starter-security``也是基于web项目扩展的。
- 对于连接数据库而言最基础就是``jdbc``(java database connectivity)，基本上java大部分数据库框架都基于此开发，在jdbc的基础上，oracle（实际上是原来的sun）制定了``JPA（Java Persistence API）``规范，目前最主流的基于jpa规范的orm框架是hibernate，也是spring-data-jpa底层框架。
- 除了上述两大块以外，还有redis相关的操作也是比较常用，springboot也提供了相关的封装：``spring-data-redis``
- 本框架主要基于以上三个模块进行了亿点工具化封装，主要的作用是**减少重复代码量，提高工作效率**，可以让程序猿们更多精力的聚焦到业务中。
- 本项目目前支持``springboot3.0``，想用之前的版本请转``old``分支查看


## 项目引入

- 将此项目引入IDE，进行``mvn install``,在其他项目中引入，目前我在尝试引入到中央仓库，后续就不需要在进行导入了，直接使用即可

```xml
<dependency>
	<groupId>top.codef</groupId>
	<artifactId>ameba-spring-boot-starter</artifactId>
	<version>0.4.0</version>
</dependency>
```

- 引入后，项目内就可以使用了，框架大致分类三个模块
	1. jpa工具模块
	2. redis工具模块
	3. web约定工具模块模块

下面就依次进行几个模块的介绍与使用


### jpa工具模块

#### 前言

java的orm框架很多，最常用的两个：
1. hibernate
2. mybatis

Hibernate和MyBatis都是Java中比较流行的ORM框架，但它们在实现上有很大的区别。以下是Hibernate和MyBatis的区别：

- 编程范式：Hibernate是基于JPA规范实现的ORM框架，它采用的是面向对象编程的范式。而MyBatis是一个半自动化的持久化框架，它采用的是基于SQL的编程范式。

- 映射方式：Hibernate通过注解或XML配置文件将Java类和数据库表进行映射，可以实现自动映射和一些高级映射，例如继承映射、多对多关系等。而MyBatis使用XML配置文件将SQL语句和数据库表进行映射，开发人员需要手动编写SQL语句。

- 性能：Hibernate会对整个对象图进行处理，可能会产生大量的SQL语句，从而导致性能瓶颈。而MyBatis只会执行映射文件中定义的SQL语句，可以精确控制SQL执行的效率。

- 数据库支持：Hibernate支持多种数据库，包括关系型数据库和NoSQL数据库。而MyBatis只支持关系型数据库。

- 灵活性：MyBatis提供了很大的灵活性，可以编写自定义的SQL语句和映射规则，可以满足各种特殊需求。而Hibernate更适合开发基于对象的应用程序，提供了更高层次的抽象和自动化。

这里框架选择hibernate主要有三个方面原因
1. spring-data-jpa支持，同时底层实现也是hibernate，除了需要jdbc外，不需要额外到其他的依赖。
2. 从事业务有关，我主要从事开发toB相关业务，需要的SQL执行效率要求不是太高。
3. 去SQL化与对象化，假如能做到大部分的数据库操作不用写SQL语句……嗯应该会很爽，同时有dialect支持，多个数据库一套代码搞定……嗯，也应该会很爽。对象化？不用写xml文件……嗯，还不错

#### 简单例子起手

1. 首先先来一个标准的Entity: User.java(这里用了lombok替代getter与setter)，至于jpa相关注解请看[jpa官网](https://www.oracle.com/java/technologies/persistence-jsp.html)

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String phone;

	private String password;

	private String username;
}
```

2. 开始创建dao层：

```java
import org.springframework.stereotype.Repository;

import top.codef.dao.BaseDao;

@Repository
public class UserDao extends BaseDao {
}

```
这就完事了？没错，BaseDao是一个抽象类，里面包含了很多数据库相关操作方法，具体方法后面会详细介绍

3. 我们再来填一个Service，用于处理User相关业务的,里面包含了CRUD4个基础方法

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.havefun.user.dao.UserDao;
import com.havefun.user.entities.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserDao userDao;

	public User create(User user) {
		userDao.create(user);
		return user;
	}

	public User modify(User user) {
		userDao.merge(user);
		return user;
	}

	public User get(Long id) {
		return userDao.get(User.class, id);
	}

	public void Delete(Long id) {
		userDao.delete(id);
	}
}
```






