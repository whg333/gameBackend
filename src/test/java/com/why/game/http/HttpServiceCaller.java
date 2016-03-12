package com.why.game.http;

import static com.why.game.http.ParseUtil.printHex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hoolai.url.HttpService;
import com.hoolai.url.RequestProperty;
import com.hoolai.util.AuthValidation;
import com.why.game.bo.user.AccountState;
import com.why.game.response.ResponseProtocolBuffer.TestProto;

public class HttpServiceCaller {
	
	private static final long userId = 10004;
	private static final String name = userId+"_uid";
	private static final HttpService httpService = new HttpService();
	
	public static void main(String[] args) {
		//testGetUserInfo();
		testProtobuf();
	}
	
	private static void testGetUserInfo(){
		final RequestProperty authSecret = new RequestProperty(AccountState.AUTH_KEY, new AuthValidation(userId).currentAuth());
		System.out.println(authSecret.getValue());
		final RequestProperty idf_c_key = new RequestProperty(AccountState.IDENTIFYING_CODE_KEY, "");
		
//		String url = "http://192.168.90.10:8077/huaTeng/userController/getUserInfo.ht?requestInfoStr={\"openid\":\"whg2\"}";
//		String response = httpService.doGet(url, authSecret, idf_c_key);
//		System.out.println(response);
		
		String url = "http://192.168.90.10:8080/huaTeng/userController/rename.ht?userIdStr=10004&name=whg10004";
//		String response = httpService.doGet(url, authSecret, idf_c_key);
//		System.out.println(response);
		
		new HttpServiceThread(url, authSecret, idf_c_key).start();
		new HttpServiceThread(url, authSecret, idf_c_key).start();
	}
	
	public static void testProtobuf() {
//		final RequestProperty contentType = new RequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//		final RequestProperty accept = new RequestProperty("Accept", "application/octet-stream");
//		
//		String url = "http://192.168.90.10:8077/huaTeng/testController/protobuf.ht?userIdStr=1234";
//		
//		new HttpServiceThread(url, contentType, accept).start();
		post();
		post2();
	}
	
	/** 具体的httpclient的使用请参见http://blog.csdn.net/wangpeng047/article/details/19624529 */
	private static void post() {  
        // 创建默认的httpClient实例.    
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // 创建httppost    
        HttpPost httppost = new HttpPost("http://192.168.90.10:8080/huaTeng/testController/protobuf.proto");  
        // 创建参数队列    
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        formparams.add(new BasicNameValuePair("userIdStr", "1234"));  
        UrlEncodedFormEntity uefEntity;  
        try {  
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            System.out.println("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity entity = response.getEntity();  
                
                byte[] bytes = EntityUtils.toByteArray(entity);
                printHex(bytes);
                TestProto newTestProto = newTestProto(name+"-111");
    			byte[] bytes2 = newTestProto.toByteArray();
    			printHex(bytes2);
    			
    			System.out.println();
    			printTestProto("testProto=", bytes2);
    			printTestProto("response=", bytes);
                
//                if (entity != null) {  
//                    System.out.println("--------------------------------------");  
//                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));  
//                    System.out.println("--------------------------------------");  
//                }  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }
	
	private static void post2() {  
        // 创建默认的httpClient实例.    
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // 创建httppost    
        HttpPost httppost = new HttpPost("http://192.168.90.10:8080/huaTeng/testController/protobuf2.proto");  
        //httppost.addHeader("Content-Type","application/x-protobuf");
        httppost.addHeader("Accept", "application/x-protobuf");
        // 创建参数队列    
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        formparams.add(new BasicNameValuePair("userIdStr", "1234"));  
        UrlEncodedFormEntity uefEntity;  
        try {  
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            System.out.println("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity entity = response.getEntity();  
                
                byte[] bytes = EntityUtils.toByteArray(entity);
                printHex(bytes);
                TestProto newTestProto = newTestProto(name+"-222");
    			byte[] bytes2 = newTestProto.toByteArray();
    			printHex(bytes2);
    			
    			System.out.println();
    			printTestProto("testProto=", bytes2);
    			printTestProto("response=", bytes);
                
//                if (entity != null) {  
//                    System.out.println("--------------------------------------");  
//                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));  
//                    System.out.println("--------------------------------------");  
//                }  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
	
	private static class HttpServiceThread extends Thread{
		
		private final String url;
		private final RequestProperty authSecret;
		private final RequestProperty idf_c_key;
		
		public HttpServiceThread(String url, RequestProperty authSecret, RequestProperty idfCKey) {
			this.url = url;
			this.authSecret = authSecret;
			this.idf_c_key = idfCKey;
		}
		
		public void run() {
			String response = httpService.doGet(url, authSecret, idf_c_key);
			System.out.println(response);
			System.out.println();
			
			TestProto newTestProto = newTestProto(name);
			byte[] bytes2 = newTestProto.toByteArray();
			printHex(bytes2);
			
			String response2 = new String(bytes2);
			printHex(response2.getBytes());
			printTestProto("String testProto2=", response2.getBytes());
			
			printProtoStr(response2);
			printTestProto("testProto2=", newTestProto.toByteArray());
			
			System.out.println();
			printHex(response.getBytes());
			printProtoStr(response);
			
			printTestProto("testProto=", response.getBytes());
		}
	}
	
	private static void printTestProto(String s, byte[] bytes){
		TestProto testProto = null;
		try {
			testProto = TestProto.parseFrom(bytes);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		if(testProto != null){
			System.out.println(s+testProto);
			System.out.println(testProto.getId());
		}
	}
	
	public static TestProto newTestProto(String name){
		TestProto.Builder builder = TestProto.newBuilder();
		builder.setId(414748264923L);
		builder.setName(name);
		builder.setRank(512);
		builder.setGold(1078);
		builder.setExp(999);
		builder.setDiamond(1024);
		return builder.build();
	}
	
	public static void printProtoStr(String s){
		List<Byte> l = new ArrayList<Byte>();
		for(int i=0;i<s.length();i++){
			l.add((byte)s.charAt(i));
		}
		System.out.println(l);
	}
	
}
