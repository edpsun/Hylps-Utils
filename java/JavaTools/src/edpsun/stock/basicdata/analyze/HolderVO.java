/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 8:40:11 PM
 */
public class HolderVO {

	public HolderVO(String s, String hnum, String amount, String rate, String vol) {
		this.date = s;
		this.holderAmount = amount;
		this.holderNumber = hnum;
		this.changeRate = rate;
		this.volum = vol;
	}

	String date = "";
	String holderNumber = "";
	String holderAmount = "";
	String changeRate = "";
	String volum = "";

	/**
	 * @return the volum
	 */
	public String getVolum() {
		return volum.replace(" ", "");
	}

	/**
	 * @param volum the volum to set
	 */
	public void setVolum(String volum) {
		this.volum = volum;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the holderNumber
	 */
	public int getHolderNumber() {
		return Integer.valueOf(holderNumber);
	}

	/**
	 * @param holderNumber the holderNumber to set
	 */
	public void setHolderNumber(String holderNumber) {
		this.holderNumber = holderNumber;
	}

	/**
	 * @return the holderAmount
	 */
	public String getHolderAmount() {
		return holderAmount;
	}

	/**
	 * @param holderAmount the holderAmount to set
	 */
	public void setHolderAmount(String holderAmount) {
		this.holderAmount = holderAmount;
	}

	/**
	 * @return the changeRate
	 */
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

	/**
	 * @param changeRate the changeRate to set
	 */
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
		
		if (getChangeRate() > 80) {
			sb.append(" ****");
		}else if (getChangeRate() > 60) {
			sb.append(" ***");
		} else if (getChangeRate() > 40) {
			sb.append(" **");
		} else if (getChangeRate() > 20) {
			sb.append(" *");
		}
		sb.append(deli);
		return sb.toString();
	}
}