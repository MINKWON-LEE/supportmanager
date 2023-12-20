/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 18.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class ServiceMaster {
	private String svcCd;
	private Integer svcMenu;
	private Integer svcType;
	private String svcNm;
	private String svcCreateDay;
	private Integer svcPkmsSeq;
	/**
	 * @return the svcCd
	 */
	public String getSvcCd() {
		return svcCd;
	}
	/**
	 * @param svcCd the svcCd to set
	 */
	public void setSvcCd(String svcCd) {
		this.svcCd = svcCd;
	}
	/**
	 * @return the svcMenu
	 */
	public Integer getSvcMenu() {
		return svcMenu;
	}
	/**
	 * @param svcMenu the svcMenu to set
	 */
	public void setSvcMenu(Integer svcMenu) {
		this.svcMenu = svcMenu;
	}
	/**
	 * @return the svcType
	 */
	public Integer getSvcType() {
		return svcType;
	}
	/**
	 * @param svcType the svcType to set
	 */
	public void setSvcType(Integer svcType) {
		this.svcType = svcType;
	}
	/**
	 * @return the svcNm
	 */
	public String getSvcNm() {
		return svcNm;
	}
	/**
	 * @param svcNm the svcNm to set
	 */
	public void setSvcNm(String svcNm) {
		this.svcNm = svcNm;
	}
	/**
	 * @return the svcCreateDay
	 */
	public String getSvcCreateDay() {
		return svcCreateDay;
	}
	/**
	 * @param svcCreateDay the svcCreateDay to set
	 */
	public void setSvcCreateDay(String svcCreateDay) {
		this.svcCreateDay = svcCreateDay;
	}
	/**
	 * @return the svcPkmsSeq
	 */
	public Integer getSvcPkmsSeq() {
		return svcPkmsSeq;
	}
	/**
	 * @param svcPkmsSeq the svcPkmsSeq to set
	 */
	public void setSvcPkmsSeq(Integer svcPkmsSeq) {
		this.svcPkmsSeq = svcPkmsSeq;
	}
}