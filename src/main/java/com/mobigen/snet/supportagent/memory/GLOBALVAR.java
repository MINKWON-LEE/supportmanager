/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.memory
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 14.
 * description : 
 */
package com.mobigen.snet.supportagent.memory;

/**
 * @author Hyeon-sik Jung
 *
 */
public class GLOBALVAR {

	public enum PROCESS {
		
		AGENTMANAGER("AGENTMANAGER")
		, SUPPORTAGENT("SUPPORTMANAGER")
		, TOMCAT("TOMCAT");
		
		public String NAME;
		
		PROCESS(String processNm){
			this.NAME = processNm;
		}
	};

	public enum APPLICATION {
		
		WEB("WEB"), DB("DB");
		
		public String NAME;
		
		APPLICATION(String name){
			this.NAME = name;
		}
	};
	
	public enum JOBTYPE {
		
		EXCEL("EXCEL"),
		MAIL("MAIL"),
		SMS("SMS"),
		NMAP("NMAP"),
		MNMAP("MNMAP");
		
		public String jobCode;
		
		JOBTYPE(String jobCode){
			this.jobCode = jobCode;
		}
	};

	public enum TARGET {
		
		PROCESS("PROCESS"),
		CPU("CPU"),
		MEMORY("MEMORY"),
		DISK("DISK");
		
		public String NAME;
		
		TARGET(String target){
			this.NAME = target;
		}
	};
	
	public enum CONNECT_TYPE{
		SFTP("SFTP", 22),
		FTP("FTP", 21);
		
		public String TYPE;
		public int PORT;
		
		CONNECT_TYPE(String type, int target){
			this.TYPE = type;
			this.PORT = target;
		}		
	}
	
	public static String CONF_DIR = "/usr/local/snetManager/conf";
	public static String CONF_PROPERTIES_FILE = "support.context.properties";
	
}
