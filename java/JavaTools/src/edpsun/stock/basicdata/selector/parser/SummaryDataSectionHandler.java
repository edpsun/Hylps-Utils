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
public class SummaryDataSectionHandler extends DataSectionHandler {
	String key1 = "经营范围";
	String key2 = "主营业务";
	String key3 = "公司背景";
	String endKeyword = "└";

	@Override
	public void dealSection(Map<String, Object> map) {
		String section = (String) map.get(header);

		String summary = getSummaryData(section);
		map.put(Instants.DATA_MAP_ENTRY_SUMMARY_DATA, summary);
	}

	/**
	 * @param section
	 */
	private String getSummaryData(String section) {
		int start = -1;
		int p1 = section.indexOf(key1);
		if (p1 > -1)
			start = p1;

		int p2 = section.indexOf(key2);
		if (p2 > -1 && p2 < start) {
			start = p2;
		}

		int p3 = section.indexOf(key3);
		if (p3 > -1 && p3 < start) {
			start = p3;
		}

		int pe = section.indexOf(endKeyword);
		String ret = null;
		if (start > -1 && pe > -1) {
			ret = section.substring(start, pe);
		} else {
			System.out
					.println("[Error] SummaryDataSectionHandler: "
							+ "Can not find the key word in data. return unchanged string." + " Start:" + start
							+ "  End:" + pe);
			ret = "Key word not found! Start:" + start + "  End:" + pe;
		}

		ret = ret.replace("├", "");
		ret = ret.replace("┼", "");
		ret = ret.replace("┤", "");
		ret = ret.replace("─", "");
		ret = ret.replace("│", "");
		ret = ret.replace(" ", "");
		//ret = ret.replace("\n", "");
		ret = ret.replace(key1, "<b>" + key1 + ":</b>\n");
		ret = ret.replace(key2, "\n----------------\n<b>" + key2 + ":</b>\n");
		ret = ret.replace(key3, "\n----------------\n<b>" + key3 + "</b>:\n");
		ret = ret.replace("\n\n", "");
		return ret;
	}
}
