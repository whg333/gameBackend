package com.why.game.repo.tencent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.hoolai.exception.DataAccessException;
import com.hoolai.exception.ImproperOperationException;
import com.hoolai.keyvalue.memcached.ExtendedMemcachedClient;
import com.why.game.bo.land.Lands;
import com.why.game.repo.LandRepo;
import com.why.game.util.Constant;

@Repository("landRepo")
public class LandRepoImpl implements LandRepo {

	@Autowired
	@Qualifier("landClient")
    private ExtendedMemcachedClient client;
	
	@Override
	public void addLands(Lands lands) {
    	String landsKey = getLandsKey(lands.getUserId());
        if(!client.add(landsKey, lands.toByteArray())){
        	throw new ImproperOperationException("can not add Lands[userId="+lands.getUserId()+"] fail! try again");
        }
	}
	
	@Override
	public Lands findLands(long userId) {
		String landsKey = getLandsKey(userId);
		byte[] bytes = (byte[])client.get(landsKey);
        if (bytes == null) {
            throw new DataAccessException("userId =" + userId + ",Lands is null", "Lands");
        }
        Lands lands = new Lands(userId, bytes);
        return lands;
	}

	@Override
	public boolean saveLands(Lands lands) {
		return client.set(getLandsKey(lands.getUserId()), lands.toByteArray());
	}
	
	private static String getLandsKey(long userId) {
        return new StringBuilder().append("ht_lands").append(Constant.separator).append(userId).toString();
    }

}
