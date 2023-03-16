package top.codef.redisdao.interfaces;

import java.util.concurrent.TimeUnit;

public interface RedisCDao extends RedisDao {

	public String getKey();

	public boolean hasKey();

	public void deleteKey();

	public Boolean expire(long time, TimeUnit timeUnit);

}
