package com.kuding.feign;

import java.nio.charset.StandardCharsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kuding.exceptions.HaveReasonException;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

	private final Gson gson;

	private Log logger = LogFactory.getLog(FeignErrorDecoder.class);

	public FeignErrorDecoder(Gson gson) {
		this.gson = gson;
	}

	@Override
	public Exception decode(String methodKey, Response response) {
		int code = response.status();
		if (code == 417) {
			String message = getMessage(response);
			return new HaveReasonException(message == null ? "error" : message);
		} else {
			logger.error("call error:" + code);
			return FeignException.errorStatus(methodKey, response);
		}
	}

	private String getMessage(Response response) {
		try {
			JsonObject jsonObject = gson.fromJson(response.body().asReader(StandardCharsets.UTF_8), JsonObject.class);
			String message = jsonObject.get("message").getAsString();
			logger.warn("feign client call error:" + message);
			return message;
		} catch (Exception e) {
			logger.debug("response body resolve error！", e);
			return null;
		}
	}
}
