package com.kuding.redisdao;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;

public abstract class AbstractHashDDao extends AbstractRedisDDao {

	protected HashOperations<String, String, String> createBoundHash() {
		return getStringRedisTemplate().opsForHash();
	}

	public void putAll(String key, Map<String, String> map) {
		createBoundHash().putAll(getKey(key), map);
	}

	public String getHashValue(String key, String hashKey) {
		return createBoundHash().get(getKey(key), hashKey);
	}

	public void put(String key, String hashKey, String value) {
		createBoundHash().put(getKey(key), hashKey, value);
	}
}
