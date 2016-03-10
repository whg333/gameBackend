package com.why.game.bo.user;

import org.compass.core.util.Assert;

import com.hoolai.platform.RequestInfo;
import com.hoolai.platform.RequestInfoFactory;
import com.why.game.repo.UserRepo;
import com.why.game.service.UserDomainService;
import com.why.game.service.UserService;
import com.why.game.util.Constant;

public class UserLoginProcessor {

	private final String requestInfoStr;
	
	private final UserRepo userRepo;
	
	private final UserDomainService userDomainService;
	
	public UserLoginProcessor(String requestInfoStr, UserRepo userRepo, UserDomainService userDomainService) {
		this.requestInfoStr = requestInfoStr;
		this.userRepo = userRepo;
		this.userDomainService = userDomainService;
	}

	public User login(){
		RequestInfo requestInfo = RequestInfoFactory.parseRequestInfo(Constant.platformType, requestInfoStr);
		String openid = requestInfo.findPid();
		boolean isNewUser = !userRepo.isUserExist(openid);
		long userId;
		User user;
		if(isNewUser){
			userId = userRepo.getUniqueId();
			Assert.isTrue(userRepo.savePlatformId(userId, requestInfo.findPid()));	// 建立platformID 与  useId的双向索引)
			user = userDomainService.initNewUser(userId);
		}else{
			userId = userRepo.findUserId(openid);
			userRepo.checkAccountState(userId);
			user = userRepo.findUser(userId);
		}
		return user;
	}
	
}
