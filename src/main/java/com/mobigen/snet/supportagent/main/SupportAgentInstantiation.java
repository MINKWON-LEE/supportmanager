package com.mobigen.snet.supportagent.main;

import javax.annotation.PostConstruct;

import com.mobigen.snet.supportagent.service.ConfigGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.component.ShadowDecryptComponet;
import com.mobigen.snet.supportagent.listener.NotificationListener;
import com.mobigen.snet.supportagent.service.AbstractService;

@Component
public class SupportAgentInstantiation extends AbstractService{

	@Autowired
	private NotificationListener notificationListener;
	
	@Autowired (required = false)
	private ShadowDecryptComponet shadowDecryptComponet;

	@Autowired (required = false)
	private ConfigGlobalService configGlobalService;

	/**
	 * Init method
	 */
	@PostConstruct
	public void init(){

		logger.info("*[Support Agent Post Construct.. init]");
		/*
		 *  socket server start..
		 */
		notificationListener.initNotificationListener();
		configGlobalService.init();

	}
}
