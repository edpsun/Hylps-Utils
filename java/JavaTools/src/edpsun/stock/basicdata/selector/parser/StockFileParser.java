/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edpsun.stock.basicdata.selector.Instants;
import edpsun.stock.basicdata.selector.bean.HolderVO;
import edpsun.stock.basicdata.selector.exception.AnalyzeException;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 4:24:15 PM
 */
public class StockFileParser {
	public static List<String> headerList = new ArrayList<String>();
	private StockDataExtracter bde = new StockDataExtracter();
	static {
		headerList.add(Instants.DATA_SECTION_HEADER_LATEST);
		headerList.add(Instants.DATA_SECTION_HEADER_HOLDER);
		headerList.add(Instants.DATA_SECTION_HEADER_SUMMARY);
	}

	public StockInfo parse(File stockFile) throws AnalyzeException {
		HashMap<String, Object> map = bde.extractBasicData(stockFile, headerList);
		handleDataSection(map);
		StockInfo sInfo = getStockInfoByMetadata(map);
		System.out.println("    [-] name:" + sInfo.getName());
		System.out.println("    [-] code:" + sInfo.getCode());
		return sInfo;
	}

	private void handleDataSection(Map<String, Object> map) {
		DataSectionHandler sectionHander = null;
		for (Iterator<String> ie = headerList.iterator(); ie.hasNext();) {
			String header = ie.next();
			try {
				sectionHander = DataSectionHandler.getSectionHandler(header);
				sectionHander.setHeader(header);

				sectionHander.dealSection(map);
			} catch (Exception e) {
				System.out.println("[Error] handling" + header + ". Msg:" + e.getMessage());
				continue;
			}
		}
	}

	static String[] keys = new String[] { Instants.DATA_MAP_ENTRY_HOLDER_DATA, Instants.DATA_MAP_ENTRY_MAIN_BUSINESS,
			Instants.DATA_MAP_ENTRY_LATEST_INFO, Instants.DATA_MAP_ENTRY_SUMMARY_DATA };

	private StockInfo getStockInfoByMetadata(HashMap<String, Object> _map) {
		String name = (String) _map.get(Instants.DATA_MAP_ENTRY_NAME);
		String code = (String) _map.get(Instants.DATA_MAP_ENTRY_CODE);

		StockInfo sInfo = new StockInfo(name, code);

		for (int i = 0; i < keys.length; i++) {
			sInfo.setStockInfoObject(keys[i], _map.get(keys[i]));
		}

		sInfo.setHolderVOs((HolderVO[]) _map.get(Instants.DATA_MAP_ENTRY_HOLDER_VO_ARRAY));

		return sInfo;
	}
}
