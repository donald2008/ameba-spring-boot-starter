package top.codef.redisdao.interfaces;

import java.util.concurrent.TimeUnit;

public interface RedisDDao extends RedisDao {

	public String getKey(String key);

	public boolean hasKey(String key);

	public void deleteKey(String key);

	public Boolean expire(String key, long time, TimeUnit timeUnit);

}
