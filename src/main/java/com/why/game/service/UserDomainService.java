package com.why.game.service;

import java.util.Map;

import com.why.game.bo.user.User;

public interface UserDomainService {

	User initNewUser(long userId);
	
	/** 每次登录生产标示该次登录的令牌，本次登录后，其他的业务操作，需要该令牌，才可以完成 */
	void generateTokenAndId(long userId, Map<String, Object> result);
	
}
