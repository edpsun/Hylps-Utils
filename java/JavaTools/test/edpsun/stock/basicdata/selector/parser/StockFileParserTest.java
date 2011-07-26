package edpsun.stock.basicdata.selector.parser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edpsun.stock.basicdata.selector.Instants;
import edpsun.stock.basicdata.selector.TestAsist;

public class StockFileParserTest {
	private File pwd;
	private File stockFile = null;

	@Before
	public void setUp() throws Exception {
		pwd = new File(TestAsist.RES_STOCK_DIR);
		stockFile = new File(pwd, "sz/002243.Txt");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParse() throws Exception {
		StockFileParser parser = new StockFileParser();
		StockInfo sinfo = parser.parse(stockFile);

		assertEquals(sinfo.getName(), "通产丽星");
		assertEquals(sinfo.getCode(), "002243");

		assertNotNull(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_LATEST_INFO));

		assertNotNull(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_MAIN_BUSINESS));
		assertTrue(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_MAIN_BUSINESS)
				.indexOf("行业:塑料制造业 主营范围:化妆品塑料包装的生产和销售") > -1);

		assertNotNull(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_SUMMARY_DATA));
		assertTrue(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_SUMMARY_DATA).indexOf("经营范围") > -1);
		assertTrue(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_SUMMARY_DATA).indexOf("主营业务") > -1);
		assertTrue(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_SUMMARY_DATA).indexOf("公司背景") > -1);

		assertNotNull(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_HOLDER_DATA));
		assertTrue(sinfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_HOLDER_DATA).indexOf(
				"─────┼───────┼─────┼─────┼────┼─────") > -1);
	}
}
