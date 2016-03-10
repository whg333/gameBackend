package com.why.game.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.dom4j.DocumentException;
import org.springframework.context.ApplicationContext;

import com.why.game.bo.flower.FlowerData;

public class BootStrap {

    private ApplicationContext ac;

    private String contextPath;

    public BootStrap(ApplicationContext ac, String contextPath) {
        this.ac = ac;
        this.contextPath = contextPath;
    }

    public void startServer() {
        try {
            loadStaticData();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } 
    }

    private void loadNativeComponent() {
        if (Constant.LOAD_PLATFORM_INFO && Constant.ANTI_PLUG_OPEN) {
            try {
                System.load(contextPath + "WEB-INF/jni_lib/libantiPlugin.so");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadStaticData() throws DocumentException, ParseException, IOException, ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream officerDataInput = loader.getResourceAsStream("com/hoolai/huaTeng/flower/flowers.xml");

        FlowerData flowerData = (FlowerData) ac.getBean("flowerData");
        //officerData.init(officerDataInput);
        //System.out.println(flowerData.getFlowerMap().size());
        loadNativeComponent();
        
    }

}
