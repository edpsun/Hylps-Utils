/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import java.util.Map;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 1:29:34 PM
 */
public class DefaultDataSectionHandler extends DataSectionHandler {

	@Override
	public void dealSection(Map<String, Object> map) {
		System.out.println("[INFO] DefaultDataSectionHandler echo original msg.");
	}

}
