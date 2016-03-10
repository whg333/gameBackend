package com.why.game.bo.flower;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import com.hoolai.util.IntHashMap;

@Component
public class FlowerData {

	private final IntHashMap<FlowerProperty> flowerPropertyMap = new IntHashMap<FlowerProperty>();
	
	@PostConstruct
	public void init() throws DocumentException {
		try {
			initData("com/why/game/flower/flowers.xml");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
    private void initData(String path) throws DocumentException, ParseException {
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
    	InputStream input = loader.getResourceAsStream(path);
		if (input == null) {
			throw new IllegalArgumentException("找不到配置文件"+path);
		}
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(input);
		List elements = doc.getRootElement().elements();
		
		for(int i=0;i<elements.size();i++){
			Element element = (Element) elements.get(i);
			int id = Integer.parseInt(element.element("id").getText());
			int type = Integer.parseInt(element.element("type").getText());
			String name = element.element("name").getText();
			
			flowerPropertyMap.put(id, new FlowerProperty(id, type, name));
		}
    }

	public IntHashMap<FlowerProperty> getFlowerMap() {
		return flowerPropertyMap;
	}
	
}
