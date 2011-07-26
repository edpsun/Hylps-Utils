package edpsun.stock.basicdata.selector.utils;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 9, 2011 9:44:15 PM
 */
public class MathTool {

	public static VO calcDataSeriesMTThresholder(Float[] data, float thresholder, int ignoreTimes) {
		if (data == null || data.length == 0) {
			return new VO(0, 0f);
		}

		int num = 0;
		int exception = 0;
		Float sum = 0f;
		for (int i = 0; i < data.length; i++) {
			if (data[i] > thresholder) {
				if (data[i] < 0) {
					exception++;
				}
				if (exception > ignoreTimes) {
					break;
				}

				num++;
				sum += data[i];
			} else {
				break;
			}
		}

		VO vo = new VO(num, sum / 100);
		return vo;
	}

	public static VO calcDataSeriesUp(Float[] data, float ignoreRate) {
		if (data == null || data.length == 0) {
			return new VO(0, 0f);
		}

		Float current = data[0];
		int continuousNum = 0;

		for (int i = 1; i < data.length; i++) {
			if (current.compareTo(data[i]) < 0) {//current < data [i]
				continuousNum++;
				current = data[i];
			} else {//current >= data [i]
				if (i == 1) {
					Float deltaRate = (current - data[i]) / data[i];
					if (deltaRate < ignoreRate) {
						continue;
					}
				}

				break;
			}
		}

		Float change = (data[0] / current);
		VO vo = new VO(continuousNum, change);

		return vo;
	}

	public static class VO {
		int QNum = -1;
		float change = 0f;

		public VO(int qNum, float change) {
			super();
			QNum = qNum;
			this.change = change;
		}

		public int getQNum() {
			return QNum;
		}

		public void setQNum(int qNum) {
			QNum = qNum;
		}

		public float getChange() {
			return change;
		}

		public void setChange(float change) {
			this.change = change;
		}

		@Override
		public String toString() {

			return "Num: " + QNum + "  Change: " + change;
		}
	}
}
