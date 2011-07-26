/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector;

import java.io.File;

import edpsun.stock.basicdata.selector.analyzer.Analyzer;
import edpsun.stock.basicdata.selector.analyzer.HolderDataAnalyzer;
import edpsun.stock.basicdata.selector.analyzer.HolderDataCalculator;
import edpsun.stock.basicdata.selector.utils.DBHelper;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 3:26:04 PM
 */
public class BDA {
	public static final String RES_STOCK_DIR = "test/res/stock";
	public static File db4o = null;
	public static File pwd = null;
	public static File buildout = null;
	public static File chosen = null;

	public void start(String path, String workDir) {
		prepareWorkingDir(workDir);
		db4o = new File(path, Instants.WORK_FILE_DB4O);

		DBHelper dbHelper = new DBHelper(db4o);

		if (!dbHelper.dbExists()) {
			System.out.println("[INFO] Persist stock into db4o.");
			StockFileManager sfm = new StockFileManager();
			sfm.persist(new File(path), dbHelper);
		} else {
			System.out.println("[INFO] Already exists. NO need to persist stock into db4o.");
		}

		//-------
		System.out.println("[INFO] Analyze stock.");
		AnalyzerManager analyzerManager = new AnalyzerManager();
		analyzerManager.setDBHelper(dbHelper);

		HolderDataCalculator holderDataCalc = new HolderDataCalculator();
		analyzerManager.addAnalyzer(holderDataCalc);

		Analyzer holderDataAnalyzer = new HolderDataAnalyzer();
		analyzerManager.addAnalyzer(holderDataAnalyzer);

		analyzerManager.doWork();
	}

	private void prepareWorkingDir(String _workDir) {
		if (_workDir == null) {
			_workDir = "BDA_WORK_HOME";
		}

		File work = new File(_workDir);
		if (work.exists()) {
			System.out.println("[Error] work dir already exists! ");
			System.exit(-1);
		}

		pwd = work;
		work.mkdirs();
		buildout = new File(work, Instants.WORK_DIR_BUILDOUT);
		buildout.mkdirs();
		chosen = new File(work, Instants.WORK_DIR_CHOSEN);
		chosen.mkdirs();
	}

	public static void main(String[] args) {
		String path = RES_STOCK_DIR;
		String workDir = null;

		if (args.length == 1) {
			path = args[0];
		} else if (args.length == 2) {
			path = args[0];
			workDir = args[1];
		}

		File f = new File(path);

		if (!f.exists()) {
			System.out.println("[Error] Dir not exist!");
			System.exit(-1);
		}

		if (!f.isDirectory()) {
			System.out.println("[Error] Path is not a dir!");
			System.exit(-1);
		}

		BDA main = new BDA();
		main.start(path, workDir);
	}
}
