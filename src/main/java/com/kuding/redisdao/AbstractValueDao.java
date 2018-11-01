package com.kuding.redisdao;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ValueOperations;

public abstract class AbstractValueDao extends AbstractRedisDDao {

	protected ValueOperations<String, String> createValueOperation() {
		return getStringRedisTemplate().opsForValue();
	}

	public void set(String key, String value, int time, TimeUnit unit) {
		createValueOperation().set(getKey(key), value, time, unit);
	}

	public void set(String key, String value, long offset) {
		createValueOperation().set(getKey(key), value, offset);
	}

	public String get(String key) {
		return createValueOperation().get(getKey(key));
	}

	public void delete(String key) {
		getStringRedisTemplate().delete(getKey(key));
	}

	public long getExpire(String key, TimeUnit unit) {
		return getStringRedisTemplate().getExpire(getKey(key), unit);
	}
}
