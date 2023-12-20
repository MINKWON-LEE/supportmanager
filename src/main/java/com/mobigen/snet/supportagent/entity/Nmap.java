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
public class Nmap {

	private String scanner;
	private String args;
	private String start;
	private String startstr;
	private String version;
	private String xmloutputversion;
	
	private ScanInfo scaninfo;
	
	private Verbose verbose;
	
	private Debugging debugging;
	
	private Host host;
	
	private RunStats runstats;

	/**
	 * @return the scanner
	 */
	public String getScanner() {
		return scanner;
	}

	/**
	 * @param scanner the scanner to set
	 */
	public void setScanner(String scanner) {
		this.scanner = scanner;
	}

	/**
	 * @return the args
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * @param args the args to set
	 */
	public void setArgs(String args) {
		this.args = args;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the startstr
	 */
	public String getStartstr() {
		return startstr;
	}

	/**
	 * @param startstr the startstr to set
	 */
	public void setStartstr(String startstr) {
		this.startstr = startstr;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the xmloutputversion
	 */
	public String getXmloutputversion() {
		return xmloutputversion;
	}

	/**
	 * @param xmloutputversion the xmloutputversion to set
	 */
	public void setXmloutputversion(String xmloutputversion) {
		this.xmloutputversion = xmloutputversion;
	}

	/**
	 * @return the scaninfo
	 */
	public ScanInfo getScaninfo() {
		return scaninfo;
	}

	/**
	 * @param scaninfo the scaninfo to set
	 */
	public void setScaninfo(ScanInfo scaninfo) {
		this.scaninfo = scaninfo;
	}

	/**
	 * @return the verbose
	 */
	public Verbose getVerbose() {
		return verbose;
	}

	/**
	 * @param verbose the verbose to set
	 */
	public void setVerbose(Verbose verbose) {
		this.verbose = verbose;
	}

	/**
	 * @return the debugging
	 */
	public Debugging getDebugging() {
		return debugging;
	}

	/**
	 * @param debugging the debugging to set
	 */
	public void setDebugging(Debugging debugging) {
		this.debugging = debugging;
	}

	/**
	 * @return the host
	 */
	public Host getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(Host host) {
		this.host = host;
	}

	/**
	 * @return the runstats
	 */
	public RunStats getRunstats() {
		return runstats;
	}

	/**
	 * @param runstats the runstats to set
	 */
	public void setRunstats(RunStats runstats) {
		this.runstats = runstats;
	}
}
