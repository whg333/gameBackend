package com.why.game.util;

import java.io.IOException;
import java.util.Properties;

import com.hoolai.platform.PlatformType;

/**
 * 项目中用到的跟平台或环境相关的一些常量
 * 
 * @author luzj
 * 
 */
public class Constant {
    
    public static boolean IS_STATISTIC = false;

    public static int SERVER_ID;
    
    public static String CURRENT_SERVER_URL;
    
    public static String[] SERVER_URL_LIST;
    
    /** 平台相关常量 */
    public static String API_KEY;

    public static String SECRET_KEY;

    public static String RESOURCE_URL;

    public static String CANVAS_URL;

    public static String BBS_URL;
    
    public static String API_PREFIX;

    public static String API_URL;
    
    public static String PAY_URL;

    public static boolean LOAD_PLATFORM_INFO = false;

    public static String PLATFORM_NAME;

    public static int PLATFORM_TYPE;

    public static PlatformType platformType;
    
    public static int APP_ID;

    public static String APP_NAME;

    public static boolean IS_DEPLOYING = false;

    public static String[] WHITE_LIST;

    public static String LAN_SUFFIX = "zh_cn";// 默认为中文

    /** 反外挂ID */
    public static boolean ANTI_PLUG_OPEN = false;
    
    public static long LOG_ID;

    public static long MODEL_LOG_ID;

    public static boolean MODEL_CALL_OPEN = false;

    public static int NPC_ID = -100;

    public static int NPC_OFFICER_ID = -100;

    public static String NPC_PLATFORM_ID = "NPC-100";

    public static String separator = "_";

    public static boolean IS_TENCENT = false;

    public static boolean JSON_BROKER_ENABLE = false;
    
    public static boolean IS_TEST_SERVER = false;
    
    public static boolean IS_OPEN_UNIONTASK = true;
    
    public static boolean IS_OPEN_ROBMINE_TASK = true;
    
    public static String TRACK_SN_ID;
    
    public static String TRACK_CLIENT_ID;
    
    public static String TRACK_GAME_ID;
    public static String TRACK_SCRIBED_HOST;
    
    public static int CORE_POOL_SIZE;
    public static int MAX_POOL_SIZE;
    public static int QUEUE_CAPACITY;
    
    
    public static String TRACK_EXPEDITION_SN_ID;
    public static String TRACK_EXPEDITION_CLIENT_ID;
    public static String TRACK_EXPEDITION_GAME_ID;
    public static String TRACK_EXPEDITION_SCRIBED_HOST;
    public static int CORE_EXPEDITION_POOL_SIZE;
    public static int MAX_POOL_SIZE_EXPEDITION;
    public static int QUEUE_CAPACITY_EXPEDITION;
    
    public static String TENCENT_ACTIVITY_ID;
    
    public static boolean IS_OPEN_EXPEDITIONROAD_TRACKING = false;
    
    public static final boolean IS_OPEN_MATRIX_DEVELOP = true;
    
    public static final boolean IS_OPEN_ARTIFACT = true;
    
    /**是否开启士兵的阵法功能*/
    public static boolean IS_OPEN_SOLDIER_MATRIX = true;
    
    /** 是否使用DB测试代理，即仅读取数据有效，修改数据无效 */
    public static boolean IS_DB_TEST_PROXY;
    
    static {
        try {
            Properties p = new Properties();
            loadPlatformProperties(p);
            loadDBProperties(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadPlatformProperties(Properties p) throws IOException{
    	p.load(Constant.class.getClassLoader().getResourceAsStream("constant_file.properties"));
        String filePath = p.getProperty("file_path");
        p.clear();
        p.load(Constant.class.getClassLoader().getResourceAsStream(filePath));

        // 调用API的接口前缀名
        API_PREFIX = p.getProperty("api_prefix");
        // 决定index.jsp include的目录 以及 platform_type
        PLATFORM_NAME = p.getProperty("platform_name");

        PLATFORM_TYPE = ("qzone").equals(PLATFORM_NAME) ? 1 : (("kaixin").equals(PLATFORM_NAME) ? 2 : ("manyou".equals(PLATFORM_NAME) ? 3 : "qplus".equals(PLATFORM_NAME)? 4 : "android".equals(PLATFORM_NAME) ? 5 : 0));

        // 传递给前端 请求的服务器地址
        SERVER_URL_LIST = p.getProperty("server_urls").split(",");    
        SERVER_ID = Integer.parseInt(p.getProperty("server_id"));      
        CURRENT_SERVER_URL = SERVER_URL_LIST[SERVER_ID];
        
        // 下载前端文件和images的url
        RESOURCE_URL = p.getProperty("resource_url");

        PAY_URL = p.getProperty("pay_url");
        
        // 是否开启维护 default=false
        if (p.containsKey("deploy_env")) {
            IS_DEPLOYING = Boolean.parseBoolean(p.getProperty("deploy_env"));
        }

        if(p.containsKey("is_statistic")) {
            IS_STATISTIC = Boolean.parseBoolean(p.getProperty("is_statistic"));
        }
        
        // 是否同步平台信息 defalut=false
        if (p.containsKey("load_platform_info")) {
            LOAD_PLATFORM_INFO = Boolean.parseBoolean(p.getProperty("load_platform_info"));
        }

        if (p.containsKey("app_id")) {
            APP_ID = Integer.parseInt(p.getProperty("app_id"));
        }

        
        API_KEY = p.getProperty("api_key");
        SECRET_KEY = p.getProperty("secret_key");
        CANVAS_URL = p.getProperty("canvas_url");
        BBS_URL = p.getProperty("bbs_url");
        API_URL = p.getProperty("api_url");
        APP_NAME = p.getProperty("app_name");

        // 是否报送模块调用 default=false
        if (p.containsKey("model_call_open")) {
            MODEL_CALL_OPEN = Boolean.parseBoolean(p.getProperty("model_call_open"));
        }

        if (p.containsKey("log_id")) {
            LOG_ID = Long.parseLong(p.getProperty("log_id"));
        }

        if (p.containsKey("model_call_log_id")) {
            MODEL_LOG_ID = Long.parseLong(p.getProperty("model_call_log_id"));
        }

        if (p.containsKey("lan_suffix")) {
            LAN_SUFFIX = p.getProperty("lan_suffix");
        }

        IS_TENCENT = "qzone".equals(PLATFORM_NAME) || "pengyou".equals(PLATFORM_NAME) || "kaixin".equals(PLATFORM_NAME) || "manyou".equals(PLATFORM_NAME)|| "qplus".equals(PLATFORM_NAME) || "android".equals(PLATFORM_NAME);

        platformType = PlatformType.valueOf(p.getProperty("platform_sdk_type").toUpperCase());
    }
    
    public static String serverName(int serverId){
		switch(serverId){
		case 0:
			return "成就服";
		case 1:
			return "开心服";
		case 2:
			return "征战服";
		case 3:
			return "奇迹服";
		case 4:
			return "荣耀服";
		case 5:
			return "世界杯服";
		}
		return "未知";
	}

	public static void loadDBProperties(Properties p) throws IOException{
    	p.load(Constant.class.getClassLoader().getResourceAsStream("constant_file.properties"));
        String dbFilePath = p.getProperty("db_file_path");
    	p.clear();
    	p.load(Constant.class.getClassLoader().getResourceAsStream(dbFilePath));
    	String dbClient = p.getProperty("db_client");
    	IS_DB_TEST_PROXY = "com.hoolai.sango.repo.ExtendedMemcachedTestClientImpl".equals(dbClient);
    }
    
    public static boolean isUsedCMEMStorage(){
    	return Constant.SERVER_ID == 0 || Constant.SERVER_ID == 1;
    }
    
	public static boolean isLocalPlatform() {
		return platformType == PlatformType.LOCAL;
	}

}
