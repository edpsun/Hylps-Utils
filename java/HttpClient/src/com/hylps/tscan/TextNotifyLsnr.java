/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.util.Properties;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 13, 2009 11:12:57 PM
 */
public class TextNotifyLsnr implements NotifyListener {
	StringBuffer sb = new StringBuffer();
	NotifyEventFilter filter = null;

	public void onNotification(NotifyEvent ne) {
		if (filter != null) {
			if (!filter.accept(ne)) {
				//				System.out.println(ne.getEventMessages()[2] + " EXC============================== -> "
				//						+ ne.getEventMessages()[0]);
				return;
			}
		}

		sb.setLength(0);
		sb.append("\n===>>>[NEW ticket]<<<================================\n");
		sb.append(":-) ");
		sb.append(ne.getEventMessages()[2]);
		sb.append(ne.getEventMessages()[0]).append("\n");
		sb.append("    [Link] --> ");
		sb.append(ne.getEventMessages()[1]);
		sb.append("\n===>>>[NEW ticket]<<<================================\n");

		System.out.println(sb.toString());
	}

	public void activate(Properties context) {

	}

	public void setFilter(NotifyEventFilter _filter) {
		this.filter = _filter;
	}
}
