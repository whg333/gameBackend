package com.why.game.util.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hoolai.exception.ParamBusinessException;
import com.hoolai.exception.StackTraceUtil;
import com.hoolai.keyvalue.memcached.ExtendedMemcachedClientImpl.StatisRecord;
import com.hoolai.util.TimeUtil;
import com.hoolai.util.net.NetworkUtils;
import com.why.game.util.operation.HttpHeaderContext;
import com.why.game.util.operation.OperationContext;

/**
 * responsibility.
 * 
 * @author Joseph
 * 
 */
public class ExceptionHandler implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    public static boolean isStatistical = false;

    public static ConcurrentMap<String, AtomicLong> perInterfaceUsedTime = new ConcurrentHashMap<String, AtomicLong>();// 执行时间
    public static ConcurrentMap<String, AtomicLong> perInterfaceUsedMTime = new ConcurrentHashMap<String, AtomicLong>();// 执行时间
    public static ConcurrentMap<String, AtomicLong> perInterfaceReqCount = new ConcurrentHashMap<String, AtomicLong>();// 执行次数
    
    public static List<StatisRecord> currentStatisRecords(){
    	List<StatisRecord> statisRecords = new ArrayList<StatisRecord>();
    	if(perInterfaceReqCount.isEmpty()){
    		return Collections.emptyList();
    	}
    	for(String serviceMethodName:perInterfaceReqCount.keySet()){
    		String[] name = serviceMethodName.split("\\.");
    		long reqCount = perInterfaceReqCount.get(serviceMethodName).longValue();
    		double usedTime = perInterfaceUsedMTime.get(serviceMethodName).longValue();
    		double avgUsedTime = reqCount <= 0.0 ? 0 : usedTime/reqCount; //avoid divided by 0.0 generate NaN
    		String ip = NetworkUtils.parseServerIp();
    		StatisRecord statisRecord = new StatisRecord(name[0], name[1], reqCount, usedTime, avgUsedTime, ip);
    		statisRecords.add(statisRecord);
    	}
    	clearStatis();
    	return statisRecords;
    }
    
    private static void clearStatis(){
    	perInterfaceReqCount.clear();
    	perInterfaceUsedTime.clear();
    	perInterfaceUsedMTime.clear();
    }

    private void addStatis(String serviceMethodName, long ntime, long mtime) {
        //if(mtime > 1000000) return;
        
        AtomicLong count = perInterfaceReqCount.get(serviceMethodName);
        AtomicLong nUsedTime = perInterfaceUsedTime.get(serviceMethodName);
        AtomicLong mUsedTime = perInterfaceUsedMTime.get(serviceMethodName);

        if (count == null) {
            count = new AtomicLong(0);
            perInterfaceReqCount.putIfAbsent(serviceMethodName, count);
            nUsedTime = new AtomicLong(0);
            perInterfaceUsedTime.putIfAbsent(serviceMethodName, nUsedTime);
            mUsedTime = new AtomicLong(0);
            perInterfaceUsedMTime.putIfAbsent(serviceMethodName, mUsedTime);
        }

        count.incrementAndGet();
        nUsedTime.addAndGet(ntime);
        mUsedTime.addAndGet(mtime);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String serviceMethodName = null;
        long start = 0;
        long ms = 0;
        //logger.info("ExceptionHandler invoke...");
        if (isStatistical) {
            ms = TimeUtil.currentTimeMillis();
            start = System.nanoTime();
        }
        try {
            serviceMethodName = serviceMethodName(invocation);
            Map<String, Object> result = (Map<String, Object>) invocation.proceed();
            result.put("status", ErrorCode.STATUS_SUCCESS);
            return result;
        } catch (ParamBusinessException e) {
            int errorCode = e.getErrorCode();
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("errorcode", errorCode == 0 ? ErrorCode.SYSTEM_ERROR.code : errorCode);
            result.put("status", ErrorCode.STATUS_ERROR);
            result.put("param", e.getParam());
            logger.info(StackTraceUtil.getStackTrace(e));
            return result;
        } catch (BusinessException e) {
            int errorCode = e.getErrorCode();
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("errorcode", errorCode == 0 ? ErrorCode.SYSTEM_ERROR.code : errorCode);
            result.put("status", ErrorCode.STATUS_ERROR);
            result.put("message", e.getMessage());
            logger.info(StackTraceUtil.getStackTrace(e));
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("errorcode", ErrorCode.SYSTEM_ERROR.code);
            result.put("status", ErrorCode.STATUS_ERROR);
            result.put("message", e.getMessage());
            logger.info(StackTraceUtil.getStackTrace(e));
            return result;
        } finally {
        	OperationContext.removeOperation();
        	HttpHeaderContext.removeHeaders();
            if (isStatistical) {
                addStatis(serviceMethodName, System.nanoTime() - start, TimeUtil.currentTimeMillis() - ms);
            }
        }
    }
    
    private String serviceMethodName(MethodInvocation invocation){
    	String methodName = invocation.getMethod().getName();
        Object service = invocation.getThis();
        String serviceName = service.toString();
        serviceName = serviceName.substring(serviceName.lastIndexOf(".")+1, serviceName.indexOf("@"));
        return serviceName+"."+methodName;
    }

}
