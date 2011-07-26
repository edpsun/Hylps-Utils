/**
 * HttpClient_declaration
 */
package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.crypto.Data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import com.hylps.tscan.TicketObj;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 21, 2011 9:06:39 PM
 */
public class B {
	public static void main(String[] args) {
		getNewTicketLines();

	}

	protected static HttpClient getHttpClient() {
		HttpClient httpclient = new DefaultHttpClient();

		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		return httpclient;
	}

	protected static TicketObj[] getNewTicketLines() {
		ArrayList<TicketObj> newLines = new ArrayList<TicketObj>();

		HttpClient client = getHttpClient();
		String header = "hipigok";

		HttpGet get = new HttpGet("http://localhost:9000");
		get.setHeader("Connection", "close");
		get.setHeader(HTTP.USER_AGENT, header);
		
		InputStream input = null;
		BufferedReader br = null;
		try {
			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = null;
				if (response.getStatusLine().getStatusCode() == 200) {
					entity = response.getEntity();
					input = entity.getContent();
				} else {
					//retry
					System.out.println(" Retry...");
					response = client.execute(get);

					if (response.getStatusLine().getStatusCode() == 200) {
						entity = response.getEntity();
						input = entity.getContent();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("[Error] Unexpect exception in http access.");
			}

			try {
				br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
				String line = null;
				boolean hasKeyWord = false;
				TicketObj to = null;

				while ((line = br.readLine()) != null) {
					line = line.trim();
					System.out.println(line);
				}

				return newLines.toArray(new TicketObj[newLines.size()]);
			} catch (Exception e) {
				System.out.println("Might be timeout! Current URL:");
				e.printStackTrace();
				throw new RuntimeException("[Error] Unexpect exception in dealing page content.");
			}
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
			client.getConnectionManager().shutdown();
		}
	}
}
