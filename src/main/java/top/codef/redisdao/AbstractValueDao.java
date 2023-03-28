package top.codef.redisdao;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ValueOperations;

public abstract class AbstractValueDao extends AbstractRedisDDao {

	/**
	 * @return
	 */
	protected ValueOperations<String, String> createValueOperation() {
		return getStringRedisTemplate().opsForValue();
	}

	/**
	 * @param key
	 * @param value
	 * @param time
	 * @param unit
	 */
	public void set(String key, String value, int time, TimeUnit unit) {
		createValueOperation().set(getKey(key), value, time, unit);
	}

	/**
	 * @param key
	 * @param value
	 * @param time
	 */
	public void set(String key, String value, Duration time) {
		createValueOperation().set(getKey(key), value, time);
	}

	/**
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		createValueOperation().set(getKey(key), value);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public Boolean setIfAbsent(String key, String value) {
		return createValueOperation().setIfAbsent(getKey(key), value);
	}

	/**
	 * @param key
	 * @param value
	 * @param duration
	 * @return
	 */
	public Boolean setIfAbsent(String key, String value, Duration duration) {
		return createValueOperation().setIfAbsent(getKey(key), value, duration);
	}

	/**
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return createValueOperation().get(getKey(key));
	}

	/**
	 * @param key
	 * @param unit
	 * @return
	 */
	public long getExpire(String key, TimeUnit unit) {
		return getStringRedisTemplate().getExpire(getKey(key), unit);
	}
}
