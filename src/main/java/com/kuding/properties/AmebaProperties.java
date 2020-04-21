package com.kuding.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ameba")
public class AmebaProperties {

	private Boolean enableErrorAdvice;

	public Boolean getEnableErrorAdvice() {
		return enableErrorAdvice;
	}

	public void setEnableErrorAdvice(Boolean enableErrorAdvice) {
		this.enableErrorAdvice = enableErrorAdvice;
	}

}
