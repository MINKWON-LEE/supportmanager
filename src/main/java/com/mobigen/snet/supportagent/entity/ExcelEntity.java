/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 17.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

import java.util.List;

/**
 * @author Hyeon-sik Jung
 *
 */
public class ExcelEntity {

	private List<ExcelListEntity> entityList;

	/**
	 * @return the entityList
	 */
	public List<ExcelListEntity> getEntityList() {
		return entityList;
	}

	/**
	 * @param entityList the entityList to set
	 */
	public void setEntityList(List<ExcelListEntity> entityList) {
		this.entityList = entityList;
	}
	
}
