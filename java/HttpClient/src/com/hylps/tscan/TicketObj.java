/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 22, 2011 10:26:56 AM
 */
public class TicketObj {
	String id = "";
	String caption = "";
	String link = "";
	String date = "";
	String trainid = "";
	String from = "";

	public TicketObj(String[] info) {
		caption = info[0];
		link = info[1];
		from = info[2];
	}

	public TicketObj(String _caption, String _link, String _from) {
		from = _from;
		caption = _caption;
		link = _link;
	}

	public TicketObj() {

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the trainid
	 */
	public String getTrainid() {
		return trainid;
	}

	/**
	 * @param trainid the trainid to set
	 */
	public void setTrainid(String trainid) {
		this.trainid = trainid;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!"".equals(from)) {
			sb.append("[From] ").append(from).append("\n");
		}

		if (!"".equals(id)) {
			sb.append("[ID] ").append(id).append("\n");
		}

		if (!"".equals(date)) {
			sb.append("[Date] ").append(date).append("\n");
		}

		if (!"".equals(trainid)) {
			sb.append("[TrainID] ").append(trainid).append("\n");
		}

		if (!"".equals(caption)) {
			sb.append("[Caption] ").append(caption).append("\n");
		}

		if (!"".equals(link)) {
			sb.append("[Link] ").append(link).append("\n");
		}

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		return this.toString().equals(obj.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
