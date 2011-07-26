/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.analyzer;

import java.io.IOException;

import edpsun.stock.basicdata.selector.bean.AnalyzeVO;
import edpsun.stock.basicdata.selector.bean.HolderVO;
import edpsun.stock.basicdata.selector.parser.StockInfo;
import edpsun.stock.basicdata.selector.utils.MathTool;
import edpsun.stock.basicdata.selector.utils.MathTool.VO;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 4:40:32 PM
 */
public class HolderDataCalculator extends HolderAnalyzer {
	StockInfo stockInfo = null;
	static float THRESHOLDER_HOLDER_INCREASE_RATE_IGNORE = 0.05f;
	static float THRESHOLDER_AVG_AMOUNT_INCREASE_RATE_AS_INCREASE = -6f;
	static int THRESHOLDER_AVG_AMOUNT_DECREASE_TIMES = 2;

	@Override
	public void analyze(StockInfo _stockInfo) throws Exception {
		this.stockInfo = _stockInfo;
		AnalyzeVO avo = new AnalyzeVO(stockInfo.getCode());
		stockInfo.setAnalyzeVo(avo);

		calcHolderAmount();
		calcHolderNumber();
	}

	private void calcHolderAmount() {
		HolderVO[] holderVOs = stockInfo.getHolderVOs();
		Float[] amountChanges = new Float[holderVOs.length];
		for (int i = 0; i < holderVOs.length; i++) {
			amountChanges[i] = new Float(holderVOs[i].getChangeRate());
		}

		VO vo = MathTool.calcDataSeriesMTThresholder(amountChanges, THRESHOLDER_AVG_AMOUNT_INCREASE_RATE_AS_INCREASE,
				THRESHOLDER_AVG_AMOUNT_DECREASE_TIMES);
		stockInfo.getAnalyzeVo().setAverageAmountChange(vo.getChange());
		stockInfo.getAnalyzeVo().setAverageAmountUpQNum(vo.getQNum());
	}

	private void calcHolderNumber() {
		HolderVO[] holderVOs = stockInfo.getHolderVOs();
		Float[] holderNums = new Float[holderVOs.length];
		for (int i = 0; i < holderVOs.length; i++) {
			holderNums[i] = new Float(holderVOs[i].getHolderNumber());
		}

		VO vo = MathTool.calcDataSeriesUp(holderNums, THRESHOLDER_HOLDER_INCREASE_RATE_IGNORE);
		stockInfo.getAnalyzeVo().setHolderChange(vo.getChange());
		stockInfo.getAnalyzeVo().setHolderUpQNum(vo.getQNum());
	}

	@Override
	public void report() {
	}
}
