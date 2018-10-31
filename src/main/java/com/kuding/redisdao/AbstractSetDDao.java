package com.kuding.redisdao;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.redis.core.SetOperations;

public abstract class AbstractSetDDao extends AbstractRedisDDao {

	private SetOperations<String, String> getOps() {
		return getStringRedisTemplate().opsForSet();
	}

	public void addAll(String key, Collection<String> values) {
		getOps().add(getKey(key), values.toArray(new String[] {}));
	}

	public Set<String> members(String key) {
		return getOps().members(getKey(key));
	}

	public long size(String key) {
		return getOps().size(getKey(key));
	}

	public Long add(String key, String value) {
		return getOps().add(getKey(key), value);
	}

	public void remove(String key, String value) {
		getOps().remove(getKey(key), value);
	}
}
