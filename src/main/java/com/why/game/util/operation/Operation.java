package com.why.game.util.operation;

import com.hoolai.util.TimeUtil;
import com.schooner.MemCached.MemcachedItem;
import com.why.game.repo.LimitationRepo;
import com.why.game.util.exception.BusinessException;
import com.why.game.util.exception.ErrorCode;

public class Operation {

    private LimitationRepo limitationRepo;
    
    private long userId;
    
    private boolean checked = false;
    
    private long currentTime = TimeUtil.currentTimeMillis();
    
    public Operation(long userId) {
        this.userId = userId;
    }
    
    public void setLimitationRepo(LimitationRepo limitationRepo) {
        this.limitationRepo = limitationRepo;
    }

    public void checkAndLock() {
        if(checked) return;
        
        if(addLockTime()) {
        	return;
        }
        
        MemcachedItem item = limitationRepo.findOperationTime(userId);
        if (item == null && addLockTime()) {
            return;
        } else if (item != null) {
                long expiretime = (Long) (item.getValue());
	        if ((currentTime > (expiretime + 1000) || (expiretime > currentTime + 1000))) {
	            limitationRepo.saveOpenrationTime(userId, currentTime);
	            return;
	        }
        }
        
        throw new BusinessException(ErrorCode.CAN_NOT_OPERATING_MUTLI_THINGS);
    }

	private boolean addLockTime() {
		boolean added = limitationRepo.addOperationTime(userId, currentTime);
        if(added){
            checked = true;
            return true;
        }
        return false;
	}

    public void ulock(){
        limitationRepo.removeOperationTime(userId);
    }
    
}
