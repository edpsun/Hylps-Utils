/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 13, 2009 11:12:57 PM
 */
public class GuiNotifyLsnr extends Thread implements NotifyListener {
	StringBuffer sb = new StringBuffer();
	NotifyEventFilter filter = null;

	NotifyFrame window = new NotifyFrame();

	ArrayList<String> messageList = new ArrayList<String>();

	public void onNotification(NotifyEvent ne) {
		String line = ne.getEventMessages()[0];

		if (filter != null) {
			if (!filter.accept(ne)) {
				if (WatchManager.isDebugMode) {
					System.out.println("[DEBUG] Filtered -> Line content:" + line);
				}
				return;
			}
		}

		//<a href='http://www.google.com'>google</a>
		String pattern = "<a href='%s'>%s</a> &nbsp; <a href='%s'>%s</a><br><br>";
		String message = String.format(pattern, new Object[] { ne.getCondition().getValue(), ne.getEventMessages()[2],
				ne.getEventMessages()[1], line });

		synchronized (messageList) {
			messageList.add(message);
			messageList.notifyAll();
		}

	}

	private void updateMessage(String line) {
		String current = window.getNotificationMessage();

		int i = current.indexOf("</body>");
		if (i == -1) {
			current = "<html><body> Not HTML!<br>";
		} else {
			current = current.substring(0, i);
		}

		final String newMessage = current + line + "</body></html>";

		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					window.setNotificationMessage(newMessage);
					if (!window.isVisible()) {
						window.setVisible(true);
					}
				}
			});
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void activate(Properties context) {
		this.start();
	}

	public void run() {
		String line = null;
		while (true) {
			synchronized (messageList) {
				if (messageList.size() == 0) {
					try {
						messageList.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				line = messageList.remove(0);
			}

			updateMessage(line);

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setFilter(NotifyEventFilter _filter) {
		this.filter = _filter;
	}

}
