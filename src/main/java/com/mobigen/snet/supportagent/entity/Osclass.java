/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 25.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class Osclass {

	private String vendor;
	private String osfamily;
	private String osgen;
	private String accuracy;
	private String cpe;
	/**
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}
	/**
	 * @param vendor the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	/**
	 * @return the osfamily
	 */
	public String getOsfamily() {
		return osfamily;
	}
	/**
	 * @param osfamily the osfamily to set
	 */
	public void setOsfamily(String osfamily) {
		this.osfamily = osfamily;
	}
	/**
	 * @return the osgen
	 */
	public String getOsgen() {
		return osgen;
	}
	/**
	 * @param osgen the osgen to set
	 */
	public void setOsgen(String osgen) {
		this.osgen = osgen;
	}
	/**
	 * @return the accuracy
	 */
	public String getAccuracy() {
		return accuracy;
	}
	/**
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	/**
	 * @return the cpe
	 */
	public String getCpe() {
		return cpe;
	}
	/**
	 * @param cpe the cpe to set
	 */
	public void setCpe(String cpe) {
		this.cpe = cpe;
	}
}
