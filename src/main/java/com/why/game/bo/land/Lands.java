package com.why.game.bo.land;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.why.game.bo.BoProtocolBuffer.LandsProto;
import com.why.game.util.ProtobufSerializable;

/**
 * <p>描述：代表主界面的土地集合，每个土地集合里面又包含若干块土地块（可认为是一亩）</p>
 * @author whg
 * @date 2016-3-8 下午03:37:52
 */
public class Lands implements ProtobufSerializable<LandsProto>{

	transient private final long userId;
	
	private List<Land> landList;
	
	public Lands(long userId){
		this.userId = userId;
		this.landList = new ArrayList<Land>();
		Land land = new Land(landList.size());
		land.initOpenCount();
		this.landList.add(land);
	}
	
	public Lands(long userId, byte[] bytes){
		this.userId = userId;
		parseFrom(bytes);
	}
	
	public void addNewLand(){
		this.landList.add(new Land(landList.size()));
	}

	@Override
	public void parseFrom(byte[] bytes){
		try {
            copyFrom(LandsProto.parseFrom(bytes));
        } catch (InvalidProtocolBufferException ex) {
        	throw new IllegalArgumentException(ex);
        }
	}
	
	@Override
	public void copyFrom(LandsProto proto) {
		int landListCount = proto.getLandListCount();
		landList = new ArrayList<Land>();
		for(int i=0;i<landListCount;i++){
			landList.add(new Land(proto.getLandList(i)));
		}
	}
	
	@Override
	public byte[] toByteArray() {
		return copyTo().toByteArray();
	}
	
	@Override
	public LandsProto copyTo() {
		LandsProto.Builder builder = LandsProto.newBuilder();
		for(int i=0;i<landList.size();i++){
			builder.addLandList(i, landList.get(i).copyTo());
		}
		return builder.build();
	}
	
	public long getUserId() {
		return userId;
	}
	
	public List<Land> getLandList() {
		return landList;
	}
	
}
