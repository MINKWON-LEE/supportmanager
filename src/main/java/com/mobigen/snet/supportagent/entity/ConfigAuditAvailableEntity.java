/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 12.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class ConfigAuditAvailableEntity {

	private String swType;
	private String swNm;
	private String unixYn;
	private String winYn;
	private Integer orderNum;
	private Integer orderNumTot;
	private String unixFileNm;
	private String winFileNm;
	/**
	 * @return the swType
	 */
	public String getSwType() {
		return swType;
	}
	/**
	 * @param swType the swType to set
	 */
	public void setSwType(String swType) {
		this.swType = swType;
	}
	/**
	 * @return the swNm
	 */
	public String getSwNm() {
		return swNm;
	}
	/**
	 * @param swNm the swNm to set
	 */
	public void setSwNm(String swNm) {
		this.swNm = swNm;
	}
	/**
	 * @return the unixYn
	 */
	public String getUnixYn() {
		return unixYn;
	}
	/**
	 * @param unixYn the unixYn to set
	 */
	public void setUnixYn(String unixYn) {
		this.unixYn = unixYn;
	}
	/**
	 * @return the winYn
	 */
	public String getWinYn() {
		return winYn;
	}
	/**
	 * @param winYn the winYn to set
	 */
	public void setWinYn(String winYn) {
		this.winYn = winYn;
	}
	/**
	 * @return the orderNum
	 */
	public Integer getOrderNum() {
		return orderNum;
	}
	/**
	 * @param orderNum the orderNum to set
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	/**
	 * @return the orderNumTot
	 */
	public Integer getOrderNumTot() {
		return orderNumTot;
	}
	/**
	 * @param orderNumTot the orderNumTot to set
	 */
	public void setOrderNumTot(Integer orderNumTot) {
		this.orderNumTot = orderNumTot;
	}
	/**
	 * @return the unixFileNm
	 */
	public String getUnixFileNm() {
		return unixFileNm;
	}
	/**
	 * @param unixFileNm the unixFileNm to set
	 */
	public void setUnixFileNm(String unixFileNm) {
		this.unixFileNm = unixFileNm;
	}
	/**
	 * @return the winFileNm
	 */
	public String getWinFileNm() {
		return winFileNm;
	}
	/**
	 * @param winFileNm the winFileNm to set
	 */
	public void setWinFileNm(String winFileNm) {
		this.winFileNm = winFileNm;
	}
}
