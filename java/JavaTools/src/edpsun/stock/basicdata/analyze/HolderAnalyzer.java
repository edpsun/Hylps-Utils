/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 17, 2010 7:49:05 PM
 */
public class HolderAnalyzer {
	static int LEVEL_BASE = 5;
	static int LEVEL_STEP = -1;

	static float TRESHOLDER_SAME_VOLUM_HOLDER_CHANGE_RATE = 0.701f;
	static float TRESHOLDER_INCREASING_HOLDER_CHANGE_RATE = 0.702f;
	static float TRESHOLDER_AMOUNT_INCREASE_PCT = 0.64f;
	static float TRESHOLDER_RATE_DECREASE_PCT = 0.05f;
	static float TRESHOLDER_CANNON = 30;
	static String detail_url = "http://stockhtm.finance.qq.com/sstock/ggcx/%s.shtml";
	HashMap<String, Object> alldataMap = null;

	static {
		String s1 = System.getProperty("SAME_VOLUM_HOLDER_CHANGE_RATE");
		if (s1 != null) {
			TRESHOLDER_SAME_VOLUM_HOLDER_CHANGE_RATE = Float.valueOf(s1);
		}

		String s2 = System.getProperty("INCREASING_HOLDER_CHANGE_RATE");
		if (s2 != null) {
			TRESHOLDER_INCREASING_HOLDER_CHANGE_RATE = Float.valueOf(s2);
		}

		String s3 = System.getProperty("TRESHOLDER_CANNON");
		if (s3 != null) {
			TRESHOLDER_CANNON = Float.valueOf(s3);
		}

		System.out.println("TRESHOLDER_SAME_VOLUM_HOLDER_CHANGE_RATE:" + TRESHOLDER_SAME_VOLUM_HOLDER_CHANGE_RATE);
		System.out.println("TRESHOLDER_INCREASING_HOLDER_CHANGE_RATE:" + TRESHOLDER_INCREASING_HOLDER_CHANGE_RATE);
		System.out.println("TRESHOLDER_CANNON:" + TRESHOLDER_CANNON);
	}

	static String listPrefix = "SL:";

	String startkey = "┼";
	String endkey = "┴";

	HashMap<STVo, HolderVO[]> havoMap = new HashMap<STVo, HolderVO[]>();
	ArrayList<STVo> stvoList = new ArrayList<STVo>();

	public void exportReport(File file) {
		BufferedWriter bww = null;
		try {

			bww = new BufferedWriter(new FileWriter(file));

			bww.write(HtmlUtils.getHeader("report"));
			bww.write("\n\n");
			Collections.sort(stvoList);
			StringBuilder sb = new StringBuilder();
			int p = 0;
			for (STVo vo : stvoList) {
				sb.setLength(0);
				sb.append("[" + (++p) + "] ");
				sb.append(vo).append("\n");
				System.out.println(sb.toString());
				
				sb.append("<!--").append(listPrefix);
				if (vo.getCode().startsWith("6")) {
					sb.append("1");
				} else {
					sb.append("0");
				}
				sb.append(vo.getCode()).append("-->\n");

				sb.append("<br> <a target=\"_blank\" href=\"choose/" + vo.getCode() + "_" + vo.getName()
						+ ".html\">概要</a>\n");
				sb.append("&nbsp;&nbsp;&nbsp; <a target=\"_blank\"  href=\"" + String.format(detail_url, vo.getCode())
						+ "\">详细</a>\n");
				sb.append("&nbsp;&nbsp;&nbsp; <a href=\"javascript:void(0);\" onclick=\"toggleDisplay('" + vo.getCode() + "')\">背景</a>\n");
		

				sb.append("<hr><ul>\n");
				HolderVO[] hvos = havoMap.get(vo);
				for (int i = 0; i < hvos.length; i++) {
					sb.append("<li>  [-] " + hvos[i]).append("</li>\n");
				}
				sb.append("</ul>");

				sb.append("<div id=\"" + vo.getCode() + "\" style=\"display:none\">\n");
				sb.append("<pre>\n");
				sb.append(vo.getBackGround());
				sb.append("</pre>\n");
				sb.append("</div>\n");
				sb.append("<br>");

				bww.write(sb.toString());
				bww.write("\n");
			}
			bww.write(HtmlUtils.getFooter());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bww.close();
			} catch (IOException e) {
				//ignore
			}
		}

	}

	public boolean analyze(HashMap<String, Object> dataMap, String name, String code, String mainBusi)
			throws IOException {
		STVo vo = new STVo(name, code, mainBusi);
		alldataMap = dataMap;
		HolderVO[] hvos = getHolderChangeRate(alldataMap.get(Instants.DATA_SECTION_HEADER_HOLDER).toString());
		vo.setBackGround(alldataMap.get(Instants.DATA_SECTION_HEADER_SUMMARY).toString());

		String includeAll = System.getenv("INCLUDE_ALL");
		if (includeAll != null) {
			System.out.println("INCLUDE_ALL is NOT null. Keep all! ");
			
				calPriorityBasedOnHolderNumber(vo, hvos);
				havoMap.put(vo, hvos);
				stvoList.add(vo);
				vo.level = "2";
				return true;
		}
		
		if (hvos.length < 5 || hvos[4].getChangeRate() == 0) {
			System.out.println("[Skip] due to not enough holder items. Name: " + name + "  " + code);
			return false;
		}
		
		String bda_date = System.getenv("DBA_DATE");
		if (bda_date != null) {
			System.out.println("DBA_DATE:" + bda_date + "   ->" + hvos[0].getDate() + " ?"
					+ hvos[0].getDate().indexOf(bda_date));
			if (hvos[0].getDate().indexOf(bda_date) > -1) {
				calPriorityBasedOnHolderNumber(vo, hvos);
				havoMap.put(vo, hvos);
				stvoList.add(vo);
				vo.level = "2";
				return true;
			} else {
				return false;
			}
		}

		calPriorityBasedOnHolderNumber(vo, hvos);
		if (vo.getLevel() == null) {
			calPriorityBasedOnHolderAmount(vo, hvos);
		}

		// search cannon
		if (System.getenv("NO_CANNON") == null) {
			searchCannon(vo, hvos);
		}

		if (vo.getLevel() != null) {
			havoMap.put(vo, hvos);
			stvoList.add(vo);
			return true;
		}

		return false;
	}

	private void searchCannon(STVo vo, HolderVO[] hvos) {
		if (hvos[0].getChangeRate() >= 100 && vo.getLevel() != null) {
			vo.setLevel("0");
			vo.setDesc(vo.getDesc() + "_CANNON");
		}

		if (vo.getLevel() == null) {
			if (hvos[0].getChangeRate() >= TRESHOLDER_CANNON && hvos[1].getChangeRate() >= TRESHOLDER_CANNON) {
				vo.setLevel("9");
				vo.setDesc(vo.getDesc() + "Little_CANNON_2");
			} else if (hvos[0].getChangeRate() >= 10 && hvos[1].getChangeRate() >= 10 && hvos[2].getChangeRate() >= 10) {
				vo.setLevel("9");
				vo.setDesc(vo.getDesc() + "Little_CANNON_3");
			}

		}
	}

	/**
	 * 1. 人数持续减少的季度数 >= 2
	 * 2. 并且 （最小人数/临近的最大人数） < TRESHOLDER_INCREASING_HOLDER_CHANGE_RATE
	 * 
	 * @param vo
	 * @param hvos
	 */
	private void calPriorityBasedOnHolderNumber(STVo vo, HolderVO[] hvos) {

		int pn0 = hvos[0].getHolderNumber();

		int tmp = hvos[0].getHolderNumber();
		int quarterNum = 0;

		if (hvos[0].getDate().equals(hvos[1].getDate()) || hvos[1].getDate().equals(hvos[2].getDate())) {
			System.out.println("[Skip] AB or AH");
			return;
		}

		int exception_times = 0;
		for (int i = 1; i < hvos.length; i++) {
			if (hvos[i].getHolderNumber() > tmp) {
				quarterNum++;
				tmp = hvos[i].getHolderNumber();
			} else {
				//allow exception once if getChangeRate() > -0.05 （集中度减少小 5%）
				if (exception_times == 0 && hvos[i - 1].getChangeRate() > -(TRESHOLDER_RATE_DECREASE_PCT * 100)
						&& i <= 3) {
					exception_times += 1;
					quarterNum++;
					//tmp = hvos[i].getHolderNumber();
					continue;
				}
				break;
			}
		}

		float f = 1.0f * pn0 / tmp;
		if (f < TRESHOLDER_INCREASING_HOLDER_CHANGE_RATE) {
			vo.setLevel("" + (LEVEL_BASE + 3 * LEVEL_STEP));
			vo.setHcRatio(f);

			if (quarterNum >= 5) {
				vo.setDesc(">=5q");
			} else if (quarterNum == 4) {
				vo.setDesc("=4q");
			} else if (quarterNum == 3) {
				vo.setDesc("=3q");
			} else if (quarterNum == 2) {
				vo.setDesc("=2q");
			} else if (quarterNum == 1) {
				vo.setDesc("=1q");
			}

			if (exception_times >= 1) {
				vo.setLevel("" + (LEVEL_BASE + 2 * LEVEL_STEP));
				vo.setDesc(vo.getDesc() + "_1EXPT");
			}
		}
	}

	/**
	 * 1.) （量持续递增(>10%)的季度数/总统计季度数） > TRESHOLDER_AMOUNT_INCREASE_PCT
	 * 2.) 或者 
	 *     流通量不变的前提下，（季度0人数/最大人数） < TRESHOLDER_SAME_VOLUM_HOLDER_CHANGE_RATE
	 * @param vo
	 * @param hvos
	 */
	private void calPriorityBasedOnHolderAmount(STVo vo, HolderVO[] hvos) {

		int plus_num = 0;
		int plus_num_continus = 0;
		boolean continuous = true;

		int pn0 = hvos[0].getHolderNumber();
		int pn1 = 0;
		String v0 = hvos[0].getVolum();

		for (int j = 0; j < hvos.length; j++) {
			if (hvos[j].getChangeRate() > 0) {
				if (hvos[j].getChangeRate() >= -5.0f) {
					plus_num += 1;
				}
				if (continuous) {
					plus_num_continus += 1;
				}
			} else {
				continuous = false;
			}

			if (v0.equals(hvos[j].getVolum()) && j != 0) {
				if (hvos[j].getHolderNumber() > pn1) {
					pn1 = hvos[j].getHolderNumber();
				}
			}
		}

		if (pn1 == 0) {
			vo.setChangeRatio(1);
		} else {
			vo.setChangeRatio((1.0f * pn0 / pn1));
		}

		if (vo.getChangeRatio() < TRESHOLDER_SAME_VOLUM_HOLDER_CHANGE_RATE && (pn0 < hvos[1].getHolderNumber())) {
			vo.setLevel("" + (LEVEL_BASE + 1 * LEVEL_STEP));
			vo.setDesc("SVol");
		} else if (plus_num == hvos.length) {
			vo.setLevel("" + (LEVEL_BASE));
			vo.setDesc("AUP");
		} else if ((1.0f * plus_num / hvos.length) >= TRESHOLDER_AMOUNT_INCREASE_PCT
				&& hvos[hvos.length - 1].getChangeRate() < 0) {
			vo.setLevel("" + (LEVEL_BASE - 1 * LEVEL_STEP));
			vo.setDesc("MUP");
		}

	}

	private HolderVO[] getHolderChangeRate(String data) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
		String line = null;
		ArrayList<String> l = new ArrayList<String>();

		boolean iscollect = false;

		try {
			while ((line = br.readLine()) != null) {

				if (line.indexOf(startkey) > -1) {
					iscollect = true;
					continue;
				}

				if (line.indexOf(endkey) > -1) {
					iscollect = false;
					break;
				}

				if (iscollect) {
					l.add(line);
				}
			}
		} finally {
			br.close();
		}
		//System.out.println("    [-] Holder data item num: " + l.size());

		List<HolderVO> list = new ArrayList<HolderVO>();
		float f = 0f;
		String[] ts = null;
		for (int i = 0; i < l.size(); i++) {
			line = l.get(i);
			//System.out.println(line);
			ts = line.split("│");

			if (ts.length != 6) {
				continue;
			}

			list.add(new HolderVO(ts[0].trim(), ts[2].trim(), ts[3].trim(), ts[4].trim(), ts[1].trim()));
			//System.out.println(hvos[i]);
		}

		int p = 0;
		int tmp = 0;
		for (HolderVO vo : list) {
			if (vo.getHolderNumber() > tmp) {
				tmp = vo.getHolderNumber();
				p++;
			} else {
				break;
			}
		}

		int size = 5;
		if (p >= 6) {
			size = p;
		} else {
			if (list.size() < 5) {
				size = list.size();
			}
		}

		list = list.subList(0, size);
		HolderVO[] hvos = list.toArray(new HolderVO[list.size()]);
		return hvos;
	}
}

class STVo implements Comparable<STVo> {

	public STVo(String n, String c, String m) {
		this.name = n;
		this.code = c;
		this.mainBusi = m;

		if (name == null && code == null) {
			throw new RuntimeException("Name or code should not be NULL");
		}
	}

	String name = null;
	String desc = "";
	String mainBusi = "";
	String code = null;
	String level = null;
	String backGround = "";

	/**
	 * @return the backGround
	 */
	public String getBackGround() {
		return backGround;
	}

	/**
	 * @param backGround the backGround to set
	 */
	public void setBackGround(String backGround) {
		this.backGround = backGround;
	}

	float changeRatio = 1.0f;
	float hcRatio = 1.0f;

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the hcRatio
	 */
	public float getHcRatio() {
		return hcRatio;
	}

	/**
	 * @param hcRatio the hcRatio to set
	 */
	public void setHcRatio(float hcRatio) {
		this.hcRatio = hcRatio;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the changeRatio
	 */
	public float getChangeRatio() {
		return changeRatio;
	}

	/**
	 * @param changeRatio the changeRatio to set
	 */
	public void setChangeRatio(float changeRatio) {
		this.changeRatio = changeRatio;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof STVo)) {
			return false;
		}

		STVo vo = (STVo) obj;

		return name.equals(vo.getName()) && code.equals(vo.getCode());
	}

	@Override
	public int hashCode() {
		return (name + code).hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(STVo vo) {
		if (level.compareTo(vo.getLevel()) == 0) {

			Float cr1 = new Float(changeRatio);
			Float cr11 = new Float(vo.getChangeRatio());

			Float hcr1 = new Float(hcRatio);
			Float hcr11 = new Float(vo.getHcRatio());

			if (hcr1.compareTo(hcr11) == 0) {
				return cr1.compareTo(cr11);
			} else {
				return hcr1.compareTo(hcr11);
			}
		} else {
			return level.compareTo(vo.getLevel());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(name);
		sb.append(" Code: ").append(code);
		sb.append(" Level: ").append(level);
		sb.append(" Desc: ").append(desc);
		sb.append(" Change: ").append(changeRatio);
		sb.append(" Increasing Change: ").append(hcRatio);
		sb.append("\n<br>").append(mainBusi);

		return sb.toString();
	}
}
