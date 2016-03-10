package com.why.game.repo.tencent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.hoolai.exception.DataAccessException;
import com.hoolai.exception.ImproperOperationException;
import com.hoolai.keyvalue.memcached.ExtendedMemcachedClient;
import com.why.game.bo.BoFactory;
import com.why.game.bo.user.AccountState;
import com.why.game.bo.user.User;
import com.why.game.repo.UserRepo;
import com.why.game.util.Constant;
import com.why.game.util.exception.BusinessException;
import com.why.game.util.exception.ErrorCode;
import com.why.game.util.operation.Operation;
import com.why.game.util.operation.OperationContext;

@Repository("userRepo")
public class UserRepoImpl implements UserRepo {

	@Autowired
	@Qualifier("userClient")
    private ExtendedMemcachedClient client;
	
	@Autowired
	private BoFactory boFactory;
	
	@Override
	public long findUserId(String platformId) {
        String key = getUserIdKey(platformId);
        return (Long) client.get(key);
    }
	
	@Override
	public boolean isUserExist(String platformId) {
        String key = getUserIdKey(platformId);
        return client.keyExists(key);
    }
	
	@Override
    public boolean savePlatformId(long userId, String platformId) {
        String userIdKey = getUserIdKey(platformId);
        if (!client.add(userIdKey, userId)) {
            throw new ImproperOperationException("it seems the platform id already exists");
        }
        String platformIdKey = getPlatformIdKey(userId);
        return client.set(platformIdKey, platformId);
    }
	
	/**
     * platformId->userId
     *
     * @param platformId
     * @return
     */
    public static String getUserIdKey(String platformId) {
        return new StringBuilder().append("ht_p").append(Constant.separator).append(platformId).append(Constant.separator).append("uid").toString();
    }
	
	@Override
    public long getUniqueId() {
        long newId = client.justIncr(getUniqueIdKey());
        if (newId < 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 新用户
        client.auAdd(getAllUserIdKey(), newId);
        return newId;
    }
	
	private static String getUniqueIdKey() {
		return "htunid";
	}
	
	/**
     * 所有用户的Id，用于随机抽取网友
     *
     * @return
     */
    private static String getAllUserIdKey() {
        return "htau";
    }
    
	@Override
    public long checkAndConvertUserId(String userIdStr) {
        long userId = convertUserId(userIdStr);
        boFactory.createUserValidation(userId).validate();
        
        checkAndLockOperation(userId);
        return userId;
    }
	
	/**
     * 查看用户状态是否能够正常游戏
     *
     * @param userId
     */
    @Override
    public void checkAccountState(long userId) {
        boFactory.createUserValidation(userId).validateAccountState(false);
    }
	
	@Override
    public long convertUserId(String userIdStr) {
        return Long.parseLong(userIdStr);
    }
    
    @Override
    public void checkAndLockOperation(long userId) {
        Operation op = OperationContext.get();
        if(op == null){
        	op = boFactory.createOperation(userId);
        }
        op.checkAndLock();
        OperationContext.recordOperation(op);
    }
    
    @Override
    public AccountState getAccountState(long userId) {
        String key = getAccountStateKey(userId);
        Object accountState = client.get(key);

        if (accountState == null) {
            return AccountState.createDefault(userId);
        }

        if (accountState instanceof AccountState) {
            return (AccountState) accountState;
        }

        if (!(accountState instanceof byte[])) {
            return AccountState.createDefault(userId);
        }
        byte[] accountStateBytes = (byte[]) accountState;
        return new AccountState(userId,accountStateBytes);
    }
    
    @Override
    public boolean saveAccountState(long userId, AccountState accountState) {
        String key = getAccountStateKey(userId);
        return client.set(key, accountState.toByteArray());
    }
    
    private static String getAccountStateKey(long userId) {
        return new StringBuilder().append("ht_u").append(Constant.separator).append(userId).append(Constant.separator).append("as").toString();
    }
    
    @Override
    public String getPlatformId(long userId) {
        String key = getPlatformIdKey(userId);
        return (String) client.get(key);
    }

    @Override
    public String checkAndGetPlatformId(long userId) {
        String platformId = this.getPlatformId(userId);
        if (platformId.length() != 32) {
            throw new ImproperOperationException("openid len is error!");
        }
        return platformId;
    }
    
    private static String getPlatformIdKey(long userId) {
        return new StringBuffer().append("ht_u").append(Constant.separator).append(userId).append(Constant.separator).append("pid").toString();
    }
    
    @Override
    public void addRankUser(long userId, int rank) {
        client.ruAdd(getRankUserIdsKey(rank), userId);
    }
    
    /**
     * 分等级用户Id，用于随机抽取网友
     *
     * @return
     */
    private static String getRankUserIdsKey(int rank) {
        return new StringBuilder().append("r").append(Constant.separator).append(rank).append(Constant.separator).append("uids").toString();
    }
	
	@Override
	public void addUser(User user) {
    	String userKey = getUserKey(user.getId());
        if(!client.add(userKey, user.toByteArray())){
        	throw new ImproperOperationException("can not add User[id="+user.getId()+"] fail! try again");
        }
	}
	
	@Override
	public User findUser(long userId) {
		String userKey = getUserKey(userId);
        byte[] bytes = (byte[])client.get(userKey);
        if (bytes == null) {
            throw new DataAccessException("userId =" + userId + ",User is null", "User");
        }
        User user = new User(bytes);
        return user;
	}
	
	@Override
	public boolean saveUser(User user){
		return client.set(getUserKey(user.getId()), user.toByteArray());
	}
	
	private static String getUserKey(long userId) {
        return new StringBuilder().append("ht_u").append(Constant.separator).append(userId).toString();
    }

}
