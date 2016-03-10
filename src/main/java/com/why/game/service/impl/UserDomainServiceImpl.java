package com.why.game.service.impl;

import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.hoolai.util.AuthValidation;
import com.why.game.bo.land.Lands;
import com.why.game.bo.user.AccountState;
import com.why.game.bo.user.User;
import com.why.game.repo.LandRepo;
import com.why.game.repo.UserRepo;
import com.why.game.service.UserDomainService;

@Service("userDomainService")
public class UserDomainServiceImpl implements UserDomainService{

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private LandRepo landRepo;
	
	@Override
	public User initNewUser(long userId) {
		User user = new User(userId);
		user.setName("测试_"+new Random().nextLong());
		user.setRank(1);
		user.setGold(10000);
		
		// 第一级用户
        userRepo.addRankUser(user.getId(), user.getRank());
        userRepo.addUser(user);
		
		Lands lands = new Lands(userId);
		landRepo.addLands(lands);
		return user;
	}
	
	@Override
	public void generateTokenAndId(long userId, Map<String, Object> result){
		result.put(AccountState.AUTH_KEY, new AuthValidation(userId).currentAuth());
		
        AccountState accountState = userRepo.getAccountState(userId);
        accountState.refreshIdentifyingCode();
        Assert.isTrue(userRepo.saveAccountState(userId, accountState));
        result.put(AccountState.IDENTIFYING_CODE_KEY, accountState.getIdentifyingCode());
	}

}
