package com.why.game.util.exception;

public enum ErrorCode {

	CAN_NOT_OPERATING_MUTLI_THINGS(88, "不能同时进行操作"),
	
	IS_BLACK_LIST(10102, "用户在黑名单中"),
	
	USER_IS_FROZEN(10095, "用户已被冻结"),
	
	SYSTEM_ERROR(4000, "系统错误, 请刷新游戏"),
	
	SOIL_IS_OPEN(10001, "土壤已经开启");
	
	public final int code;
	public final String msg;
	
	private ErrorCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public static final int STATUS_ERROR = 1;

    public static final int STATUS_SUCCESS = 2;
    
    public static final int LOGIN_IS_EXPIRED = 888;
	
}
