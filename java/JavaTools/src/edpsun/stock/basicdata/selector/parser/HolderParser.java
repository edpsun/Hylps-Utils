/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edpsun.stock.basicdata.selector.bean.HolderVO;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 2, 2011 12:03:24 AM
 */
public class HolderParser {

	String startkey = "┼";
	String endkey = "┴";

	public HolderVO[] parseHolderData(String data) throws IOException {
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

		List<HolderVO> list = new ArrayList<HolderVO>();
		float f = 0f;
		String[] ts = null;
		for (int i = 0; i < l.size(); i++) {
			line = l.get(i);

			ts = line.split("│");

			if (ts.length != 6) {
				continue;
			}

			list.add(new HolderVO(ts[0].trim(), ts[2].trim(), ts[3].trim(), ts[4].trim(), ts[1].trim()));
		}

		//		int p = 0;
		//		int tmp = 0;
		//		for (HolderVO vo : list) {
		//			if (vo.getHolderNumber() > tmp) {
		//				tmp = vo.getHolderNumber();
		//				p++;
		//			} else {
		//				break;
		//			}
		//		}
		//
		//		int size = 5;
		//		if (p >= 6) {
		//			size = p;
		//		} else {
		//			if (list.size() < 5) {
		//				size = list.size();
		//			}
		//		}
		//
		//		list = list.subList(0, size);

		HolderVO[] hvos = list.toArray(new HolderVO[list.size()]);
		return hvos;
	}
}
