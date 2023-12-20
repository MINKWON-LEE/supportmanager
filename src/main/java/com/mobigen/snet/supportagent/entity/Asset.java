/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.entity
 * Company : Mobigen
 * File    : Asset.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
public class Asset {

	private String macAddress;
	
	private String companyId;
	
	private String assetLocation;
	
	private String ipAddress;
	
	private String teamId;
	
	private String firmwareVer;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getAssetLocation() {
		return assetLocation;
	}

	public void setAssetLocation(String assetLocation) {
		this.assetLocation = assetLocation;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getFirmwareVer() {
		return firmwareVer;
	}

	public void setFirmwareVer(String firmwareVer) {
		this.firmwareVer = firmwareVer;
	}
}
