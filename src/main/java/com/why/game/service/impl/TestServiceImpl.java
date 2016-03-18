package com.why.game.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.why.game.service.TestService;

@Service("testService")
public class TestServiceImpl implements TestService {

	@Override
	public Map<String, Object> testMap() {
		System.out.println("testMap");
		return null;
	}

	@Override
	public String testStr() {
		System.out.println("testStr");
		return null;
	}

}
