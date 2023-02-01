package top.codef.redisdao;

import java.util.concurrent.TimeUnit;

import top.codef.redisdao.interfaces.RedisCDao;

public abstract class AbstractRedisCDao implements RedisCDao {

	@Override
	public boolean hasKey() {
		return getStringRedisTemplate().hasKey(getKey());
	}

	@Override
	public void deleteKey() {
		getStringRedisTemplate().delete(getKey());

	}

	@Override
	public Boolean expire(long time, TimeUnit timeUnit) {
		return getStringRedisTemplate().expire(getKey(), time, timeUnit);
	}

}
