package com.mobigen.snet.supportagent.main;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mobigen.snet.supportagent.component.PatchDBComponent;

public class PatchMain {

	static Logger logger = (Logger) LoggerFactory.getLogger(PatchMain.class);

	@SuppressWarnings("resource")
	public static void main(String args[]) {
		if(args.length > 0){
			ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			logger.info("Support Agent Patch DB Started.!");
			
			PatchDBComponent patchComponent = (PatchDBComponent) ctx.getBean("patchDBComponent");
			
			List<String> result = patchComponent.patch(args[0]);

		}else{
			System.out.println("Please enter arguments...");
		}
		
		System.exit(0);
	}

}
