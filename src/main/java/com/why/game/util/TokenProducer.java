package com.why.game.util;

import com.hoolai.util.StringUtil;
import com.hoolai.util.TimeUtil;

public class TokenProducer {
    private static final String produceTokenAttachStr = "             sango@hoolai.com          "; 
    
    private static final String worldBattleProduceTokenAttachStr = "           worldbattle.sango@hoolai.com          "; 
    
    private static final String worldBattleProduceTokenAttachStrV2 = "           worldbattleV2.sango@hoolai.com          "; 
    
    private static final String weixinProduceTokenAttachStr = "           weixin.sango@hoolai.com          "; 
    
    public static String produceToken(String userIdStr) {
        return StringUtil.encryptToMd5(userIdStr + produceTokenAttachStr);
    }
    
    public static String produceWorldBattleToken(String userIdStr) {
        if(userIdStr == null) {
            userIdStr = "";
        }
        return StringUtil.encryptToMd5(userIdStr + worldBattleProduceTokenAttachStr);
    }
    
    public static String produceWorldBattleTokenV2(String userIdStr) {
        if(userIdStr == null) {
            userIdStr = "";
        }
        return StringUtil.encryptToMd5(userIdStr + worldBattleProduceTokenAttachStrV2);
    }
    
    public static String produceGetUserInfoToken(String openid) {
        return StringUtil.encryptToMd5(openid + produceTokenAttachStr);
    }
    
    public static String produceLoginIdentifyingCodeToken(String userIdStr) {
        return StringUtil.encryptToMd5(userIdStr + produceTokenAttachStr + "hoolai&*^*huai~@12"+TimeUtil.currentTimeMillis());
    }
    
    public static String produceWeiXinToken(){
    	return StringUtil.encryptToMd5(weixinProduceTokenAttachStr);
    }
    
    public static void main(String[] args) {
		System.out.println(produceWeiXinToken());
	}
}
