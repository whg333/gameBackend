package com.why.game.bo.land;

import com.google.protobuf.InvalidProtocolBufferException;
import com.why.game.bo.BoProtocolBuffer.SoilProto;
import com.why.game.bo.flower.Flower;
import com.why.game.util.ProtobufSerializable;
import com.why.game.util.exception.BusinessException;
import com.why.game.util.exception.ErrorCode;

/**
 * <p>描述：土壤块，可在这上面种植花朵</p>
 * @author whg
 * @date 2016-3-8 下午03:40:26
 */
public class Soil implements ProtobufSerializable<SoilProto>{
	
	private static final int INIT_GRASS = -1;
	private static final int INIT_DRY = 60;
	private static final int INIT_FERTILE = 60;
	
	private int index;
	
	private boolean isOpen;
	
	private Flower flower;
	
	private int grass;
	private int dry;
	private int fertile;
	
	public Soil(int index){
		this.index = index;
		this.isOpen = false;
		this.flower = null;
		this.grass = INIT_GRASS;
		this.dry = INIT_DRY;
		this.fertile = INIT_FERTILE;
	}
	
	public Soil(SoilProto proto){
		copyFrom(proto);
	}
	
	public void open() {
		isOpen = true;
	}
	
	public void checkIsClose() {
		if(isOpen){
			throw new BusinessException(ErrorCode.SOIL_IS_OPEN);
		}
	}
	
	public boolean hasFlower(){
		return flower != null;
	}
	
	@Override
	public void parseFrom(byte[] bytes){
		try {
            copyFrom(SoilProto.parseFrom(bytes));
        } catch (InvalidProtocolBufferException ex) {
        	throw new IllegalArgumentException(ex);
        }
	}
	
	@Override
	public void copyFrom(SoilProto proto) {
		index = proto.getIndex();
		isOpen = proto.getIsOpen();
		if(proto.hasFlower()){
			flower = new Flower(proto.getFlower());
		}
		grass = proto.getGrass();
		dry = proto.getDry();
		fertile = proto.getFertile();
	}
	
	@Override
	public byte[] toByteArray() {
		return copyTo().toByteArray();
	}
	
	@Override
	public SoilProto copyTo() {
		SoilProto.Builder builder = SoilProto.newBuilder();
		builder.setIndex(index);
		builder.setIsOpen(isOpen);
		if(hasFlower()){
			builder.setFlower(flower.copyTo());
		}
		builder.setGrass(grass);
		builder.setDry(dry);
		builder.setFertile(fertile);
		return builder.build();
	}

	public int getIndex() {
		return index;
	}

	public boolean getIsOpen() {
		return isOpen;
	}

	public Flower getFlower() {
		return flower;
	}

	public int getGrass() {
		return grass;
	}

	public int getDry() {
		return dry;
	}

	public int getFertile() {
		return fertile;
	}
	
}
