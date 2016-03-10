package com.why.game.bo.user;

import com.google.protobuf.InvalidProtocolBufferException;
import com.why.game.bo.BoProtocolBuffer.UserProto;
import com.why.game.util.ProtobufSerializable;
import com.why.game.util.exception.BusinessException;
import com.why.game.util.exception.ErrorCode;

public class User implements ProtobufSerializable<UserProto>{

	private long id;
	
	//@JsonIgnore
	private String name;
	
	private int rank;
	
	private int gold;
	
	public User(long id) {
		this.id = id;
	}
	
	public User(byte[] bytes){
		parseFrom(bytes);
	}
	
	@Override
	public void parseFrom(byte[] bytes){
		try {
            copyFrom(UserProto.parseFrom(bytes));
        } catch (InvalidProtocolBufferException ex) {
        	throw new IllegalArgumentException(ex);
        }
	}
	
	@Override
	public void copyFrom(UserProto proto) {
		id = proto.getId();
		name = proto.getName();
		rank = proto.getRank();
		gold = proto.getGold();
	}
	
	@Override
	public byte[] toByteArray() {
		return copyTo().toByteArray();
	}
	
	@Override
	public UserProto copyTo() {
		UserProto.Builder builder = UserProto.newBuilder();
		builder.setId(id);
		builder.setName(name);
		builder.setRank(rank);
		builder.setGold(gold);
		return builder.build();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}
	
	public void testException(){
		throw new BusinessException(ErrorCode.SOIL_IS_OPEN);
	}
	
}
