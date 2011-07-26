/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.db4o.ObjectContainer;

import edpsun.stock.basicdata.selector.parser.StockFileParser;
import edpsun.stock.basicdata.selector.parser.StockInfo;
import edpsun.stock.basicdata.selector.utils.DBHelper;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 2, 2011 4:55:28 PM
 */
public class StockFileManager {
	public StockFileManager() {
	}

	public void persist(File stockPaht, DBHelper dbHelper) {
		List<File> list = getStockFileList(stockPaht);

		System.out.println("    [+] Stock Dir   : " + stockPaht.getAbsolutePath());
		System.out.println("    [+] File number : " + list.size());
		System.out.println("==========================================");

		StockFileParser sfParser = new StockFileParser();
		StockInfo stockInfo = null;
		int i = 0;
		File file = null;
		ObjectContainer db = dbHelper.getObejctContainer();
		try {
			for (Iterator<File> iterator = list.iterator(); iterator.hasNext();) {
				try {
					file = iterator.next();
					Thread.sleep(250);
					System.out.println("[" + (++i) + "] Analyzing: " + file.getAbsolutePath());
					System.out.println("-------------------------------------------------------");

					stockInfo = sfParser.parse(file);
					db.store(stockInfo);
				} catch (Exception e) {
					System.out.println("Skip current!!! Msg:" + e.getMessage());
					e.printStackTrace();
				}
			}
			db.commit();
		} finally {
			db.close();
		}
	}

	private List<File> getStockFileList(File p) {
		List<File> list = new ArrayList<File>();

		if (!p.exists()) {
			throw new RuntimeException("Path not exist! " + p.getAbsolutePath());
		}

		if (p.isDirectory()) {
			File[] fs = p.listFiles();
			for (int i = 0; i < fs.length; i++) {
				list.addAll(getStockFileList(fs[i]));
			}
		} else if (p.isFile() && p.getAbsolutePath().toLowerCase().endsWith(".txt")) {
			list.add(p);
		}
		return list;
	}

}
