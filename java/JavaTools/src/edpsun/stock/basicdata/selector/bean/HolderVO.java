/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.bean;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 8:40:11 PM
 */
public class HolderVO {
	String date = "";
	String holderNumber = "";
	String holderAmount = "";
	String changeRate = "";
	String volum = "";

	public HolderVO(String s, String hnum, String amount, String rate, String vol) {
		this.date = s;
		this.holderAmount = amount;
		this.holderNumber = hnum;
		this.changeRate = rate;
		this.volum = vol;
	}

	public String getVolum() {
		return volum.replace(" ", "");
	}

	public void setVolum(String volum) {
		this.volum = volum;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getHolderNumber() {
		return Integer.valueOf(holderNumber);
	}

	public void setHolderNumber(String holderNumber) {
		this.holderNumber = holderNumber;
	}

	public String getHolderAmount() {
		return holderAmount;
	}

	public void setHolderAmount(String holderAmount) {
		this.holderAmount = holderAmount;
	}

	public float getChangeRate() {
		Float f = null;
		try {
			f = Float.valueOf(changeRate);
		} catch (Exception e) {
			System.out.println("Convert " + changeRate + " to " + 0);
			f = 0f;
		}

		return f;
	}

	public void setChangeRate(String c) {
		this.changeRate = c;
	}

	@Override
	public String toString() {
		String deli = " | ";
		StringBuilder sb = new StringBuilder();
		sb.append(date).append(deli);
		sb.append(volum).append(deli);
		sb.append(holderNumber).append(deli);
		sb.append(holderAmount).append(deli);
		sb.append(changeRate);

		float cr = getChangeRate();

		if (cr > 80) {
			sb.append(" ****");
		} else if (cr > 60) {
			sb.append(" ***");
		} else if (cr > 40) {
			sb.append(" **");
		} else if (cr > 20) {
			sb.append(" *");
		}
		sb.append(deli);
		return sb.toString();
	}
}