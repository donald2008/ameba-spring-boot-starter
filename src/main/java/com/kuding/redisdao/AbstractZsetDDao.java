package com.kuding.redisdao;

import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public abstract class AbstractZsetDDao extends AbstractRedisDDao {

	protected ZSetOperations<String, String> createZsetOperation() {
		return getStringRedisTemplate().opsForZSet();
	}

	public Set<TypedTuple<String>> getAllInfo(String key) {
		Set<TypedTuple<String>> set = createZsetOperation().rangeWithScores(getKey(key), 0, -1);
		return set;
	}

	public void remove(String key, Object... values) {
		createZsetOperation().remove(getKey(key), values);
	}

	public double increaseScore(String key, String value, double score) {
		double sc = createZsetOperation().incrementScore(getKey(key), value, score);
		return sc;
	}

	public Double score(String key, String value) {
		return createZsetOperation().score(getKey(key), value);
	}

	public void addAll(String key, Set<TypedTuple<String>> set) {
		createZsetOperation().add(getKey(key), set);
	}

	public void add(String key, String value, double score) {
		createZsetOperation().add(getKey(key), value, score);
	}

	public Set<String> range(String key, long start, long end) {
		return createZsetOperation().range(getKey(key), start, end);
	}

}