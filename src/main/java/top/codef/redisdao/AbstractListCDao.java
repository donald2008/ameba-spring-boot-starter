package top.codef.redisdao;

import java.util.List;

import org.springframework.data.redis.core.BoundListOperations;

public abstract class AbstractListCDao extends AbstractRedisCDao {

	private BoundListOperations<String, String> opsForList() {
		return getStringRedisTemplate().boundListOps(getKey());
	}

	public String leftPop() {
		String value = opsForList().leftPop();
		return value;
	}

	public void rightPush(String value) {
		opsForList().rightPush(value);
	}

	public List<String> getAll() {
		List<String> list = opsForList().range(0, -1);
		return list;
	}

	public void remove(String value) {
		opsForList().remove(-1, value);
	}
}
