/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 13.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class ProcessEntity {

	private String serverNm;
	/**
	 * Process 명 
	 */
	private String procesNm;
	
	private String user;
	/**
	 * process id 
	 */
	private String pid;
	/**
	 * memory 점유율 %
	 */
	private String mem;
	/**
	 * CPU 점유율 %
	 */
	private String cpu;
	/**
	 * 프로세스 상태
	 * 0 : dead
	 * 1 : live
	 */
	private int status;
	
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
	 * @return the procesNm
	 */
	public String getProcesNm() {
		return procesNm;
	}
	/**
	 * @param procesNm the procesNm to set
	 */
	public void setProcesNm(String procesNm) {
		this.procesNm = procesNm;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}
	/**
	 * @return the mem
	 */
	public String getMem() {
		return mem;
	}
	/**
	 * @param mem the mem to set
	 */
	public void setMem(String mem) {
		this.mem = mem;
	}
	/**
	 * @return the cpu
	 */
	public String getCpu() {
		return cpu;
	}
	/**
	 * @param cpu the cpu to set
	 */
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
}
