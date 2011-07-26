/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import edpsun.stock.basicdata.selector.Instants;
import edpsun.stock.basicdata.selector.bean.HolderVO;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 1:29:34 PM
 */
public class HolderDataSectionHandler extends DataSectionHandler {
	String key1 = "历年人均持股情况";
	String key2 = "  ─────┴───────┴─────┴─────┴────┴─────";

	@Override
	public void dealSection(Map<String, Object> map) throws Exception {
		String section = (String) map.get(header);

		String holderData = getHolderData(section);
		map.put(Instants.DATA_MAP_ENTRY_HOLDER_DATA, holderData);

		HolderParser holderParser = new HolderParser();
		HolderVO[] holderVos = holderParser.parseHolderData(holderData);
		map.put(Instants.DATA_MAP_ENTRY_HOLDER_VO_ARRAY, holderVos);
	}

	private String getHolderData(String section) {
		String ret = null;
		int i1 = section.indexOf(key1);
		int i2 = section.indexOf(key2, i1);
		if (i1 > -1 && i2 > -1) {
			ret = section.substring(i1, i2 + key2.length());
		} else if (i1 > -1 && i2 == -1) {
			String tmp = section.substring(i1);

			StringBuilder sb = new StringBuilder();

			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(tmp.getBytes())));
			String line = null;
			boolean dateStarted = false;
			try {
				while ((line = br.readLine()) != null) {

					if (!isAccepted(line) && !dateStarted) {
						sb.append(line).append("\n");
					} else if (isAccepted(line)) {
						sb.append(line).append("\n");
						dateStarted = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			ret = sb.toString();
		} else {
			System.out
					.println("[Error] HolderDataSectionHandler: Can not find the key word in data. return EMPTY string."
							+ " Start:" + i1 + " End:" + i2);

			ret = "";
		}

		return ret;
	}

	private boolean isAccepted(String l) {
		String[] as = l.split("│");
		String tmp = null;
		if (as.length >= 6) {
			tmp = as[0].trim();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				df.parse(tmp);
				return true;
			} catch (ParseException e) {
			}
		}

		return false;
	}
}
