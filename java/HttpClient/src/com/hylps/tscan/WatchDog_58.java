/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.hylps.util.HttpHelper;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 13, 2009 7:23:55 PM
 */
public class WatchDog_58 extends WatchDog {

	private String tickLineKeyWord = "class=\"t\" target=\"_blank\" href=";
	private String secKeyWord = "</a><span";//只是给58用的，通用的模板用huochepiao的实现

	private int check_loop = -1;

	String linkRex = "href=\"(.*?)\"";
	Pattern linkPattern = Pattern.compile(linkRex);
	String captionRex = "\">(.*)";
	Pattern captionPattern = Pattern.compile(captionRex);

	protected TicketObj[] getNewTicketLines() {
		ArrayList<TicketObj> newLines = new ArrayList<TicketObj>();

		HttpHelper httpHelper = getHttpHelper();
		InputStream input = getUrlContAsInputStream(httpHelper, condition.getValue());
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line = null;
			boolean hasKeyWord = false;
			TicketObj to = null;

			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.indexOf(tickLineKeyWord) > -1) {

					hasKeyWord = true;
					String tmpLine = null;
					while ((tmpLine = br.readLine()) != null) {
						tmpLine = tmpLine.trim();
						if (tmpLine.indexOf(secKeyWord) == -1) {
							line += " " + tmpLine;
						} else {
							break;
						}
					}

					line = line.trim().replace("&nbsp;", " ");
					line = line.replace("日", " ");
					line = line.replace("月", "-");

					String[] messages = attractTicketInfo(line);
					to = new TicketObj(messages);

					if (firstScan) {
						//just start
						if (pool.size() < poolSize) {
							pool.add(0, to);
						} else {
							break;
						}
					} else if (!pool.contains(to)) {
						newLines.add(to);
					} else {
						break;
					}
				}
			}

			if (!hasKeyWord) {
				System.out.println("Current URL:" + getLogPrefix());
				throw new RuntimeException(
						"[Error] Can not find the line containing the key word.Please check the URL manually!");
			}
			firstScan = false;
			return newLines.toArray(new TicketObj[newLines.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("[Error] deal with page content failure.", e);
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
			httpHelper.shutdownHttpClient();
		}
	}

	protected Pattern getCaptionPattern() {
		return captionPattern;
	}

	/* (non-Javadoc)
	 * @see com.hylps.kuxun.WatchDog#getLinkPattern()
	 */
	@Override
	protected Pattern getLinkPattern() {
		return linkPattern;
	}

	protected String[] attractTicketInfo(String info) {
		String[] s = super.attractTicketInfo(info);
		s[1] = s[1];

		return s;
	}

}