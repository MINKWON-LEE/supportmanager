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
 * File    : FirmWare.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
public class FirmWare {

	private String companyId;
	
	private String companyNm;
	
	private String firmwareVer;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyNm() {
		return companyNm;
	}

	public void setCompanyNm(String companyNm) {
		this.companyNm = companyNm;
	}

	public String getFirmwareVer() {
		return firmwareVer;
	}

	public void setFirmwareVer(String firmwareVer) {
		this.firmwareVer = firmwareVer;
	}
}
