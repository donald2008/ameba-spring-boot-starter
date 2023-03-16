package top.codef.redisdao.interfaces;

import org.springframework.data.redis.core.StringRedisTemplate;

public interface RedisDao {

	public StringRedisTemplate getStringRedisTemplate();
}
