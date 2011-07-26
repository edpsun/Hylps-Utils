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
public class WatchDog_HMAIN extends WatchDog {

	private String tickLineKeyWord = "huochepiao";

	private String keyWord2 = "<table border=\"0\" align=\"center\" cellpadding=\"0\">";

	private int check_loop = -1;

	String linkRex = "href=(.*?) target=_blank>.*<A";
	Pattern linkPattern = Pattern.compile(linkRex);
	String captionRex = "<A href.*target=_blank>(.*?)</a>.*<A";
	Pattern captionPattern = Pattern.compile(captionRex);

	protected TicketObj[] getNewTicketLines() {
		ArrayList<TicketObj> newLines = new ArrayList<TicketObj>();

		HttpHelper httpHelper = getHttpHelper();
		InputStream input = getUrlContAsInputStream(httpHelper, condition.getValue());
		BufferedReader br = null;

		String lastHugeLine = null;
		String targetLine = null;

		try {
			br = new BufferedReader(new InputStreamReader(input, "GB2312"));
			String line = null;
			boolean hasKeyWord = false;
			TicketObj to = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.length() < 10)
					continue;

				if (lastHugeLine == null) {
					lastHugeLine = line;
				} else {
					if (line.indexOf(keyWord2) > -1) {
						targetLine = lastHugeLine;
						break;
					} else {
						lastHugeLine = line;
					}
				}

			}

			String[] allLines = targetLine.split("・ ");
			for (int i = 0; i < allLines.length; i++) {
				line = allLines[i];

				if (allLines[i].indexOf(tickLineKeyWord) > -1) {
					hasKeyWord = true;
					line = line.replace("&nbsp;", " ");

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

			if (lastHugeLine == null || !hasKeyWord) {
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

}
