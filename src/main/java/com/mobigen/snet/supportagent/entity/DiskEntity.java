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
public class DiskEntity {

//	Filesystem, Size, Used, Avail, Use%, Mounted
	private String serverNm;
	private String fileSystem;
	private String size;
	private String used;
	private String avail;
	private String useRate;
	private String mountedOn;
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
	 * @return the fileSystem
	 */
	public String getFileSystem() {
		return fileSystem;
	}
	/**
	 * @param fileSystem the fileSystem to set
	 */
	public void setFileSystem(String fileSystem) {
		this.fileSystem = fileSystem;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
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
	 * @return the avail
	 */
	public String getAvail() {
		return avail;
	}
	/**
	 * @param avail the avail to set
	 */
	public void setAvail(String avail) {
		this.avail = avail;
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
	/**
	 * @return the mountedOn
	 */
	public String getMountedOn() {
		return mountedOn;
	}
	/**
	 * @param mountedOn the mountedOn to set
	 */
	public void setMountedOn(String mountedOn) {
		this.mountedOn = mountedOn;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
}
