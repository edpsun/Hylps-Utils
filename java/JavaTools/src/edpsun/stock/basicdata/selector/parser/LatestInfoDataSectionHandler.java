/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import java.util.Map;

import edpsun.stock.basicdata.selector.Instants;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 1:29:34 PM
 */
public class LatestInfoDataSectionHandler extends DataSectionHandler {
	String key1 = "公司近五年每股收益";
	String key2 = "─────────┴─────┴─────┴─────┴────────";

	@Override
	public void dealSection(Map<String, Object> map) {
		String section = (String) map.get(header);

		String latestInfo = getLatestInfoData(section);
		map.put(Instants.DATA_MAP_ENTRY_LATEST_INFO, latestInfo);

		String mainBusi = getMainBusiness(latestInfo);
		map.put(Instants.DATA_MAP_ENTRY_MAIN_BUSINESS, mainBusi);
	}

	private String getLatestInfoData(String section) {
		String ret = null;
		int i1 = section.indexOf(key1);
		int i2 = section.indexOf(key2, i1);
		if (i1 > -1 && i2 > -1) {
			ret = section.substring(0, i2 + key2.length());
		} else {
			System.out
					.println("[Error] LatestInfoDataSectionHandler: Can not find the key word in data. return unchanged string.");
			ret = section;
		}

		return ret;
	}

	private String getMainBusiness(String lastest) {
		String keyword0 = "│行业";
		String keyword1 = "│主营";
		String keyword2 = "───";
		int p0 = lastest.indexOf(keyword0);
		int p1 = lastest.indexOf(keyword1);

		if (p0 < p1 && p0 > -1) {
			p1 = p0;
		}

		int p2 = lastest.indexOf(keyword2, p1 + 1);

		String main_busi = "Unknown";
		if (p2 > p1 && p1 > -1) {
			main_busi = lastest.substring(p1, p2);
			main_busi = main_busi.replaceAll("\n.*│", " ");

			main_busi = main_busi.replaceAll("│", " ");
			while (main_busi.indexOf("  ") > -1) {
				main_busi = main_busi.replaceAll("  ", " ");
			}

		} else {
			main_busi = main_busi + " p1->" + p1 + " p2->" + p2;
		}

		return main_busi;
	}

}
