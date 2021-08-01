package com.kuding.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kuding.controllers.GlobalExceptionHandler;

@Configuration
@ConditionalOnProperty(name = "ameba.enable-error-advice", havingValue = "true")
@ConditionalOnWebApplication(type = Type.SERVLET)
public class CommonExceptionHandlerConfig {

	@Bean
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}
}
