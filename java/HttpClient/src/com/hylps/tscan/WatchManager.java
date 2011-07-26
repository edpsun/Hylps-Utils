/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.hylps.tscan.config.Condition;
import com.hylps.tscan.config.ObjectFactory;
import com.hylps.tscan.config.TscanConfig;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 13, 2009 7:23:12 PM
 */
public class WatchManager {
	static final String PROP_NAME_DEBUG_MODE = "DEBUG_MODE";
	static final String PROP_NAME_USE_HTTP_PROXY = "USE_HTTP_PROXY";
	static final String PROP_NAME_HTTP_PROXY_HOST = "HTTP_PROXY_HOST";
	static final String PROP_NAME_HTTP_PROXY_PORT = "HTTP_PROXY_PORT";
	static final String PROP_USE_NOTIFY_FILTER = "USE_NOTIFY_FILTER";
	static String CONFIG_FILE = "stuff" + File.separator + "config.xml";

	public static boolean isDebugMode = false;
	private static boolean isUseNotifyFilter = false;
	private boolean isUseProxy = false;
	private String proxyHost = null;
	private int proxyPort = -1;
	private TscanConfig tscanConfig = null;

	public WatchManager() {
		readRuntimeConfig();
		tscanConfig = ObjectFactory.unMarshall(CONFIG_FILE);
	}

	private void readRuntimeConfig() {
		String strDebugMode = System.getProperty(PROP_NAME_DEBUG_MODE, "");
		isDebugMode = (strDebugMode.equalsIgnoreCase("true")) ? true : false;

		String strUseNotifyFilter = System.getProperty(PROP_USE_NOTIFY_FILTER, "");
		isUseNotifyFilter = (strUseNotifyFilter.equalsIgnoreCase("true")) ? true : false;

		String strUseProxy = System.getProperty(PROP_NAME_USE_HTTP_PROXY, "");
		isUseProxy = (strUseProxy.equalsIgnoreCase("true")) ? true : false;
		if (isUseProxy) {
			System.out.println("[INFO] Using proxy!");
			proxyHost = System.getProperty(PROP_NAME_HTTP_PROXY_HOST);
			proxyPort = Integer.parseInt(System.getProperty(PROP_NAME_HTTP_PROXY_PORT, "-1"));

			if (isDebugMode) {
				System.out.println("[INFO] proxyHost:" + proxyHost);
				System.out.println("[INFO] proxyPort:" + proxyPort);
			}
		} else {
			System.out.println("[INFO] Connect directly!");
		}

		if (isDebugMode) {
			CONFIG_FILE = "stuff" + File.separator + "config_debug.xml";
			System.out.println("Using " + CONFIG_FILE);
		}

		System.out.println("[INFO] Use NotifyFilter:" + isUseNotifyFilter);
		System.out.println("Version: 2011-01_25 20:00");
	}

	public void startWork() {
		List<Condition> urlList = tscanConfig.getCondition();

		System.out.println("-------------------------------------------------");
		for (Condition con : (List<Condition>) urlList) {
			con.setValue(con.getValue().trim());
			System.out.println(String.format(" [-] [%s] [%s]\t => %s ", con.getName(), con.getType(), con.getValue()));
		}
		System.out.println("-------------------------------------------------\n");

		//
		Properties props = new Properties();
		WatchDog wd = null;

		NotifyListener txtlsnr = new TextNotifyLsnr();
		NotifyListener midilsnr = new MidiNotifyLsnr();
		NotifyListener guilsnr = new GuiNotifyLsnr();

		if (isUseNotifyFilter) {
			NotifyEventFilterXmlImpl filter = new NotifyEventFilterXmlImpl(CONFIG_FILE);
			filter.setVerbose(true);

			txtlsnr.setFilter(filter);
			filter.showFilter();

			midilsnr.setFilter(new NotifyEventFilterXmlImpl(CONFIG_FILE));
			guilsnr.setFilter(new NotifyEventFilterXmlImpl(CONFIG_FILE));
		}

		txtlsnr.activate(props);
		midilsnr.activate(props);
		guilsnr.activate(props);

		String wdNameBase = "com.hylps.tscan.WatchDog_";
		String wdName = null;
		for (Condition con : (List<Condition>) urlList) {
			wdName = wdNameBase + con.getType().toUpperCase();
			if (isDebugMode) {
				System.out.println("[DEBUG] WatchDog Name:" + wdName + " for URL type:" + con.getType());
			}

			try {
				wd = (WatchDog) Class.forName(wdName).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			wd.setContext(props);
			wd.setCondition(con);

			wd.setDebugMode(isDebugMode);
			if (isUseProxy) {
				wd.setProxy(proxyHost, proxyPort);
			}

			wd.addNotifyListener(txtlsnr);
			wd.addNotifyListener(midilsnr);
			wd.addNotifyListener(guilsnr);

			wd.start();
		}
	}

	public static void main(String[] args) {
		WatchManager watchManager = new WatchManager();
		watchManager.startWork();
	}
}
