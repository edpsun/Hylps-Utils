/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edpsun.stock.basicdata.selector.Instants;
import edpsun.stock.basicdata.selector.TestAsist;
import edpsun.stock.basicdata.selector.bean.HolderVO;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 2, 2011 12:15:25 AM
 */
public class HolderParserTest {

	private File pwd;
	private File stockFile = null;

	@Before
	public void setUp() throws Exception {
		pwd = new File(TestAsist.RES_STOCK_DIR);
		stockFile = new File(pwd, "sz/002243.Txt");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edpsun.stock.basicdata.selector.parser.HolderParser#parseHolderData(java.lang.String)}.
	 */
	@Test
	public void testParseHolderData() throws Exception {
		StockFileParser parser = new StockFileParser();
		StockInfo sinfo = parser.parse(stockFile);

		HolderParser hparser = new HolderParser();
		HolderVO[] vos = hparser.parseHolderData((String) sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_HOLDER_DATA));

		assertEquals(vos.length, 12);

		assertEquals(vos[0].getDate(), "2011-03-31");
		assertEquals(vos[0].getVolum(), "A10509");
		assertEquals(vos[0].getHolderNumber(), 9190);
		assertEquals(vos[0].getHolderAmount(), "11435");
		//assertEquals(vos[0].getChangeRate(), "48.78");

		assertEquals(vos[vos.length - 1].getDate(), "2008-06-30");
		assertEquals(vos[vos.length - 1].getVolum(), "A3240");
		assertEquals(vos[vos.length - 1].getHolderNumber(), 16902);
		assertEquals(vos[vos.length - 1].getHolderAmount(), "1917");
		//assertEquals(vos[vos.length - 1].getChangeRate(), "--");

	}

}
