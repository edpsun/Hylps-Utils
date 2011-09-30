/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.hylps.tscan.config.Condition;
import com.hylps.util.HttpHelper;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 13, 2009 7:23:55 PM
 */
public abstract class WatchDog extends Thread {
	protected boolean isDebugMode = false;
	protected boolean isUseProxy = false;
	protected String proxyHost = null;
	protected int proxyPort = -1;

	protected SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

	protected Properties context = null;
	protected Condition condition = null;

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

	protected boolean isExit = false;
	protected boolean firstScan = true;

	protected Random rdm = new Random();
	protected static String[] headers = new String[] {
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.04 (lucid) Firefox/3.6.13",
			"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2",
			"Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Ubuntu/10.04 Chromium/10.0.642.2",
			"Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/534.10 (KHTML, like Gecko) Chrome/8.0.552.237 Safari/534.10",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; CIBA; TheWorld)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; CIBA)", };

	public static int scan_time_secs = 60;
	public static int http_timeout_secs = 90;
	private int check_loop = -1;

	SimpleDateFormat sdf = new SimpleDateFormat("");

	static {
		String s = System.getProperty("SCAN_TIME", "60");

		scan_time_secs = Integer.valueOf(s);
		System.out.println("[Info] SCAN_Time:" + scan_time_secs);

	}
	public int poolSize = 30;
	ArrayList<TicketObj> pool = new ArrayList<TicketObj>();
	ArrayList<NotifyListener> listeners = new ArrayList<NotifyListener>();

	public synchronized void addNotifyListener(NotifyListener lsnr) {
		listeners.add(lsnr);
	}

	public void notifyMessage(NotifyEvent o) {
		for (NotifyListener listener : listeners) {
			listener.onNotification(o);
		}
	}

	public void setProxy(String host, int port) {
		isUseProxy = true;
		proxyHost = host;
		proxyPort = port;
	}

	public void setNotUseProxy() {
		isUseProxy = false;
		proxyHost = null;
		proxyPort = -1;
	}

	public void setDebugMode(boolean b) {
		isDebugMode = b;
	}

	public void run() {
		watchPage();
	}

	private void watchPage() {
		while (!isExit) {
			try {
				checkUrl();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Unexpected Ex:" + e.getMessage());
				//ignore
			}

			try {
				Random r = new Random();
				int i = r.nextInt(15);
				Thread.sleep(1000 * (scan_time_secs + i));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	protected void dealWithNewLine(TicketObj line) {
		if (pool.size() >= poolSize) {
			pool.remove(0);
		}

		if (pool.size() > poolSize) {
			System.out.println("Unexpected pool size!");
		}

		pool.add(line);

		NotifyEvent ne = new NotifyEvent();
		ne.setCondition(condition);
		ne.setEventMessages(new String[] { line.getCaption(), line.getLink(), line.getFrom() });
		notifyMessage(ne);
	}

	abstract TicketObj[] getNewTicketLines();

	protected void showCurrentLine(TicketObj line, boolean isVerbose) {
		StringBuffer output = new StringBuffer();

		output.append(getLogPrefix());
		if (isVerbose) {
			output.append("[top] ");
			output.append("[");
			output.append(df.format(new Date()));
			output.append("]    --> ");
			output.append(getLineInfo(line)).append("\n");
		} else {
			//			output.append("[");
			//			output.append(check_loop);
			//			output.append("] --> ");

			output.append("NO new post!").append("\n");
		}

		System.out.println(output.toString());
	}

	String spaces = "------------------------------";

	protected void checkUrl() {

		TicketObj[] newlines = getNewTicketLines();

		StringBuffer sb = new StringBuffer();
		if (isDebugMode) {
			if (newlines.length >= 1) {
				sb.append(getLogPrefix());
				sb.append("-> new lines number:");
				sb.append(newlines.length).append("\n");

				sb.append("\n======================================\n");
				for (int i = 0; i < newlines.length; i++) {
					sb.append("    [-]").append(newlines[i]).append("\n");
				}
				System.out.println(sb.toString());
			}
		} else {
			System.out.print("Refreshing -------[" + condition.getType() + "]" + condition.getName() + spaces + "\r");
		}

		//		//debug:
		//		if (newlines.length > 10) {
		//			System.out.println("A#########################################################");
		//			for (TicketObj ticketObj : pool) {
		//				System.out.println("\n------> " + ticketObj);
		//			}
		//			System.out.println("A#########################################################");
		//
		//			System.out.println("B#########################################################");
		//			for (int i = 0; i < newlines.length; i++) {
		//				System.out.println("\n=====> " + pool.contains(newlines[i]) + "\n" + newlines[i]);
		//			}
		//			System.out.println("B#########################################################");
		//		}

		check_loop++;
		if (newlines.length == 0) {
			if ((check_loop % 10) == 0) {
				check_loop = 0;
				if (isDebugMode) {
					showCurrentLine(pool.get(pool.size() - 1), true);
				}
			} else {
				if (isDebugMode) {
					showCurrentLine(pool.get(pool.size() - 1), false);
				}
			}
		} else {
			check_loop = 0;
			for (int i = newlines.length - 1; i >= 0; i--) {
				dealWithNewLine(newlines[i]);
			}
			showCurrentLine(pool.get(pool.size() - 1), true);
		}
	}

	/**
	 * @param isExit the isExit to set
	 */
	public void setExitFlag(boolean isExit) {
		this.isExit = isExit;
	}

	/**
	 * @return the context
	 */
	public Properties getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Properties context) {
		this.context = context;
	}

	protected String getLogPrefix() {
		String type = condition.getType();
		String typeString = null;
		if (type.equalsIgnoreCase("K")) {
			typeString = "酷讯";
		} else if (type.equalsIgnoreCase("H")) {
			typeString = "票网";

		} else if (type.equalsIgnoreCase("HMain")) {
			typeString = "主票网";

		} else if (type.equalsIgnoreCase("G")) {
			typeString = "赶集";

		} else if (type.equalsIgnoreCase("58")) {
			typeString = "58";

		} else {
			typeString = type;
		}

		String str = "[" + condition.getName() + "]";//[" + typeString + "]";
		return str;
	}

	protected abstract Pattern getLinkPattern();

	protected abstract Pattern getCaptionPattern();

	protected String[] attractTicketInfo(String info) {

		//captain.
		String caption = "no_caption";//////////////////
		Pattern captionPattern = getCaptionPattern();
		Matcher captionMatcher = captionPattern.matcher(info);
		if (captionMatcher.find()) {
			caption = captionMatcher.group(1);
		} else {
			System.out.println("[Error] Can not find caption." + "\n [Line] " + info + "\n [Pattern] "
					+ captionPattern.toString());
		}

		//link
		String link = "no_link";
		Pattern linkPattern = getLinkPattern();
		Matcher linkMatcher = linkPattern.matcher(info);
		if (linkMatcher.find()) {
			link = linkMatcher.group(1);
		} else {
			System.out.println("[Error] Can not find link." + "\n [Line] " + info + "\n [Pattern] "
					+ linkPattern.toString());
		}

		return new String[] { caption, link, getLogPrefix() };
	}

	protected String getLineInfo(TicketObj line) {
		String str = "";
		if (isDebugMode) {
			str += line.getCaption() + "\n                      Http   --> " + line.getLink();
		} else {
			str += line.getCaption() + "\n                      Http   --> " + line.getLink();
		}
		return str;
	}

	protected HttpHelper getHttpHelper() {
		HttpHelper httpHelper = new HttpHelper();

		Properties properties = new Properties();

		if (isUseProxy) {
			properties.setProperty("isUseProxy", "" + isUseProxy);
			properties.setProperty("proxyHost", proxyHost);
			properties.setProperty("proxyPort", "" + proxyPort);
			properties.setProperty("proxyType", "http");
		}
		httpHelper.createHttpClient(properties);

		return httpHelper;
	}

	protected InputStream getUrlContAsInputStream(HttpHelper httpHelper, String url) {
		Header h1 = new BasicHeader("Connection", "close");
		String header = headers[rdm.nextInt(headers.length)];
		Header h2 = new BasicHeader(HTTP.USER_AGENT, header);

		InputStream input = null;
		try {
			input = httpHelper.getUrl(condition.getValue(), new Header[] { h1, h2 });
		} catch (Exception e) {
			if (isDebugMode) {
				e.printStackTrace();
				System.out.println("[INFO] Retry " + url);
			} else {
				System.out.println("[Error] while get page content: " + e.getMessage() + "  -> Retry  " + url);
			}
			input = httpHelper.getUrl(condition.getValue(), new Header[] { h1, h2 });
		}
		return input;
	}
}
