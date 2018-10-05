package com.kuding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kuding.dao.CommonDao;

@Configuration
public class AmebaConfig {

	@Bean
	public CommonDao commonDao() {
		return new CommonDao();
	}
}
