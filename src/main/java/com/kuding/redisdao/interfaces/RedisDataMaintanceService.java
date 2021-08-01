package com.kuding.redisdao.interfaces;

/**
 * 
 * 用于Redis数据的维护
 * 
 * @author donald
 *
 */
public interface RedisDataMaintanceService {

	/**
	 * 获取redidao
	 * 
	 * @return
	 */
	RedisDao getRedisDao();

	/**
	 * 同步
	 */
	void sync();

	/**
	 * 重置
	 */
	void reset();

}
