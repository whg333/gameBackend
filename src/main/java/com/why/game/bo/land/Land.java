package com.why.game.bo.land;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.google.protobuf.InvalidProtocolBufferException;
import com.why.game.bo.BoProtocolBuffer.LandProto;
import com.why.game.util.ProtobufSerializable;

/**
 * <p>描述：土地块（可认为是一亩），里面包括3*6=18个土壤块，可以在土壤块上种植花朵</p>
 * @author whg
 * @date 2016-3-8 下午03:39:13
 */
public class Land implements ProtobufSerializable<LandProto>{

	private final int ROW = 3;
	private final int COLUMN = 6;
	
	private final int INIT_OPEN_COUNT = 6;
	
	private final int CLOSE_INDEX = -1;
	
	private int index = CLOSE_INDEX;
	private List<Soil> soilList;
	
	public Land(int index){
		this.index = index;
		this.soilList = new ArrayList<Soil>(ROW * COLUMN);
		for(int i=0;i<ROW * COLUMN;i++){
			soilList.add(new Soil(i));
		}
	}
	
	public void initOpenCount(){
		for(int i=0;i<INIT_OPEN_COUNT;i++){
			soilList.get(i).open();
		}
	}
	
	public Land(LandProto proto){
		copyFrom(proto);
	}
	
	/** 开启索引为index的土壤 */
	public void checkAndOpenSoil(int index){
		Assert.isTrue(soilList.size() < ROW * COLUMN);
		
		Soil soil = soilList.get(index);
		soil.checkIsClose();
		soil.open();
	}
	
	@Override
	public void parseFrom(byte[] bytes){
		try {
            copyFrom(LandProto.parseFrom(bytes));
        } catch (InvalidProtocolBufferException ex) {
        	throw new IllegalArgumentException(ex);
        }
	}
	
	@Override
	public void copyFrom(LandProto proto) {
		index = proto.getIndex();
		
		int soilListCount = proto.getSoilListCount();
		soilList = new ArrayList<Soil>();
		for(int i=0;i<soilListCount;i++){
			soilList.add(new Soil(proto.getSoilList(i)));
		}
	}
	
	@Override
	public byte[] toByteArray() {
		return copyTo().toByteArray();
	}
	
	@Override
	public LandProto copyTo() {
		LandProto.Builder builder = LandProto.newBuilder();
		builder.setIndex(index);
		for(int i=0;i<soilList.size();i++){
			builder.addSoilList(i, soilList.get(i).copyTo());
		}
		return builder.build();
	}

	public int getIndex() {
		return index;
	}

	public List<Soil> getSoilList() {
		return soilList;
	}
	
}
