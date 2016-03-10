package com.why.game.util.operation;

import java.util.Map;

public class HttpHeaderContext {

	private static ThreadLocal<Map<String, String>> httpHeaders = new ThreadLocal<Map<String, String>>();

	public static String getHeader(String key) {
		Map<String, String> httpHeaderMap = httpHeaders.get();
		if(httpHeaderMap == null) return null;
		
		return httpHeaderMap.get(key);
	}
	
	public static void addHeaders(Map<String, String> httpHeaderMap){
		//System.out.println(Thread.currentThread().getName()+" addHeaders...");
		httpHeaders.set(httpHeaderMap);
	}
	
	public static void removeHeaders(){
		if(httpHeaders.get() == null){
			return;
		}
		
		httpHeaders.remove();
		//System.out.println(Thread.currentThread().getName()+" removeHeaders...");
	}
	
}
