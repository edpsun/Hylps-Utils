/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 1:29:34 PM
 */
public class LatestInfoDataSectionHandler extends DataSectionHandler {
	String key1 = "公司近五年每股收益";
	String key2 = "─────────┴─────┴─────┴─────┴────────";

	@Override
	String dealSection(String section) {
		String ret = null;
		int i1 = section.indexOf(key1);
		int i2 = section.indexOf(key2, i1);
		if (i1 > -1 && i2 > -1) {
			ret = section.substring(0, i2 + key2.length());
		} else {
			System.out.println("[Error] LatestInfoDataSectionHandler: Can not find the key word in data. return unchanged string.");
			ret = section;
		}
		return ret;
	}
}
