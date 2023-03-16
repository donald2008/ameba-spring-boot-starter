package top.codef.redisdao;

import java.util.concurrent.TimeUnit;

import top.codef.redisdao.interfaces.RedisDDao;

public abstract class AbstractRedisDDao implements RedisDDao {

	@Override
	public boolean hasKey(String key) {
		return getStringRedisTemplate().hasKey(getKey(key));
	}

	@Override
	public void deleteKey(String key) {
		getStringRedisTemplate().delete(getKey(key));

	}

	@Override
	public Boolean expire(String key, long time, TimeUnit timeUnit) {
		return getStringRedisTemplate().expire(getKey(key), time, timeUnit);
	}

}
