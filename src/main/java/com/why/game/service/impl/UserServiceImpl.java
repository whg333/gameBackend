package com.why.game.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.why.game.bo.user.User;
import com.why.game.bo.user.UserLoginProcessor;
import com.why.game.repo.LandRepo;
import com.why.game.repo.UserRepo;
import com.why.game.service.UserDomainService;
import com.why.game.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private LandRepo landRepo;
	
	@Autowired
	private UserDomainService userDomainService;
	
	@Override
	public Map<String, Object> getUserInfo(String requestInfoStr) {
		User user = new UserLoginProcessor(requestInfoStr, userRepo, userDomainService).login();
		long userId = user.getId();
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("user", user);
		result.put("lands", landRepo.findLands(userId));
		
		//user.testException();
		userDomainService.generateTokenAndId(userId, result);
		
		return result;
	}

	@Override
	public Map<String, Object> rename(String userIdStr, String name) {
		long userId = userRepo.checkAndConvertUserId(userIdStr);
		Map<String, Object> result = new HashMap<String, Object>();
		User user = userRepo.findUser(userId);
		user.setName(name);
		userRepo.saveUser(user);
		result.put("user", user);
		return result;
	}

}
