package com.why.game.util.operation;

import com.hoolai.exception.BusinessException;
import com.hoolai.exception.ParamBusinessException;
import com.hoolai.util.AuthValidation;
import com.why.game.bo.user.AccountState;
import com.why.game.repo.UserRepo;
import com.why.game.service.AntiPluginDomainService;
import com.why.game.util.Constant;
import com.why.game.util.exception.ErrorCode;

/**
 * validate user
 * 
 * accountState, cookie, white list. etc
 * 
 * @author luzj
 * 
 */
public class UserValidation {

	private static final boolean checkIdentifyingCode = true;
	private static final boolean checkAuth = true;
	
	private long userId;

	public UserValidation(long userId) {
		this.userId = userId;
	}

	/**
	 * 检查帐号状态
	 */
	public void validateAccountState(boolean checkIdentifyingCode) {
		AccountState accountState = userRepo.getAccountState(userId);
		// 是否需要检测玩家登录的时候生产的令牌（登录的时候不需要校验）
		if(checkIdentifyingCode){
			validateIdentifyingCode(accountState);
		}
		antiPluginDomainService.refreshFreezeState(userId, accountState);

		validateFrozen(accountState);
		validateBlackList(accountState);
	}
	
	private void validateIdentifyingCode(AccountState accountState){
		String identifyingCode = HttpHeaderContext.getHeader(AccountState.IDENTIFYING_CODE_KEY);
		if(!accountState.isValidIdentifyingCode(identifyingCode)){
//			throw new ParamBusinessException(ErrorCode.CAN_NOT_REPEAT_LOGIN, "User[" + userId + "] is repeat login.",
//					accountState);
		}
	}

	private void validateBlackList(AccountState accountState) {
		if (accountState != null && accountState.getIsBlackList()) {
			throw new ParamBusinessException(ErrorCode.IS_BLACK_LIST.code, "User[" + userId + "] is in black list.",
					accountState);
		}
	}

	private void validateFrozen(AccountState accountState) {
		if (accountState != null && accountState.isFrozenAccount()) {
			throw new ParamBusinessException(ErrorCode.USER_IS_FROZEN.code, "User[" + userId + "] is frozen.", accountState);
		}
	}

	/**
	 * 检查是否在黑名单里，用于屏蔽黑名单内用户被其他人看到
	 */
	public void validateBlackList() {
		AccountState accountState = userRepo.getAccountState(userId);
		validateBlackList(accountState);
	}

	/**
	 * 检查Cookie, 白名单等状态是否合法
	 * 
	 * @param userIdStr
	 * @return
	 */
	public void validate() {
		validateWhiteList();

		validateAccountState(checkIdentifyingCode);

		validateAuth(checkAuth);
	}

	private void validateAuth(boolean checkAuth) {
	    //if(!Constant.IS_DEPLOYING) return;
		
		if(!checkAuth) return;
		
	    String authSecret = HttpHeaderContext.getHeader(AccountState.AUTH_KEY);
	    
	    if(authSecret == null){
	        throw new BusinessException(ErrorCode.LOGIN_IS_EXPIRED, AccountState.AUTH_KEY+" is null");
	    }
	    
	    if(!(new AuthValidation(userId, authSecret).isAuthValid())){
	        throw new BusinessException(ErrorCode.LOGIN_IS_EXPIRED, AccountState.AUTH_KEY+" validate fail");
	    }
    }

    public void validateWhiteList() {
		// 部署环境 只有白名单用户可以正常通讯
		if (Constant.IS_DEPLOYING && !inWhiteList(userRepo.getPlatformId(userId))) {
			throw new BusinessException(ErrorCode.LOGIN_IS_EXPIRED, "君主" + userId + "，请重新登录");
		}
	}

	public static void validateWhitList(String platformId) {
		if (Constant.IS_DEPLOYING && !inWhiteList(platformId)) {
			throw new BusinessException(ErrorCode.LOGIN_IS_EXPIRED, "君主" + platformId + "，请重新登录");
		}
	}

	/**
	 * 是否在白名单
	 * 
	 * @param platformId
	 * @return
	 */
	private static boolean inWhiteList(String platformId) {
		for (String s : Constant.WHITE_LIST) {
			if (s.equals(platformId)) {
				return true;
			}
		}
		return false;
	}

	private UserRepo userRepo;
	private AntiPluginDomainService antiPluginDomainService;

	public void setUserRepo(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	public void setAntiPluginDomainService(AntiPluginDomainService antiPluginDomainService) {
		this.antiPluginDomainService = antiPluginDomainService;
	}

}
