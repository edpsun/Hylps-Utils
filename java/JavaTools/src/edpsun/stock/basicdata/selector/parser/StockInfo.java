/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edpsun.stock.basicdata.selector.bean.AnalyzeVO;
import edpsun.stock.basicdata.selector.bean.HolderVO;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 1, 2011 4:25:00 PM
 */
public class StockInfo {
	private String name;
	private String code;
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private HolderVO[] holderVOs = null;
	private AnalyzeVO analyzeVo = null;
	private ArrayList<String> tags = new ArrayList<String>();

	public StockInfo(String _name, String _code) {
		this.name = _name;
		this.code = _code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public HolderVO[] getHolderVOs() {
		return holderVOs;
	}

	public void setHolderVOs(HolderVO[] holderVOs) {
		this.holderVOs = holderVOs;
	}

	public Object getStockInfoObject(String key) {
		if (dataMap.containsKey(key)) {
			return dataMap.get(key);
		} else {
			return null;
		}
	}

	public void setStockInfoObject(String key, Object o) {
		dataMap.put(key, o);
	}

	public String getStockInfoValue(String key) {
		if (dataMap.containsKey(key)) {
			return dataMap.get(key).toString();
		} else {
			return null;
		}
	}

	public AnalyzeVO getAnalyzeVo() {
		return analyzeVo;
	}

	public void setAnalyzeVo(AnalyzeVO analyzeVo) {
		this.analyzeVo = analyzeVo;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name:").append(name).append("\n");
		sb.append("code:").append(code).append("\n");

		if (holderVOs != null) {
			sb.append("Holder VO size:").append(holderVOs.length).append("\n");
			for (int i = 0; i < holderVOs.length; i++) {
				sb.append(holderVOs[i]).append("\n");
			}
		}

		//		Set<String> keyset = dataMap.keySet();
		//		for (String key : keyset) {
		//			sb.append(key).append("--------------\n");
		//			sb.append(dataMap.get(key)).append("\n");
		//		}

		if (tags != null) {
			sb.append("Tags:");
			for (String string : tags) {
				sb.append(string + ",");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public void addTag(String tag) {
		if (tags == null) {
			tags = new ArrayList<String>();
		}

		if (!tags.contains(tag)) {
			tags.add(tag);
		}
	}

	public String getTags() {
		StringBuilder sb = new StringBuilder();
		for (String tag : tags) {
			sb.append(tag).append(", ");
		}
		return sb.toString();
	}
}
