# ambeba-spring-boot-starter 一个神奇的约定工具


## 系统需求
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

4. 在``application.properties``或者``application.yaml``配置中配置数据连接，其实就是spring data jpa的相关连接，当然还需要在对应的数据中创建相关的数据表信息

[![数据表](https://resources.codef.top/ameba/pic/1.PNG "数据表")](https://resources.codef.top/ameba/pic/1.PNG "数据表")

```yaml
spring:
  application:
    name: spring-boot-fun
  jpa:
    database: mysql
    show-sql: true
    properties:
      hibernate:
        '[format_sql]': true
        '[default_batch_fetch_size]': 10
  datasource: 
    username: your username
    password: your pwd
    url: jdbc:mysql://127.0.0.1:3306/have_fun?useSSL=false&serverTimezone=GMT%2b8
    driver-class-name: com.mysql.cj.jdbc.Driver
```

5. 准备工作搞定以后，就可以进行测试了，这里通过springboot项目的JUnitTest进行个简单的测试：

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.havefun.user.entities.User;
import com.havefun.user.services.UserService;

@SpringBootTest
class Springboot3ApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	void contextLoads() {
		userService.create(new User("phone num", "raw pwd", "some name"));
	}
}
```

6. 运行测试后，控制台会打印出对应的sql语句：
```
Hibernate: 
    insert 
    into
        user
        (password, phone, username) 
    values
        (?, ?, ?)
```
这就是一个简单的数据持久化过程了！
[![1-1](https://resources.codef.top/ameba/pic/1-1.PNG "1-1")](http://https://resources.codef.top/ameba/pic/1-1.PNG "1-1")

7. 接着就是查询与修改
```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.havefun.user.services.UserService;

@SpringBootTest 
class Springboot3ApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	void contextLoads() {
		var user = userService.get(1L);
		System.out.println(user);
	}
//	Hibernate: 
//	    select
//	        u1_0.id,
//	        u1_0.password,
//	        u1_0.phone,
//	        u1_0.username 
//	    from
//	        user u1_0 
//	    where
//	        u1_0.id=?
//	User [id=1, phone=phone num, password=raw pwd, username=some name]
}
```

以上就是最简单的一个例子。


#### SQL建造者：QueryBuilder

通常情况下，对于处理SQL不会那么简单，可能会稍微的复杂一些，JPA中提供了一套完整的去SQL化查询机制：Criteria SQL，由hibernate进行了实现，Criteria去进行处理相对于直接写SQL会显得有些复杂，不过好处是其良好的二次封装特性。本人就在criteria的基础上进行的二次封装。

1. 先上个最简单的例子：根据上面的例子，需要通过手机号获取一个用户信息
```java
	public User getUserByPhone(String phone) {
		var user = userDao.getSingle(User.class, QueryBuilder.createFilter().eq("phone", phone));
		return user;
	}
//	Hibernate: 
//	    select
//	        u1_0.id,
//	        u1_0.password,
//	        u1_0.phone,
//	        u1_0.username 
//	    from
//	        user u1_0 
//	    where
//	        u1_0.phone=?
//	User [id=1, phone=phone num, password=raw pwd, username=some name]
```
其中``QueryBuilder``中提供了一个静态方法：
```java
public static CommonFilter createFilter() {
		return new CommonFilter();
	}
```
其中``CommonFilter``里面包含了一个SQL语句中需要的一些必要组成部部分，例如
	1. where条件相关
	2. order相关
	3. groupby相关
	4. select相关
	5. update相关
	6. join相关
等等，根据这些组成部分，来进行Criteria的组装，最终得到想要的数据结果，下面就是CommonFilter中支持的where的一些操作

| 操作  | 方法  |
| :------------: | :------------: |
|  where相关 |
| 相等  | eq  |
| 不等  | neq  |
| 近似  | like  |
| 为null  | isNull  |
| 不为null  | isNotNull  |
| 包含  | in  |
| 不包含  | notIn  |
| 大于  | gt  |
| 大于等于  | ge  |
| 小于  | lt  |
| 小于等于  | le  |
| 在……之间  | between  |

- **特别说明一点，这里的where相关操作全部是由and连接，并没有去兼容or的操作，因为在我日常写代码的过程中，能出现用or的操作有但是远远没有and那么多，假如出现or的问题，可以使用JPQL或者原生SQL等其他方法实现就好**


#### DAO层核心：BaseDao

``BaseDao``基本上涵盖了大部分Criteria操作，常用的增删改查都能通过BaseDao内的方法来实现，下面列举出BaseDao的一些操作：

| 方法名  | 说明  |
|---|---|
| getSingle  | 获取单个实例  |
| getList  | 获取列表数据  |
| getPage  | 查询分页  |
| count  |  获取数量  |
| countDistinct  | 获取数量（去重）  |
|  update |  update操作  |
| updateWithNull  |  update操作（可传null）  |
| delete  |  delete操作（条件删除）  |

除了上述的一些方法外，``BaseDao``还继承了``AbstractDao``的操作，``AbstractDao``中包含了最基础的CRUD相关操作（create、merge、detach、delete、get，flush、refresh，lock），同时可以获取当前数据操作对应的``EntityManager``与``Session``(hibernate)。

#### 拿getSingle举例子
- 上一节讲的例子：

```java
	public User getUserByPhone(String phone) {
		var user = userDao.getSingle(User.class, QueryBuilder.createFilter().eq("phone", phone));
		return user;
	}
```
表明了通过条件查询单个实体信息可以像上述方法那样做，需要注意的是，通过``getsingle``方法查询出的结果包含多条的话，会报错：

```
org.springframework.dao.IncorrectResultSizeDataAccessException: query did not return a unique result: 2
```
- 查询某个字段：如果查询某个字段的话可以类似这么做：
```java
	/**
	 * 通过手机号获取用户名
	 * 
	 * @param phone
	 * @return
	 */
	public String getUserNameByPhone(String phone) {
		var username = userDao.getSingle(String.class, User.class,
				QueryBuilder.createFilter().select("username").eq("phone", phone));
		return username;
	}
//	Hibernate: 
//	    select
//	        u1_0.username 
//	    from
//	        user u1_0 
//	    where
//	        u1_0.phone=?
//	some name
```
通过``QueryBuilder.createFilter().select("实体类字段名")``的方式就可以进行某个字段的查询了，多字段查询时一样的方式，例如需要通过手机号获取没有password的用户信息，做到如下几步就可实现：
1.可以在``User``中添加构造器：
```java
	public User(String phone, String username) {
		super();
		this.phone = phone;
		this.username = username;
	}

```
2. 按照构造器参数顺序，通过``QueryBuilder``构建``CommonFilter``，然后调用``select("参数1","参数2")``
```java
	public User getReadUser(String phone) {
		var user = userDao.getSingle(User.class,
				QueryBuilder.createFilter().select("phone", "username").eq("phone", phone));
		return user;
	}
//		Hibernate: 
//		    select
//		        u1_0.phone,
//		        u1_0.username 
//		    from
//		        user u1_0 
//		    where
//		        u1_0.phone=?
//		User [id=null, phone=phone num, password=null, username=some name]

```
方式也非常简单。



#### 一些架构上的约定方式

**有些说明很重要，需要注意**

1. 传参约定：通过``QueryBuilder``构建``CommonFilter``时，假如通过类似``eq``，``lt``等判断方法时，若其传的参数为null，代码则认为此条件为**无效条件**，例如：
```java
	public List<User> getList(String phone) {
		var list = userDao.getList(User.class, QueryBuilder.createFilter().eq("phone", phone));
		return list;
	}
```
假如传入的``phone``为``null``时，构建的查询语句不会存在``where phone = ...``
```java
	@Test
	void nullTest() {
		userService.getList(null).forEach(System.out::println);
//		Hibernate: 
//		    select
//		        u1_0.id,
//		        u1_0.password,
//		        u1_0.phone,
//		        u1_0.username 
//		    from
//		        user u1_0
//		User [id=1, phone=phone num, password=raw pwd, username=some name]
//		User [id=2, phone=2, password=2, username=2]	
	}
```
这样做的目的在于大多数查询筛选过程中会把筛选条件当成非必填参数进行传递，一般情况下非必填参数是不需添加``where``相关操作的。

2. 分页查询：本框架会自带分页查询方法：``getPage``，例如：
```java
public <T> Page<T> getPage(Class<T> clazz, Pageable pageable, CommonFilter filter)
```
其中``Pageable``结构如下：
```java
public class Pageable {

	private Long pageCount;// 页数

	private int eachPageSize = 15;// 每页大小

	private int pageNo = 1;// 页码

	private OrderEnum order = OrderEnum.DESC;// 排序方式

	private String orderStr = "id";// 排序字段名

	private Long totalCount; //总数

//getter and setter
}
```
需要说明一下：页码是从第一页开始，每页大小默认为15个，默认情况下，数据分页是按照``id``字段倒排序获取，查询完成后会回填``pageCount``页数与``totalCount``数据总数；返回值是``Page<T>``对象：
```
public class Page<T> {

	private Pageable pageable;

	private List<T> content = new ArrayList<T>(0);

	public Page(Pageable pageable) {
		this.pageable = pageable;
	}

	public Page() {
	}
}
```
基本上``pageable``对应的参数足够使用，假如说还有其他相关需求可以发布issue讨论







#### join table 相关操作

1. jpa相关的join操作是需要配个JPA中``@JoinColumn``进行操作的，例如，上面给出了``User``,同时``UserAuth``与``User``成多对一关系

```java
@Entity
@Getter
@Setter
public class UserAuth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private String authName;

	public UserAuth(User user, String authName) {
		this.user = user;
		this.authName = authName;
	}

	public UserAuth() {
	}

```
2. 创建一个``UserAuth``的操作，基本上应该是这样
```
@Service
@Transactional
public class AuthService {

	@Autowired
	private UserDao userDao;

	public UserAuth create(Long userId, String authName) {
		var user = userDao.get(User.class, userId);
		if (user == null)
			throw new HaveReasonException("无此用户");
		var auth = new UserAuth(user, authName);
		userDao.create(auth);
		return auth;
	}
}
//Hibernate: 
//    select
//        u1_0.id,
//        u1_0.password,
//        u1_0.phone,
//        u1_0.username 
//    from
//        user u1_0 
//    where
//        u1_0.id=?
//Hibernate: 

//    insert 
//    into
//        user_auth
//        (auth_name, user_id) 
//    values
//        (?, ?)
```








