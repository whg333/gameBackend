package com.why.game.bo.user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hoolai.exception.ImproperOperationException;
import com.hoolai.util.TimeUtil;
import com.why.game.bo.BoProtocolBuffer.AccountStateProto;
import com.why.game.util.ProtobufSerializable;
import com.why.game.util.TokenProducer;

/**
 * 账户状态
 * 
 */
public class AccountState implements ProtobufSerializable<AccountStateProto> {

	private static final int[] freezeHours = {0, 12, 12, 12, 12, 24, 72, 168, 360, 720};
	
	public static  final int maxWarningCount = 3;
	
	public static String AUTH_KEY = "ht_auth_secret";
	
	public static String IDENTIFYING_CODE_KEY = "ht_idf_c_key";
	
	transient private long userId;
	
    private boolean isFrozen;

    private long unfreezeTime;

    private long frozenTime;

    /** 黑名单 */
    private boolean isBlackList;

    transient int hours;

    // for idle user.
    // private long lastLoginTime;

    private int abuseCount;

    private int lastAbuseMinute;

    private boolean isRead;
    
    // 玩家每次登录，生产唯一的值，每次调用业务方法的时候。校验
    private String identifyingCode;

    public static AccountState createDefault(long userId) {
        AccountState one = new AccountState(userId);
        one.setAbuseCount(0);
        one.setBlackList(false);
        one.setFrozenTime(-1);
        one.setUnfreezeTime(-1);
        one.setHours(0);
        one.setIsFrozen(false);
        one.setIsRead(false);

        return one;
    }

    /**
     * 滥用外挂一次，记录次数，惩罚 FIXME：如有其他非冻结的惩罚措施，应将惩罚内容传出去处理
     * 
     * @param 如进一步惩罚
     *            ，true； 如忽略，false
     */
    public boolean abuseOnce(long now) {
        if (ignoreInShortTime(now)) {
            return false;
        }

        this.abuseCount += 1;
        return true;
    }

    /**
     * 是否忽略
     * 
     * @param now
     * @return
     */
    private boolean ignoreInShortTime(long now) {
        if (abuseCount == 1 && lastAbuseMinute <= TimeUnit.MILLISECONDS.toMinutes(now) - 60) {
            return true;
        }
        if ((abuseCount == 4 || abuseCount == 5 || abuseCount == 6)
                && lastAbuseMinute <= TimeUnit.MILLISECONDS.toMinutes(now) + 30) {
            return true;
        }
        return false;
    }
    
    public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
	public boolean isFrozenAccount() {
		return isFrozen && unfreezeTime > TimeUtil.currentTimeMillis() && abuseCount > maxWarningCount;
	}
	
	public boolean isWarningAccount(){
		return isFrozen && unfreezeTime > TimeUtil.currentTimeMillis() && abuseCount <= maxWarningCount;

	}
	
	public boolean needUnfrozen(){
		return isFrozen && unfreezeTime < TimeUtil.currentTimeMillis();
	} 

    public int getAbuseCount() {
        return abuseCount;
    }
	public void freeze(long beginMillis, long lastTimeAsMillis) {
		this.setFrozenTime(beginMillis);
		this.setIsFrozen(true);
		this.setUnfreezeTime(beginMillis + lastTimeAsMillis);
	}
	
	public void freeze(long beginMillis, int abuseCount) {
		int maxFreezeIndex = freezeHours.length - 1;
	
		if(abuseCount < 1) {
			throw new  ImproperOperationException("abuseCount is invalid!");
		}
		
		int freezeIndex = abuseCount > maxFreezeIndex ? maxFreezeIndex : abuseCount;
		int hours = freezeHours[freezeIndex];
		long lastTimeAsMillis = TimeUnit.HOURS.toMillis(hours);
		freeze(beginMillis, lastTimeAsMillis);
		this.setAbuseCount(abuseCount);
	}
	
	public void unfreeze() {
		this.setFrozenTime(0);
		this.setIsFrozen(false);
		this.setUnfreezeTime(0);
		this.setAbuseCount(0);
	}

    public void setAbuseCount(int abuseCount) {
        if (abuseCount < 0) {
            throw new ImproperOperationException("abuseCount should not less than 0,");
        }
        this.abuseCount = abuseCount;
    }

    public int getLastAbuseMinute() {
        return lastAbuseMinute;
    }

    public void setLastAbuseMinute(int lastAbuseMinute) {
        this.lastAbuseMinute = lastAbuseMinute;
    }

    public void setIsBlackList(boolean isBlackList) {
        this.isBlackList = isBlackList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AccountState)) {
            return false;
        }
        AccountState another = (AccountState) obj;

        return this.isRead == another.isRead && this.abuseCount == another.abuseCount
                && this.isFrozen == another.isFrozen && this.isBlackList == another.isBlackList
                && this.hours == another.hours && this.frozenTime == another.frozenTime
                && this.unfreezeTime == another.unfreezeTime;
    }

    public int getHours() {
        return hours;
    }

    public boolean getIsBlackList() {
        return isBlackList;
    }

    public void setBlackList(boolean isBlackList) {
        this.isBlackList = isBlackList;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public boolean getIsFrozen() {
        return isFrozen;
    }

    public long getFrozenTime() {
        return frozenTime;
    }

    public void setFrozenTime(long frozenTime) {
        this.frozenTime = frozenTime;
        initHours();
    }

    public void setIsFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public long getUnfreezeTime() {
        return unfreezeTime;
    }

    public void setUnfreezeTime(long unfreezeTime) {
        this.unfreezeTime = unfreezeTime;
        initHours();
    }

    public boolean isFrozen() {
        return isFrozen && unfreezeTime > TimeUtil.currentTimeMillis();
    }

    public String getIdentifyingCode() {
		return identifyingCode;
	}

    // 只允许在登录的时候调用（getUserInfo）
	public void refreshIdentifyingCode() {
		String tempCode=TokenProducer.produceLoginIdentifyingCodeToken(this.userId+"");
		this.identifyingCode = tempCode;
	}
	
	public boolean isValidIdentifyingCode(String identifyingCode){
		if(this.identifyingCode==null||identifyingCode==null){
			return false;
		}
		return this.identifyingCode.equals(identifyingCode);
	}

	@Override
    public String toString() {
        return "AccountState [abuseCount=" + abuseCount + ", frozenTime=" + frozenTime + ", hours=" + hours
                + ", isBlackList=" + isBlackList + ", isFrozen=" + isFrozen + ", isRead=" + isRead
                + ", lastAbuseMinute=" + lastAbuseMinute + ", unfreezeTime=" + unfreezeTime + "]";
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeBoolean(this.isFrozen);
        stream.writeLong(this.unfreezeTime);
        stream.writeLong(this.frozenTime);
        stream.writeBoolean(this.isBlackList);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        this.isFrozen = stream.readBoolean();
        this.unfreezeTime = stream.readLong();
        this.frozenTime = stream.readLong();
        this.isBlackList = stream.readBoolean();
        initHours();
    }

    private void initHours() {
        this.hours = (int) TimeUnit.MILLISECONDS.toHours(this.unfreezeTime - this.frozenTime);
    }

    public AccountStateProto copyTo() {
    	AccountStateProto.Builder protoBuilder = AccountStateProto.newBuilder();
        protoBuilder.setFrozenTime((getFrozenTime()));
        protoBuilder.setIsFrozen((getIsFrozen()));
        protoBuilder.setUnfreezeTime((getUnfreezeTime()));
        protoBuilder.setAbuseCount((getAbuseCount()));
        protoBuilder.setIsRead((getIsRead()));
        protoBuilder.setLastAbuseMinute((getLastAbuseMinute()));
        protoBuilder.setIsBlackList((getIsBlackList()));
        protoBuilder.setIdentifyingCode(getIdentifyingCode());
        return protoBuilder.build();
    }

    public void copyFrom(AccountStateProto proto) {
        setFrozenTime((proto.getFrozenTime()));
        setIsFrozen((proto.getIsFrozen()));
        setUnfreezeTime((proto.getUnfreezeTime()));
        setAbuseCount((proto.getAbuseCount()));
        setIsRead((proto.getIsRead()));
        setLastAbuseMinute((proto.getLastAbuseMinute()));
        setIsBlackList((proto.getIsBlackList()));
        this.identifyingCode=proto.getIdentifyingCode();
        afterCopyFrom();
    }

    private void afterCopyFrom() {
        initHours();
    }

    public byte[] toByteArray() {
        return copyTo().toByteArray();
    }

    public void parseFrom(byte[] bytes) {
        try {
            copyFrom(AccountStateProto.parseFrom(bytes));
        } catch (InvalidProtocolBufferException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public AccountState(long userId) {
    	this.userId=userId;
    }

    public AccountState(long userId,byte[] bytes) {
        this(userId);
        parseFrom(bytes);
    }

}
