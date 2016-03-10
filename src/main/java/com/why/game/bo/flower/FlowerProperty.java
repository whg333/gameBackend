package com.why.game.bo.flower;

public class FlowerProperty {

	private final int xmlId;
	private final int type;
	private final String name;
	
	public FlowerProperty(int xmlId, int type, String name) {
		super();
		this.xmlId = xmlId;
		this.type = type;
		this.name = name;
	}
	
	public int getXmlId() {
		return xmlId;
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
}
