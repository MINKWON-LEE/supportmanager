/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 13.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.entity;

import java.util.List;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.entity
 * Company : Mobigen
 * File    : TMSInterworkingEntity.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 13.
 * Description : 
 * 
 */
public class TMSInterworkingEntity {

	private List<FirmWare> firmwareList;
	private List<Company> companyList;
	private List<Group> groupList;
	private List<Asset> assetList;
	public List<FirmWare> getFirmwareList() {
		return firmwareList;
	}
	public void setFirmwareList(List<FirmWare> firmwareList) {
		this.firmwareList = firmwareList;
	}
	public List<Company> getCompanyList() {
		return companyList;
	}
	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}
	public List<Group> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<Group> groupList) {
		this.groupList = groupList;
	}
	public List<Asset> getAssetList() {
		return assetList;
	}
	public void setAssetList(List<Asset> assetList) {
		this.assetList = assetList;
	}
}
