/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.bean;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 2, 2011 10:43:54 PM
 */
public class AnalyzeVO implements Comparable<AnalyzeVO> {

	public AnalyzeVO(String c) {
		this.code = c;
	}

	public static AnalyzeVO getClone(AnalyzeVO v) {
		AnalyzeVO avo = new AnalyzeVO(v.getCode(), v.getLevel(), v.getSublevel(), v.getHolderUpQNum(),
				v.getHolderChange(), v.getAverageAmountUpQNum(), v.getAverageAmountChange());

		return avo;
	}

	/**
	 * @param code
	 * @param level
	 * @param sublevel
	 * @param holderUpQNum
	 * @param holderChange
	 * @param averageAmountUpQNum
	 * @param averageAmountChange
	 */
	private AnalyzeVO(String code, String level, String sublevel, int holderUpQNum, float holderChange,
			int averageAmountUpQNum, float averageAmountChange) {
		super();
		this.code = code;
		this.level = level;
		this.sublevel = sublevel;
		this.holderUpQNum = holderUpQNum;
		this.holderChange = holderChange;
		this.averageAmountUpQNum = averageAmountUpQNum;
		this.averageAmountChange = averageAmountChange;
	}

	String code = null;
	String level = null;
	String sublevel = "";

	int holderUpQNum = 0;
	int averageAmountUpQNum = 0;

	float holderChange = 1.0f;
	float averageAmountChange = 1.0f;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the sublevel
	 */
	public String getSublevel() {
		return sublevel;
	}

	/**
	 * @param sublevel the sublevel to set
	 */
	public void setSublevel(String sublevel) {
		this.sublevel = sublevel;
	}

	/**
	 * @return the holderUpQNum
	 */
	public int getHolderUpQNum() {
		return holderUpQNum;
	}

	/**
	 * @param holderUpQNum the holderUpQNum to set
	 */
	public void setHolderUpQNum(int holderUpQNum) {
		this.holderUpQNum = holderUpQNum;
	}

	/**
	 * @return the averageAmountUpQNum
	 */
	public int getAverageAmountUpQNum() {
		return averageAmountUpQNum;
	}

	/**
	 * @param averageAmountUpQNum the averageAmountUpQNum to set
	 */
	public void setAverageAmountUpQNum(int averageAmountUpQNum) {
		this.averageAmountUpQNum = averageAmountUpQNum;
	}

	/**
	 * @return the holderChange
	 */
	public float getHolderChange() {
		return holderChange;
	}

	/**
	 * @param holderChange the holderChange to set
	 */
	public void setHolderChange(float holderChange) {
		this.holderChange = holderChange;
	}

	/**
	 * @return the averageAmountChange
	 */
	public float getAverageAmountChange() {
		return averageAmountChange;
	}

	/**
	 * @param averageAmountChange the averageAmountChange to set
	 */
	public void setAverageAmountChange(float averageAmountChange) {
		this.averageAmountChange = averageAmountChange;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof AnalyzeVO)) {
			return false;
		}

		AnalyzeVO vo = (AnalyzeVO) obj;

		return code.equals(vo.getCode());
	}

	@Override
	public int hashCode() {
		return (code).hashCode();
	}

	@Override
	public int compareTo(AnalyzeVO vo) {

		int p = level.compareTo(vo.getLevel());
		if (p != 0) {
			return p;
		}

		Float hc1 = new Float(holderChange);
		Float hc11 = new Float(vo.getHolderChange());
		p = hc1.compareTo(hc11);
		if (p != 0) {
			return p;
		}

		return sublevel.compareTo(vo.getSublevel());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Code: ").append(code);
		sb.append(" Level: ").append(level);
		sb.append(" SubLevel: ").append(sublevel);
		sb.append(" HolderChange: [" + holderUpQNum + "]").append(holderChange);
		sb.append(" AverageAmountChange: [" + averageAmountUpQNum + "]").append(averageAmountChange);

		return sb.toString();
	}

}
