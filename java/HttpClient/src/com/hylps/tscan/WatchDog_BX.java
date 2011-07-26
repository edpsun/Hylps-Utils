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
public class WatchDog_BX extends WatchDog {

	private String tickLineKeyWord = "<th>到达城市</th><th>发布日期</th></tr>";
	private String secKeyWord = "<tr>";
	private String thdKeyWord = "/huochepiao";
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

					String[] tmp = line.split(secKeyWord);
					for (int i = 0; i < tmp.length; i++) {
						if (tmp[i].indexOf(thdKeyWord) > -1) {
							int p1 = tmp[i].indexOf("<a href");
							int p2 = tmp[i].indexOf("</a>", p1);
							if (p2 > p1) {
								line = tmp[i].substring(p1, p2);
							} else {
								continue;
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
						} else {
							continue;
						}
					}
					//只有一行数据，所以退出循环
					break;
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
		if (s[1].indexOf("http") > -1) {
			s[1] = s[1];
		} else {
			s[1] = "http://beijing.baixing.com" + s[1];
		}
		return s;
	}

}
