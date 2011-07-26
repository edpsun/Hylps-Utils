/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edpsun.stock.basicdata.selector.Instants;
import edpsun.stock.basicdata.selector.bean.AnalyzeVO;
import edpsun.stock.basicdata.selector.bean.HolderVO;
import edpsun.stock.basicdata.selector.parser.StockFileParser;
import edpsun.stock.basicdata.selector.parser.StockInfo;
import edpsun.stock.basicdata.selector.utils.HtmlUtils;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 4:40:32 PM
 */
public abstract class HolderAnalyzer implements Analyzer {
	static String detail_url = "http://stockhtm.finance.qq.com/sstock/ggcx/%s.shtml";
	static String stdiv = "<div stockid=\"%s\" id=\"%d\" class=\"stdiv\" tags=\"%s\" hQnum=\"%d\" hCRate=\"%f\" elid=\"%s\" >";

	public void exportReport(File path, String fileName, ArrayList<AnalyzeVO> avoList,
			HashMap<String, StockInfo> stockMap) {
		BufferedWriter bww = null;
		File file = new File(path, "report-" + fileName);

		try {
			bww = new BufferedWriter(new FileWriter(file));

			bww.write(HtmlUtils.getHeader(fileName));
			bww.write("\n\n");
			bww.write(getFilterHmtl());

			StringBuilder total = new StringBuilder();
			total.append("<span id='total'></span>").append("\n");
			total.append("<br><br>").append("\n");
			total.append("<div id=\"listdiv\"></div><br><br>").append("\n");
			bww.write(total.toString());

			Collections.sort(avoList);
			StringBuilder sb = new StringBuilder();
			int p = 0;
			for (AnalyzeVO vo : avoList) {
				StockInfo stockInfo = stockMap.get(vo.getCode());
				String tags = stockInfo.getTags();
				sb.setLength(0);
				sb.append("[" + (++p) + "] ").append(stockInfo.getName()).append("<br>\n    ");
				sb.append(vo).append("<br>\n");
				sb.append("    Tags: ").append(tags).append("<br>\n");
				System.out.println(sb.toString());

				sb.append("    Busi: ").append(stockInfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_MAIN_BUSINESS))
						.append("\n");

				sb.append("<br> <a target=\"_blank\" href=\"choose/" + stockInfo.getCode() + "_" + stockInfo.getName()
						+ ".html\">概要</a>\n");
				sb.append("&nbsp;&nbsp;&nbsp; <a target=\"_blank\"  href=\"" + String.format(detail_url, vo.getCode())
						+ "\">详细</a>\n");
				sb.append("&nbsp;&nbsp;&nbsp; <a href=\"javascript:void(0);\" onclick=\"toggleDisplay('" + vo.getCode()
						+ "')\">背景</a>\n");

				sb.append("<hr><ul>\n");
				HolderVO[] hvos = stockInfo.getHolderVOs();
				int length = hvos.length;
				int LEN = 8;
				if (length > LEN) {
					int aaNum = vo.getAverageAmountUpQNum();
					int hNum = vo.getHolderUpQNum();

					int maxQnum = hNum > aaNum ? hNum : aaNum;
					if (maxQnum > LEN) {
						length = maxQnum;
					} else {
						length = LEN;
					}

				}
				for (int i = 0; i < length; i++) {
					sb.append("<li>  [-] " + hvos[i]).append("</li>\n");
				}
				sb.append("</ul>\n");

				sb.append("<div id=\"" + vo.getCode() + "\" style=\"display:none\">\n");
				sb.append("<pre>\n");
				sb.append(stockInfo.getStockInfoValue(Instants.DATA_MAP_ENTRY_SUMMARY_DATA));
				sb.append("</pre>\n");
				sb.append("</div>\n");

				String exportListID = null;
				if (vo.getCode().startsWith("6")) {
					exportListID = "1";
				} else {
					exportListID = "0";
				}
				exportListID += vo.getCode();

				sb.insert(0, String.format(stdiv, vo.getCode(), p, tags, vo.getHolderUpQNum(), vo.getHolderChange(),
						exportListID));
				sb.append("\n</div>");
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

	static String[] keys = new String[] { Instants.DATA_MAP_ENTRY_LATEST_INFO, Instants.DATA_MAP_ENTRY_HOLDER_DATA,
			Instants.DATA_MAP_ENTRY_SUMMARY_DATA };

	protected void exportStockCompactInfo(File path, StockInfo stockInfo) throws IOException {

		String fileName = stockInfo.getCode() + "_" + stockInfo.getName() + ".html";

		File f = new File(path, fileName);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));

			bw.write(HtmlUtils.getHeader(fileName));
			bw.write("\n\n");

			bw.write("<pre>");
			for (String header : keys) {
				String s = (String) stockInfo.getStockInfoObject(header);
				if (s != null) {
					bw.write(s);
					bw.write("\n");
				}
			}
			bw.write("</pre>");
			bw.write(HtmlUtils.getFooter());
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				//ignore;
			}
		}
	}

	private String getFilterHmtl() {
		StringBuilder sb = new StringBuilder();
		sb.append("<span title=\"ID\">ID:</span>").append("\n");
		sb.append("<input type=\"text\" name=\"stockid\" value=\"\"/>").append("\n");
		sb.append("<br>").append("\n");
		
		sb.append("<span title=\"Tags\">Tags:</span>").append("\n");
		sb.append("<input type=\"text\" name=\"tags\" value=\"\"/>").append("\n");
		sb.append("<br>").append("\n");

		sb.append("<span title=\"Holder num change rate lower limit.\">Rate >=</span>").append("\n");
		sb.append("<input type=\"text\" name=\"change_rate_min\" value=\"\"/>").append("\n");
		sb.append("<br>").append("\n");

		sb.append("<span title=\"Holder num change rate upper limit.\">Rate <=</span>").append("\n");
		sb.append("<input type=\"text\" name=\"change_rate_max\" value=\"\"/>").append("\n");
		sb.append("<br>").append("\n");

		sb.append("<span title=\"Holder change quarter num lower limit.\">Qnum >=</span>").append("\n");
		sb.append("<input type=\"text\" name=\"qnum_min\" value=\"\"/>").append("\n");
		sb.append("<br>").append("\n");

		sb.append("<span title=\"Holder change quarter num upper limit.\">Qnum <=</span>").append("\n");
		sb.append("<input type=\"text\" name=\"qnum_max\" value=\"\"/>").append("\n");
		sb.append("<br>").append("\n");
		sb.append("<button type=\"button\" id=\"filterButton\">Filter</button>").append("\n");
		sb.append("<button type=\"button\" id=\"cleanButton\">Reset</button>").append("\n");
		sb.append("<button type=\"button\" id=\"exportButton\">Export</button>").append("\n");
		sb.append("<button type=\"button\" id=\"showPicButton\">ShowPic</button>").append("\n");
		
		return sb.toString();
	}
}
