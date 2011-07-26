/**
 * HttpClient_declaration
 */
package com.hylps.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 26, 2011 1:50:44 PM
 */
public class HttpHelper {
	public boolean isDebug = false;
	HttpClient httpclient = null;

	public HttpHelper() {

	}

	public HttpClient getHttpClient() {
		return httpclient;
	}

	public void createHttpClient() {
		createHttpClient(new Properties());
	}

	public void createHttpClient(Properties props) {
		httpclient = new DefaultHttpClient();

		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 90000);

		if (isDebug) {
			System.out.println("[+] isUseProxy:" + props.getProperty("isUseProxy"));
		}

		if ("true".equals(props.getProperty("isUseProxy"))) {

			if (isDebug) {
				System.out.println("    [-] proxyHost:" + props.getProperty("proxyHost"));
				System.out.println("    [-] proxyPort:" + props.getProperty("proxyPort"));
				System.out.println("    [-] proxyType:" + props.getProperty("proxyType"));
			}

			HttpHost proxy = new HttpHost(props.getProperty("proxyHost"), Integer.valueOf(props
					.getProperty("proxyPort")), props.getProperty("proxyType"));
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
	}

	public void shutdownHttpClient() {
		httpclient.getConnectionManager().shutdown();
	}

	public InputStream getUrl(String url, Header[] headers) {
		if (httpclient == null) {
			throw new RuntimeException("[Error] Please createHttpClient first!");
		}

		InputStream input = null;
		HttpGet get = new HttpGet(url);
		try {
			for (int i = 0; i < headers.length; i++) {
				get.setHeader(headers[i]);
			}

			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = null;
			if (response.getStatusLine().getStatusCode() == 200) {
				entity = response.getEntity();
				input = entity.getContent();
			} else {
				get.abort();
				throw new RuntimeException("[Error] return code not 200. URL:" + url);
			}
		} catch (Exception ex) {
			get.abort();
			throw new RuntimeException("[Error] Unexpect exception in http access.", ex);
		}

		return input;
	}
}
