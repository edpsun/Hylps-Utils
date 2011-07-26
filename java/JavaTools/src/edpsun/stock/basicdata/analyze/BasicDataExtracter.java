/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 16, 2010 10:03:27 PM
 */
public class BasicDataExtracter {
	String headerRex = "<!.(.*)>≈≈(.*)≈≈.*";
	Pattern headerPattern = Pattern.compile(headerRex);

	public HashMap<String, Object> extractBasicData(String file, List<String> headers) throws AnalyzeException {
		File f = new File(file);
		return extractBasicData(f, headers);
	}

	public HashMap<String, Object> extractBasicData(File file, List<String> headers) throws AnalyzeException {
		if (!file.exists() || !file.isFile()) {
			throw new AnalyzeException("File can not be read. =>" + file.getAbsolutePath());
		}

		HashMap<String, Object> dataMap = new HashMap<String, Object>();

		BufferedReader br = null;
		String[] ts = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
			String line = null;
			boolean isCollectLine = false;
			while ((line = br.readLine()) != null) {
				//header
				if (isHeader(line)) {
					//deal with last header
					if (ts != null) {
						if (isCollectLine && sb.length() > 0) {
							dataMap.put(ts[0], sb.toString());
							sb.setLength(0);
						}
					}

					//deal with new header
					ts = getHeader(line);
					//System.out.println(ts[0] + " - " + ts[1]);
					if (!dataMap.containsKey(Instants.DATA_MAP_ENTRY_NAME)) {
						int len = ts[1].length();
						dataMap.put(Instants.DATA_MAP_ENTRY_NAME, ts[1].substring(0, len - 6));
						dataMap.put(Instants.DATA_MAP_ENTRY_CODE, ts[1].substring(len - 6));
					}

					if (headers.contains(ts[0])) {
						isCollectLine = true;
					} else {
						isCollectLine = false;
					}
				}

				//collect data body
				if (isCollectLine) {
					sb.append(line).append("\n");
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				//ignore
			}
		}

		return dataMap;
	}

	private boolean isHeader(String s) {
		Matcher m = headerPattern.matcher(s);
		if (m.find()) {
			return true;
		}
		return false;
	}

	private String[] getHeader(String s) {
		Matcher m = headerPattern.matcher(s);
		if (m.find()) {
			return new String[] { m.group(1), m.group(2) };
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		String s = "<!B公司概况>≈≈凤凰光学600071≈≈维赛特财经www.vsatsh.cn(更新:10.09.17)";
		String headerRex = "<!(.*)>≈≈(.*)≈≈.*";
		Pattern headerPattern = Pattern.compile(headerRex);
		Matcher m = headerPattern.matcher(s);
		if (m.find()) {
			System.out.println("?" + m.groupCount());
			System.out.println(m.group(0));
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}
	}
}
