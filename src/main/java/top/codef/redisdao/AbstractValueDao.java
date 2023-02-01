package top.codef.redisdao;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ValueOperations;

public abstract class AbstractValueDao extends AbstractRedisDDao {

	protected ValueOperations<String, String> createValueOperation() {
		return getStringRedisTemplate().opsForValue();
	}

	public void set(String key, String value, int time, TimeUnit unit) {
		createValueOperation().set(getKey(key), value, time, unit);
	}

	public void set(String key, String value, Duration time) {
		createValueOperation().set(getKey(key), value, time);
	}

	public void set(String key, String value) {
		createValueOperation().set(getKey(key), value);
	}

	public Boolean setIfAbsent(String key, String value) {
		return createValueOperation().setIfAbsent(getKey(key), value);
	}

	public Boolean setIfAbsent(String key, String value, Duration duration) {
		return createValueOperation().setIfAbsent(getKey(key), value, duration);
	}

	public String get(String key) {
		return createValueOperation().get(getKey(key));
	}

	public long getExpire(String key, TimeUnit unit) {
		return getStringRedisTemplate().getExpire(getKey(key), unit);
	}
}
