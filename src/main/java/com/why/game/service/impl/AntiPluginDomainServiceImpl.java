package com.why.game.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hoolai.util.TimeUtil;
import com.why.game.bo.user.AccountState;
import com.why.game.repo.UserRepo;
import com.why.game.service.AntiPluginDomainService;

@Service("antiPluginDomainService")
public class AntiPluginDomainServiceImpl implements AntiPluginDomainService {
	
	@Autowired
	private UserRepo userRepo;

	@Override
	public void addBlackList(List<Long> userIdList) {
		for(long userId : userIdList) {
		    addSingleBlackList(userId);	
		}
	}

	@Override
	public void addSingleBlackList(Long userId) {
	   AccountState accountState = userRepo.getAccountState(userId);
	   accountState.setBlackList(true);
	   userRepo.saveAccountState(userId, accountState);
	}

	@Override
	public void delBlackList(List<Long> userIdList) {
		for(long userId : userIdList) {
			delSingleBlackList(userId);
		}
	}

	@Override
	public void delSingleBlackList(Long userId) {
	       AccountState accountState = userRepo.getAccountState(userId);
		   accountState.setBlackList(false);
		   userRepo.saveAccountState(userId, accountState);

	}

	@Override
	public void freezeUsers(List<Long> userIdList, long freezeTime) {
		for(long userId : userIdList) {
			freezeUser(userId, freezeTime);
		}
	}
	
	@Override
	public void freezeUser(Long userId, long freezeTime) {
		long now = TimeUtil.currentTimeMillis();
		AccountState accountState = userRepo.getAccountState(userId);
		accountState.freeze(now, freezeTime);
		userRepo.saveAccountState(userId, accountState);
	}

	@Override
	public void unfreezeUsers(List<Long> userIdList) {
		for(long userId : userIdList) {	
			unfreezeUser(userId);
		}

	}
	
	@Override
	public void unfreezeUser(Long userId) {
		AccountState accountState = userRepo.getAccountState(userId);
		accountState.unfreeze();
		userRepo.saveAccountState(userId, accountState);
	}


	@Override
	public void freezeUser(Long userId, int abuseCount) {
		long now = TimeUtil.currentTimeMillis();
		AccountState accountState = userRepo.getAccountState(userId);
		accountState.freeze(now, abuseCount);
		userRepo.saveAccountState(userId, accountState);		
	}

	@Override
	public void refreshFreezeState(Long userId) {
		AccountState accountState = userRepo.getAccountState(userId);
	    refreshFreezeState(userId, accountState);
	}

	@Override
	public void refreshFreezeState(Long userId, AccountState accountState) {
		if(accountState != null && accountState.needUnfrozen()) {
			accountState.unfreeze();
			userRepo.saveAccountState(userId, accountState);
		}
		
	}

	@Override
	public boolean isWarningBotUser(Long userId) {
		AccountState accountState = userRepo.getAccountState(userId);
		if(accountState != null && accountState.isWarningAccount()) {
			return true;
		}
		return false;
	}
	
	

}
