package top.codef.config;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import top.codef.dao.CommonDao;
import top.codef.dao.TypedCommonDao;

@Configuration
@ConditionalOnClass({ SessionFactory.class })
public class AmebaConfig {

	@Bean
	public CommonDao commonDao() {
		return new CommonDao();
	}

	@Bean
	public TypedCommonDao typedCommonDao() {
		return new TypedCommonDao();
	}
}
