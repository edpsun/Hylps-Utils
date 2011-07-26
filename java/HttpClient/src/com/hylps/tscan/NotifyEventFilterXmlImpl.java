/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.hylps.tscan.config.Filter;
import com.hylps.tscan.config.Group;
import com.hylps.tscan.config.ObjectFactory;
import com.hylps.tscan.config.Rule;
import com.hylps.tscan.config.TscanConfig;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 14, 2009 1:24:36 PM
 */
public class NotifyEventFilterXmlImpl implements NotifyEventFilter {
	private long timestamp = -1;
	private String config_file = null;
	private boolean isVerbose = false;
	private TscanConfig tsconfig = null;

	public NotifyEventFilterXmlImpl(String _config_file) {
		config_file = _config_file;
		init();
	}

	public void init() {
		File f = new File(config_file);
		timestamp = f.lastModified();
		tsconfig = ObjectFactory.unMarshall(config_file);
	}

	public boolean accept(NotifyEvent ne) {
		if (checkConfigFileChanged()) {
			System.out.println("[INFO] Filter config file Changed ...");
			init();
		}

		Filter f = tsconfig.getFilter();
		if (f == null || f.getGroup().size() == 0) {
			System.out.println("[INFO] NO filter is specified.");
			return true; //accept as default
		}

		List<Group> fs = f.getGroup();

		boolean accept = false;
		if (WatchManager.isDebugMode && isVerbose) {
			System.out.println("        [DEBUG] #############################################################");
		}
		for (Group group : fs) {
			accept = acceptByGroup(group, ne);
			if (WatchManager.isDebugMode && isVerbose) {
				System.out.println("        [DEBUG] Group " + group.getId() + " return " + accept);
			}

			if (!accept) {
				break;
			}
		}

		if (WatchManager.isDebugMode && isVerbose) {
			System.out.println("        [DEBUG] final return " + accept + "   -> " + ne.getEventMessages()[0]);
		}

		return accept;
	}

	public static String INCLUDE = "include";
	public static String EXCLUDE = "exclude";

	private boolean acceptByGroup(Group grp, NotifyEvent ne) {
		List<Rule> lr = grp.getRule();

		String lineDesc = (String) ne.getEventMessages()[0];
		if (lineDesc == null) {
			return false;
		}

		boolean isInlcude = false;
		for (Rule rule : lr) {
			if (INCLUDE.equals(rule.getType())) {
				if (lineDesc.indexOf(rule.getValue()) > -1) {
					isInlcude = true;
				}
			} else if (EXCLUDE.equals(rule.getType())) {
				if (lineDesc.indexOf(rule.getValue()) > -1) {
					if (isVerbose) {
						System.out.println(ne.getEventMessages()[2] + "[new] Refused By " + getRuleDesc(grp, rule)
								+ " -> " + ne.getEventMessages()[0]);
					}
					return false;
				}
			} else {
				System.out.println("[WARN] ignore UNKNOWn rule type: " + rule.getType());
			}
		}

		if (isVerbose && !isInlcude) {
			System.out.println(ne.getEventMessages()[2] + "[new] Refused By [Group " + grp.getId() + ":includes]"
					+ " -> " + ne.getEventMessages()[0]);
		}
		return isInlcude;
	}

	public void showFilter() {
		Filter f = tsconfig.getFilter();
		if (f == null || f.getGroup().size() == 0) {
			System.out.println("[INFO] NO filter is specified.");
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("-----filter setting----\n");

		List<Group> fs = f.getGroup();
		List<Rule> ls = null;
		for (Group group : fs) {
			sb.append(" [+] Group ").append(group.getId()).append("\n");
			ls = group.getRule();
			for (Rule rule : ls) {
				sb.append("     [-] ").append(rule.getType()).append(" -> ").append(rule.getValue()).append("\n");
			}
		}

		System.out.println(sb.toString());
	}

	private boolean checkConfigFileChanged() {
		File f = new File(config_file);
		long ts = f.lastModified();
		if (ts > timestamp) {
			return true;
		} else {
			return false;
		}
	}

	private String getRuleDesc(Group grp, Rule rule) {
		return String.format("[Group %s:(%s|%s)]", grp.getId(), rule.getType(), rule.getValue());
	}

	//	private boolean accecptByIncludeFilters(NotifyEvent ne) {
	//		//at least include one keyword
	//		if (incFilters.size() == 0) {
	//			return true;
	//		}
	//
	//		boolean ret = false;
	//		String lineDesc = (String) ne.getEventMessages()[0];
	//
	//		String keyWord = null;
	//		for (String filterStr : incFilters) {
	//			keyWord = filterStr.split(":")[1];
	//			if (lineDesc != null && lineDesc.indexOf(keyWord) > -1) {
	//				ret = true;
	//				break;
	//			}
	//		}
	//
	//		if (!ret && isShowExcFilter) {
	//			System.out.println(ne.getEventMessages()[2] + "[new] Refused By INC -> " + ne.getEventMessages()[0]);
	//		}
	//		return ret;
	//	}
	//
	//	private boolean accecptByExcludeFilters(NotifyEvent ne) {
	//		if (excFilters.size() == 0) {
	//			return true;
	//		}
	//
	//		//if any keyword included,refuse.
	//		boolean ret = true;
	//		String lineDesc = ne.getEventMessages()[0];
	//
	//		String keyWord = null;
	//		for (String filterStr : excFilters) {
	//			keyWord = filterStr.split(":")[1];
	//			if (lineDesc != null && lineDesc.indexOf(keyWord) > -1) {
	//				if (WatchManager.isDebugMode || isShowExcFilter) {
	//					System.out.println(ne.getEventMessages()[2] + "[new] Refused By " + filterStr + " -> "
	//							+ ne.getEventMessages()[0]);
	//				}
	//				ret = false;
	//				break;
	//			}
	//		}
	//		return ret;
	//	}

	/**
	 * @return the isVerbose
	 */
	public boolean isVerbose() {
		return isVerbose;
	}

	/**
	 * @param isVerbose the isVerbose to set
	 */
	public void setVerbose(boolean isVerbose) {
		this.isVerbose = isVerbose;
	}

}
