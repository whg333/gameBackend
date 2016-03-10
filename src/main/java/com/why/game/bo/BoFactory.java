package com.why.game.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.why.game.repo.LimitationRepo;
import com.why.game.repo.UserRepo;
import com.why.game.service.AntiPluginDomainService;
import com.why.game.util.operation.Operation;
import com.why.game.util.operation.UserValidation;

@Component
public class BoFactory {

	@Autowired
    private UserRepo userRepo;
	
	@Autowired
	private LimitationRepo limitationRepo;
	
	@Autowired
    private AntiPluginDomainService antiPluginDomainService;
	
	public UserValidation createUserValidation(long userId) {
        UserValidation userValidation = new UserValidation(userId);
        userValidation.setUserRepo(userRepo);
        userValidation.setAntiPluginDomainService(antiPluginDomainService);
        return userValidation;
    }
	
	public Operation createOperation(long userId) {
        Operation op = new Operation(userId);
        op.setLimitationRepo(limitationRepo);
        return op;
    }
	
}
