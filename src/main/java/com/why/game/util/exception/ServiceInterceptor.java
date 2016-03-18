package com.why.game.util.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
 * <p>
 * 描述：把之前基于xml配置的ExceptionHandler改为使用@Aspect注解形式的Spring AOP。<br>
 * 如此一来在@Around里面配置了只针对XxxService的public方法且返回值为Map<String, Object>的作切面拦截，并过滤掉XxxDomainService的方法
 * </p>
 * @author whg
 * @date 2016-3-18 下午02:57:32
 */
@Aspect
public class ServiceInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(ServiceInterceptor.class);

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
	
	@SuppressWarnings("unchecked")
	@Around("execution(public java.util.Map<String, Object> com.why.game.service.impl.*.*(..)) and !execution(* com.why.game.service.impl.*DomainService*.*(..))")
	public Object around(ProceedingJoinPoint invocation) throws Throwable{
		String serviceMethodName = null;
        long start = 0;
        long ms = 0;
        logger.info("ServiceInterceptor around...");
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
	
	private String serviceMethodName(ProceedingJoinPoint invocation){
    	String methodName = invocation.getSignature().getName();
        Object service = invocation.getThis();
        String serviceName = service.toString();
        serviceName = serviceName.substring(serviceName.lastIndexOf(".")+1, serviceName.indexOf("@"));
        return serviceName+"."+methodName;
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
	
}
