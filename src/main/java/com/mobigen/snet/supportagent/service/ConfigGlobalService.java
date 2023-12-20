package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.dao.DaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConfigGlobalService {

	Logger logger = LoggerFactory.getLogger(ConfigGlobalService.class);

	@Autowired
	private DaoMapper daoMapper;

	public void init() {
		try {
			//ConfigGlobal 인스턴스 생성
			List<Map> configList = daoMapper.selectSnetConfigGlobalList();
			ConfigGlobalManager.getInstance(configList, true);
		}catch (Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			logger.error(e.getMessage());
		}

	}
}
