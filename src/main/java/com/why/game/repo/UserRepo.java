package com.why.game.repo;

import com.why.game.bo.user.AccountState;
import com.why.game.bo.user.User;

public interface UserRepo {
	
	long findUserId(String platformId);
	
	boolean isUserExist(String platformId);
	
	boolean savePlatformId(long userId, String platformId);
	
	long getUniqueId();
	
	long checkAndConvertUserId(String userIdStr);
	
	void checkAccountState(long userId);
	
	long convertUserId(String userIdStr);
	
	void checkAndLockOperation(long userId);
	
	AccountState getAccountState(long userId);
	
	boolean saveAccountState(long userId, AccountState accountState);
	
	String getPlatformId(long userId);
	
	String checkAndGetPlatformId(long userId);
	
	void addRankUser(long userId, int rank);
	
	void addUser(User user);

	User findUser(long userId);
	
	boolean saveUser(User user);
	
}
