package com.why.game.bo.flower;

import com.google.protobuf.InvalidProtocolBufferException;
import com.why.game.bo.BoProtocolBuffer.FlowerProto;
import com.why.game.util.ProtobufSerializable;

public class Flower implements ProtobufSerializable<FlowerProto>{

	private int id;
	
	private int xmlId;
	
	/** 成长时间 */
	private long growTime;
	
	/** 质量 */
	private int quality;
	
	/** 单价 */
	transient private int price;
	
	/** 害虫 */
	private int bug;
	
	/** 鲜花状态——阶段 */
	private int stage;
	
	public Flower(){
		
	}
	
	public Flower(FlowerProto proto){
		copyFrom(proto);
	}
	
	@Override
	public void parseFrom(byte[] bytes){
		try {
            copyFrom(FlowerProto.parseFrom(bytes));
        } catch (InvalidProtocolBufferException ex) {
        	throw new IllegalArgumentException(ex);
        }
	}
	
	@Override
	public void copyFrom(FlowerProto proto) {
		id = proto.getId();
		xmlId = proto.getXmlId();
		growTime = proto.getGrowTime();
		quality = proto.getQuality();
		bug = proto.getBug();
		stage = proto.getStage();
	}
	
	@Override
	public byte[] toByteArray() {
		return copyTo().toByteArray();
	}
	
	@Override
	public FlowerProto copyTo() {
		FlowerProto.Builder builder = FlowerProto.newBuilder();
		builder.setId(id);
		builder.setXmlId(xmlId);
		builder.setGrowTime(growTime);
		builder.setQuality(quality);
		builder.setBug(bug);
		builder.setStage(stage);
		return builder.build();
	}

	public int getXmlId() {
		return xmlId;
	}

	public long getGrowTime() {
		return growTime;
	}

	public int getQuality() {
		return quality;
	}

	public int getPrice() {
		return price;
	}

	public int getBug() {
		return bug;
	}

	public int getStage() {
		return stage;
	}
	
}
