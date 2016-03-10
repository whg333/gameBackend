package com.why.game.service;

import java.util.List;

import com.why.game.bo.user.AccountState;

/**
 * 反外挂接口
 * @author JasonLee
 *
 */
public interface AntiPluginDomainService {
	
	void freezeUser(Long userId, long freezeTime);
	
	void freezeUser(Long userId, int abuseCount);
	
	void freezeUsers(List<Long> userIdList, long freezeTime);
	
	void unfreezeUser(Long userId);
	
	void unfreezeUsers(List<Long> userIdList);
	
	void addSingleBlackList(Long userId);
	
	void addBlackList(List<Long> userIdList);
	
	void delSingleBlackList(Long userId);
	
	void delBlackList(List<Long> userIdList);
	
	void refreshFreezeState(Long userId);
	
	void refreshFreezeState(Long userId, AccountState accountState);
	
	boolean isWarningBotUser(Long userId);

}
