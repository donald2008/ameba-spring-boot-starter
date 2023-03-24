package top.codef.redisdao.interfaces;

import java.util.concurrent.TimeUnit;

public interface RedisDDao extends RedisDao {

	/**
	 * @param key
	 * @return
	 */
	public String getKey(String key);

	/**
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key);

	/**
	 * @param key
	 */
	public void deleteKey(String key);

	/**
	 * @param key
	 * @param time
	 * @param timeUnit
	 * @return
	 */
	public Boolean expire(String key, long time, TimeUnit timeUnit);

}
