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
 * File    : Group.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 10.
 * Description : 
 * 
 */
public class Group {

	private String branchId;
	
	private String branchNm;
	
	private String teamId;
	
	private String teamNm;

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchNm() {
		return branchNm;
	}

	public void setBranchNm(String branchNm) {
		this.branchNm = branchNm;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamNm() {
		return teamNm;
	}

	public void setTeamNm(String teamNm) {
		this.teamNm = teamNm;
	}
}
