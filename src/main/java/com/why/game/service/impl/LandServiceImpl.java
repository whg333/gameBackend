package com.why.game.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.why.game.bo.user.User;
import com.why.game.repo.UserRepo;
import com.why.game.service.LandService;

@Service("landService")
public class LandServiceImpl implements LandService{

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public Map<String, Object> test(String userIdStr) {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = userRepo.findUser(Long.parseLong(userIdStr));
		result.put("user", user);
		user.testException();
		return result;
	}

}
