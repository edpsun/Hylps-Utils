/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import com.hylps.tscan.config.Condition;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 14, 2009 12:38:09 PM
 */
public class NotifyEvent {
	Condition condition = null;

	/**
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	String[] eventMessages = null;

	/**
	 * @return the eventMessages
	 */
	public String[] getEventMessages() {
		return eventMessages;
	}

	/**
	 * @param eventMessages the eventMessages to set
	 */
	public void setEventMessages(String[] eventMessages) {
		this.eventMessages = eventMessages;
	}

}
