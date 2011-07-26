/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.util.Properties;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 13, 2009 11:10:51 PM
 */
public interface NotifyListener {
	void setFilter(NotifyEventFilter filter);

	void onNotification(NotifyEvent notification);

	void activate(Properties context);
}
