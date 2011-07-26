/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.exception;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 16, 2010 10:27:17 PM
 */
public class AnalyzeException extends Exception {
	public AnalyzeException(String s) {
		super(s);
	}

	public AnalyzeException(Exception e) {
		super(e);
	}
}
