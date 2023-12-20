package com.mobigen.snet.supportagent.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.dao.PatchDBMapper;
import com.mobigen.snet.supportagent.utils.DateUtil;

import jodd.util.StringUtil;


@Component("patchDBComponent")
public class PatchDBComponent extends AbstractComponent{
	
	@Autowired
	private PatchDBMapper patchDBMapper;

	public List<String> patch(String path){
		List<String> result = new ArrayList<String>();
		
		// 1. read update sql
		String fileName = "UPDATE.sql";
		File file = new File(path + File.separator + fileName);
		
		String sql = "";
		String line = "";
		BufferedReader in =null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while ((line = in.readLine()) != null) {
				sql += " " + StringUtil.trimRight(line);
				if (sql.endsWith(";")) {
					result.add(DateUtil.getCurrDateByStringFormat("yyyy-MM-dd HH:mm:ss") + " [[ " + sql + " ]] - " + executeQuery(sql));
					sql = "";
				}
			}
		} catch (IOException e) {
			logger.error("Patch Exception :: {}", e.getMessage(), e.fillInStackTrace());
		} finally{
			try {
				if(in!=null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	public String executeQuery(String query){
		String result = "SUCCESS!";
		try {
			Map<String, String> map =new HashMap<String, String>();
			map.put("query", query);
			patchDBMapper.patchDbQuery(map);
		} catch (Exception e) {
			result = "FAILED. [[ "+e.getMessage()+" ]]";
		}
		return result;
	}
	
}
