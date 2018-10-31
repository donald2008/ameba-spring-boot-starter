package com.kuding.redisdao;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.redis.core.BoundSetOperations;

public abstract class AbstractSetCDao extends AbstractRedisCDao {

	private BoundSetOperations<String, String> getOps() {
		return getStringRedisTemplate().boundSetOps(getKey());
	}

	public void addAll(Collection<String> values) {
		getOps().add(values.toArray(new String[] {}));
	}

	public Set<String> members() {
		return getOps().members();
	}

	public long size() {
		return getOps().size();
	}

	public Long add(String value) {
		return getOps().add(value);
	}

	public boolean isMember(String value) {
		return getOps().isMember(value);
	}

	public void remove(String value) {
		getOps().remove(value);
	}
}
