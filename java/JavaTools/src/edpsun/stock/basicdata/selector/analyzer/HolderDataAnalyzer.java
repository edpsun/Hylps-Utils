/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import edpsun.stock.basicdata.selector.BDA;
import edpsun.stock.basicdata.selector.Instants;
import edpsun.stock.basicdata.selector.bean.AnalyzeVO;
import edpsun.stock.basicdata.selector.bean.HolderVO;
import edpsun.stock.basicdata.selector.parser.StockInfo;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 4:40:32 PM
 */
public class HolderDataAnalyzer extends HolderAnalyzer {
	static int LEVEL_BASE = 5;
	static int LEVEL_STEP = -1;
	static float THRESHOLDER_HOLDER_CHANGE_RATE = 0.85f;
	static int THRESHOLDER_HOLDER_QNUM = 2;
	static int THRESHOLDER_AVG_AMOUNT_QNUM = 5;
	static float TRESHOLDER_CANNON = 30;

	HashMap<String, StockInfo> stockMap = new HashMap<String, StockInfo>();
	ArrayList<AnalyzeVO> avoListChosen = new ArrayList<AnalyzeVO>();

    ArrayList<AnalyzeVO> avoListCannon = new ArrayList<AnalyzeVO>();

	StockInfo stockInfo = null;

	@Override
	public void analyze(StockInfo _stockInfo) throws Exception {
		this.stockInfo = _stockInfo;

		boolean b1 = checkHolderNum(stockInfo);
		boolean b2 = checkAvgAmount(stockInfo);

		if (b1 || b2) {
			avoListChosen.add(stockInfo.getAnalyzeVo());
		} else if ("true".equalsIgnoreCase(System.getProperty("INCLUDE_ALL"))) {

			stockInfo.addTag("By_ALL");
			stockMap.put(stockInfo.getCode(), _stockInfo);
			avoListChosen.add(stockInfo.getAnalyzeVo());
			stockInfo.getAnalyzeVo().setLevel("" + (LEVEL_BASE - 4 * LEVEL_STEP));
		}

		findCannon(stockInfo);
	}

	private void findCannon(StockInfo _stockInfo) {
		HolderVO[] holderVOs = stockInfo.getHolderVOs();
		AnalyzeVO avo_origin = _stockInfo.getAnalyzeVo();

		if (avo_origin.getLevel() == null) {
			return;
		}

		if (holderVOs[0].getChangeRate() >= 100) {
			_stockInfo.addTag("CANNON_1");
		} else if (holderVOs[0].getChangeRate() >= TRESHOLDER_CANNON
				&& holderVOs[1].getChangeRate() >= TRESHOLDER_CANNON) {
			_stockInfo.addTag("CANNON_2x30");
		} else if (holderVOs[0].getChangeRate() >= 10 && holderVOs[1].getChangeRate() >= 10
				&& holderVOs[2].getChangeRate() >= 10) {
			_stockInfo.addTag("CANNON_3x10");
		}

	}

	private boolean checkHolderNum(StockInfo _stockInfo) {
		AnalyzeVO avo = _stockInfo.getAnalyzeVo();
		if (avo.getHolderChange() < THRESHOLDER_HOLDER_CHANGE_RATE && avo.getHolderChange() > 0) {
			stockMap.put(avo.getCode(), _stockInfo);
			avo.setLevel("" + (LEVEL_BASE + 3 * LEVEL_STEP));

			int quarterNum = avo.getHolderUpQNum();
			if (quarterNum <= 1) {
				return false;
			} else {
				_stockInfo.addTag("(HolderNum--)=Q" + avo.getHolderUpQNum());
				avo.setSublevel("" + (LEVEL_BASE * 2 + avo.getHolderUpQNum() * LEVEL_STEP));
				return true;
			}
		}
		return false;
	}

	private boolean checkAvgAmount(StockInfo _stockInfo) {
		AnalyzeVO avo = _stockInfo.getAnalyzeVo();

		boolean isInclude = false;
		int l = 0;
		if (avo.getHolderUpQNum() >= THRESHOLDER_HOLDER_QNUM) {
			isInclude = true;
			_stockInfo.addTag("(HolderNum--)=Q" + avo.getHolderUpQNum());

			avo.setSublevel("" + (LEVEL_BASE * 2 + avo.getHolderUpQNum() * LEVEL_STEP));

			l++;
		}

		if (avo.getAverageAmountUpQNum() >= THRESHOLDER_AVG_AMOUNT_QNUM) {
			isInclude = true;
			_stockInfo.addTag("(AverageAmount++)=Q" + avo.getAverageAmountUpQNum());
			avo.setSublevel("" + (LEVEL_BASE * 2 + avo.getAverageAmountUpQNum() * LEVEL_STEP));

			l++;
		}

		if (isInclude) {
			stockMap.put(avo.getCode(), _stockInfo);

			if (avo.getLevel() == null)
				avo.setLevel("" + (LEVEL_BASE + l * LEVEL_STEP));

			return true;
		}
		return false;
	}

	@Override
	public void report() {
		//export stock file
		Set<String> keys = stockMap.keySet();

		for (String key : keys) {
			StockInfo s = stockMap.get(key);

			try {
				exportStockCompactInfo(BDA.chosen, s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		exportReport(BDA.pwd, "chosen.html", avoListChosen, stockMap);
	}
}
