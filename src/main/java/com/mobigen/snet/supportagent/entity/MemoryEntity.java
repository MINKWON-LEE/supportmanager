/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 15.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class MemoryEntity {

	private String serverNm;
	private String total;
	private String free;
	private String used;
	private String useRate;
	private String serverIp;
	
	/**
	 * @return the serverNm
	 */
	public String getServerNm() {
		return serverNm;
	}
	/**
	 * @param serverNm the serverNm to set
	 */
	public void setServerNm(String serverNm) {
		this.serverNm = serverNm;
	}
	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}
	/**
	 * @return the free
	 */
	public String getFree() {
		return free;
	}
	/**
	 * @param free the free to set
	 */
	public void setFree(String free) {
		this.free = free;
	}
	/**
	 * @return the used
	 */
	public String getUsed() {
		return used;
	}
	/**
	 * @param used the used to set
	 */
	public void setUsed(String used) {
		this.used = used;
	}
	/**
	 * @return the useRate
	 */
	public String getUseRate() {
		return useRate;
	}
	/**
	 * @param useRate the useRate to set
	 */
	public void setUseRate(String useRate) {
		this.useRate = useRate;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

}
