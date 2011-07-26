/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 3:45:42 PM
 */
public class AnalyzerManagerTest {
	AnalyzerManager analyzer = null;
	private File pwd;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		analyzer = new AnalyzerManager();
		pwd = new File(TestAsist.RES_STOCK_DIR);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
