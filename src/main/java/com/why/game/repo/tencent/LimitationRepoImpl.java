package com.why.game.repo.tencent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.hoolai.keyvalue.memcached.ExtendedMemcachedClient;
import com.schooner.MemCached.MemcachedItem;
import com.why.game.repo.LimitationRepo;
import com.why.game.util.Constant;

@Repository("limitationRepo")
public class LimitationRepoImpl implements LimitationRepo {

	@Autowired
	@Qualifier("userClient")
    private ExtendedMemcachedClient client;
	
	@Override
	public boolean addOperationTime(long userId, long value) {
		return client.add(getOperationTimeKey(userId), value);
	}

	@Override
	public MemcachedItem findOperationTime(long userId) {
		return client.gets(getOperationTimeKey(userId));
	}

	@Override
	public boolean removeOperationTime(long userId) {
		return client.delete(getOperationTimeKey(userId));
	}

	@Override
	public void saveOpenrationTime(long userId, long value) {
		client.set(getOperationTimeKey(userId), value); 
	}
	
	private static String getOperationTimeKey(long userId) {
        return new StringBuilder("ht_u").append(Constant.separator).append(userId).append("l").append(Constant.separator).append("oping").toString();
    }

}
