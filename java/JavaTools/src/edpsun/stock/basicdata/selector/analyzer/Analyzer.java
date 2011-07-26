/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.analyzer;

import edpsun.stock.basicdata.selector.parser.StockInfo;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 4:32:32 PM
 */
public interface Analyzer {
	void analyze(StockInfo stockInfo) throws Exception;

	void report();
}
