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
public class Finished {

	private String time;
	private String timestr;
	private String elapsed;
	private String summary;
	private String exit;
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the timestr
	 */
	public String getTimestr() {
		return timestr;
	}
	/**
	 * @param timestr the timestr to set
	 */
	public void setTimestr(String timestr) {
		this.timestr = timestr;
	}
	/**
	 * @return the elapsed
	 */
	public String getElapsed() {
		return elapsed;
	}
	/**
	 * @param elapsed the elapsed to set
	 */
	public void setElapsed(String elapsed) {
		this.elapsed = elapsed;
	}
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}
	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * @return the exit
	 */
	public String getExit() {
		return exit;
	}
	/**
	 * @param exit the exit to set
	 */
	public void setExit(String exit) {
		this.exit = exit;
	}
}
