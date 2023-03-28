package top.codef.redisdao.interfaces;

import org.springframework.data.redis.core.StringRedisTemplate;

public interface RedisDao {

	/**
	 * @return
	 */
	public StringRedisTemplate getStringRedisTemplate();
}
