/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 1:29:34 PM
 */
public class DefaultDataSectionHandler extends DataSectionHandler {

	@Override
	String dealSection(String section) {
		System.out.println("[INFO] DefaultDataSectionHandler echo original msg.");
		return section;
	}

}
