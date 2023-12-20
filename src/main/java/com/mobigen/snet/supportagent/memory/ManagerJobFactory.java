/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.memory.ManagerJobFactory.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2016. 2. 22.
 * description : 
 */

package com.mobigen.snet.supportagent.memory;

public interface ManagerJobFactory{
	public final String EXCEL = "EXCEL";
	public final String MAIL = "MAIL"; // MAIL notification
	public final String SMS  = "SMS";  // SMS notification
	public final String NMAP = "NMAP"; // Single task Nmap
	public final String FNMAP = "FNMAP"; // File task Nmap
	public final String MNMAP = "MNMAP"; // Multiple task Nmap 
	public final String OTP = "OTP"; // Multiple task Nmap 
}
