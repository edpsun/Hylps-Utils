/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

import java.util.HashMap;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 1:23:55 PM
 */
public abstract class DataSectionHandler {
	private static String SECTION_HANDLER_LATEST_INFO = "LatestInfoDataSectionHandler";
	private static String SECTION_HANDLER_HOLDER = "HolderDataSectionHandler";
	private static String SECTION_HANDLER_SUMMARY = "SummaryDataSectionHandler";

	private static HashMap<String, String> handlerMapper = new HashMap<String, String>();
	static {
		handlerMapper.put(Instants.DATA_SECTION_HEADER_LATEST, SECTION_HANDLER_LATEST_INFO);
		handlerMapper.put(Instants.DATA_SECTION_HEADER_HOLDER, SECTION_HANDLER_HOLDER);
		handlerMapper.put(Instants.DATA_SECTION_HEADER_SUMMARY, SECTION_HANDLER_SUMMARY);
	}

	abstract String dealSection(String section);

	public static DataSectionHandler getSectionHandler(String key) throws Exception {
		String name = handlerMapper.get(key);
		if (name == null) {
			name = "DefaultDataSectionHandler";
		}

		String pname = DataSectionHandler.class.getPackage().getName();

		String handlerName = pname + "." + name;
		//System.out.println("Section handler : " + handlerName);
		DataSectionHandler h = null;
		h = (DataSectionHandler) Class.forName(handlerName).newInstance();
		return h;
	}
}
