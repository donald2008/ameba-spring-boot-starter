package com.kuding.redisdao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.BoundHashOperations;

public abstract class AbstractHashCDao extends AbstractRedisCDao {

	protected BoundHashOperations<String, String, String> createBoundHash() {
		return getStringRedisTemplate().boundHashOps(getKey());
	}

	public void putAll(Map<String, String> map) {
		createBoundHash().putAll(map);
	}

	public String getHashValue(String hashKey) {
		return createBoundHash().get(hashKey);
	}

	public String get(String hashkey) {
		return createBoundHash().get(hashkey);
	}

	public void put(String hashKey, String value) {
		createBoundHash().put(hashKey, value);
	}

	public Map<String, String> entries() {
		return createBoundHash().entries();
	}

	public void remove(String hashKey) {
		createBoundHash().delete(hashKey);
	}

	public List<String> getList(Collection<String> keys) {
		List<String> values = createBoundHash().multiGet(keys);
		return values;
	}

}
