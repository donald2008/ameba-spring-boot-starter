package top.codef.redisdao;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ListOperations;

public abstract class AbstractListDDao extends AbstractRedisDDao {

	private ListOperations<String, String> opsForList() {
		return getStringRedisTemplate().opsForList();
	}

	public String leftPop(String key) {
		String value = opsForList().leftPop(getKey(key));
		return value;
	}

	public void rightPush(String key, String value) {
		opsForList().rightPush(getKey(key), value);
	}

	public List<String> getAll(String key) {
		List<String> list = opsForList().range(getKey(key), 0, -1);
		return list;
	}

	public long pushAll(String key, Set<String> set) {
		long count = opsForList().rightPushAll(getKey(key), set);
		return count;
	}
	
	
}
