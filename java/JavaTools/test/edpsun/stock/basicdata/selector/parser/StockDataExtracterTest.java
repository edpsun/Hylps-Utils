/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edpsun.stock.basicdata.selector.Instants;
import edpsun.stock.basicdata.selector.TestAsist;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 5:04:41 PM
 */
public class StockDataExtracterTest {
	StockDataExtracter sde = null;
	private File pwd;
	private File stockFile = null;

	@Before
	public void setUp() throws Exception {
		pwd = new File(TestAsist.RES_STOCK_DIR);
		stockFile = new File(pwd, "sz/002243.Txt");
		sde = new StockDataExtracter();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExtractBasicData() throws Exception {
		HashMap<String, Object> map = sde.extractBasicData(stockFile, StockFileParser.headerList);

		for (Iterator<String> ie = StockFileParser.headerList.iterator(); ie.hasNext();) {
			String key = ie.next();
			assertTrue(map.containsKey(key));
		}

		assertEquals(map.get(Instants.DATA_MAP_ENTRY_NAME), "通产丽星");
		assertEquals(map.get(Instants.DATA_MAP_ENTRY_CODE), "002243");
	}
}
