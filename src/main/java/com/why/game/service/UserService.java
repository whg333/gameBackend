package com.why.game.service;

import java.util.Map;

public interface UserService {

	Map<String, Object> getUserInfo(String requestInfoStr);
	
	Map<String, Object> rename(String userIdStr, String name);
	
}
