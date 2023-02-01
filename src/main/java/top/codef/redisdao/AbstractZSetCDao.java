package top.codef.redisdao;

import java.util.Set;

import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public abstract class AbstractZSetCDao extends AbstractRedisCDao {

	public BoundZSetOperations<String, String> createZsetOperation() {
		return getStringRedisTemplate().boundZSetOps(getKey());
	}

	public void remove(Object... values) {
		createZsetOperation().remove(values);
	}

	public double increaseScore(String value, double score) {
		double sc = createZsetOperation().incrementScore(value, score);
		return sc;
	}

	public Double score(String value) {
		return createZsetOperation().score(value);
	}

	public void addAll(Set<TypedTuple<String>> set) {
		createZsetOperation().add(set);
	}

	public void add(String value, double score) {
		createZsetOperation().add(value, score);
	}

	public Set<String> range(String key, long start, long end) {
		return createZsetOperation().range(start, end);
	}

	public Set<String> rangebyScore(double start, double end) {
		return createZsetOperation().rangeByScore(start, end);
	}

	public Long removeByRange(String key, double start, double end) {
		return createZsetOperation().removeRangeByScore(start, end);
	}
}
