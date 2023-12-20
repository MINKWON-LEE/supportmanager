/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.main.main.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2016. 2. 3.
 * description : 
 */

package com.mobigen.snet.supportagent.main;


import com.igloosec.smartguard.next.agentmanager.memory.INMEMORYDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	static Logger logger = (Logger) LoggerFactory.getLogger(Main.class);

	public static void main(String args[]) {
		@SuppressWarnings({ "unused", "resource" })
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:applicationContext.xml");

		INMEMORYDB memory = new INMEMORYDB();
		memory.init();

		logger.info("*[diagnosisSchedule] INMEMORYDB init");

		logger.info("*[Support Agent Started.!]");
	}


}
