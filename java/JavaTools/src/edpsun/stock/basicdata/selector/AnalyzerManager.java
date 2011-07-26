/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

import edpsun.stock.basicdata.selector.analyzer.Analyzer;
import edpsun.stock.basicdata.selector.bean.HolderVO;
import edpsun.stock.basicdata.selector.parser.StockInfo;
import edpsun.stock.basicdata.selector.utils.DBHelper;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 16, 2010 9:40:34 PM
 */
public class AnalyzerManager {
	private DBHelper dbHelper;
	private List<Analyzer> analyzerList = new ArrayList<Analyzer>();

	public void doWork() {
		ObjectContainer db = dbHelper.getObejctContainer();

		try {
			Query query = db.query();
			query.constrain(StockInfo.class);
			List<StockInfo> list = query.execute();

			int i = 0;
			StockInfo stockInfo = null;
			for (Iterator<StockInfo> iterator = list.iterator(); iterator.hasNext();) {
				try {
					Thread.sleep(200);
					stockInfo = iterator.next();
					System.out.println("[" + (i++) + "]==============================================================");

					System.out.println("    [-] Name : " + stockInfo.getName());
					System.out.println("    [-] Code : " + stockInfo.getCode());

					if (stockInfo.getName().indexOf("ST") > -1) {
						System.out.println("    [-] Skip ST from report.");
						continue;
					}

					HolderVO[] hvos = stockInfo.getHolderVOs();

					if (hvos.length < 5) {
						System.out.println("    [-] Skip Qnum < 5");
						continue;
					}

					if (hvos.length >= 3) {
						if (hvos[0].getDate().equals(hvos[1].getDate()) || hvos[1].getDate().equals(hvos[2].getDate())) {
							System.out.println("    [-] Skip AB or AH");
							continue;
						}
					}

					for (Iterator<Analyzer> ie2 = analyzerList.iterator(); ie2.hasNext();) {
						ie2.next().analyze(stockInfo);
					}
				} catch (Exception e) {
					//System.out.println("[Error] Skip current!!! Msg:" + e.getMessage());
					e.printStackTrace();
				}
			}

			//generate reports
			for (Iterator<Analyzer> ie2 = analyzerList.iterator(); ie2.hasNext();) {
				ie2.next().report();
			}

		} finally {
			db.close();
		}

	}

	public void addAnalyzer(Analyzer analyzer) {
		analyzerList.add(analyzer);
	}

	public void removeAnalyzer(Analyzer analyzer) {
		analyzerList.remove(analyzer);
	}

	public void setDBHelper(DBHelper _dbHelper) {
		this.dbHelper = _dbHelper;
	}

}
