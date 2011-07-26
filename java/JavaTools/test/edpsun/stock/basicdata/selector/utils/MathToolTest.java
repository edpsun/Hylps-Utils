/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edpsun.stock.basicdata.selector.utils.MathTool.VO;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 9, 2011 11:34:06 PM
 */
public class MathToolTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edpsun.stock.basicdata.selector.utils.MathTool#calcDataSeriesMTThresholder(java.lang.Float[], float, int)}.
	 */
	@Test
	public void testCalcDataSeriesMTThresholder() {
		Float[] f = new Float[] { 151.56f, -2.78f, 9.26f, 1.33f, -35.32f, -23.59f, -3.20f, 83.05f, };
		VO vo = MathTool.calcDataSeriesMTThresholder(f, -6, 2);
		assertEquals(vo.getQNum(), 4);
	}

}
