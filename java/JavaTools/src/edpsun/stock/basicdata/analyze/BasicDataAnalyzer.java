/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 16, 2010 9:40:34 PM
 */
public class BasicDataAnalyzer {
	BasicDataExtracter basicDataExtracter = new BasicDataExtracter();

	static List<String> headerList = new ArrayList<String>();
	static {
		headerList.add(Instants.DATA_SECTION_HEADER_LATEST);
		headerList.add(Instants.DATA_SECTION_HEADER_HOLDER);
		headerList.add(Instants.DATA_SECTION_HEADER_SUMMARY);
	}

	public BasicDataAnalyzer() {
	}

	public void doWork(String path) {
		File f = new File(path);
		List<File> list = getFileList(f);

		System.out.println("[+] Working in dir : " + path);
		System.out.println("[+]    File number : " + list.size());
		System.out.println("==========================================");

		File buildout = null;
		File chosen = null;
		if (list.size() == 0) {
			return;
		} else {
			if (f.isDirectory()) {
				buildout = new File(f, "buildout");
			}
			if (f.isFile()) {
				buildout = new File(f.getParentFile(), "buildout");
			}

			if (!buildout.exists()) {
				buildout.mkdirs();
			}

			chosen = new File(buildout.getParentFile(), "choose");
			if (!chosen.exists()) {
				chosen.mkdirs();
			}
		}

		int i = 0;
		HashMap<String, Object> dataMap = null;
		HolderAnalyzer ha = new HolderAnalyzer();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			try {
				Thread.sleep(100);
				File file = (File) iterator.next();
				System.out.println("[" + (++i) + "] Analyzing: " + file.getAbsolutePath());
				System.out.println("==============================================================");

				//extract
				try {
					dataMap = basicDataExtracter.extractBasicData(file, headerList);
				} catch (AnalyzeException e) {
					System.out.println("        [Error] skip current file. Msg:" + e.getMessage());
					continue;
				}

				String code = (String) dataMap.get(Instants.DATA_MAP_ENTRY_CODE);
				String name = (String) dataMap.get(Instants.DATA_MAP_ENTRY_NAME);

				System.out.println("    [-] Name : " + name);
				System.out.println("    [-] Code : " + code);
				if (name.indexOf("ST") > -1) {
					System.out.println("    [-] Skip ST from report.");
					continue;
				}

				//get wanted part
				DataSectionHandler sectionHander = null;
				for (Iterator<String> iterator2 = headerList.iterator(); iterator2.hasNext();) {
					String header = iterator2.next();
					//					System.out.println("#################################################################");
					//					System.out.println("  [-] Header: " + header);
					//					System.out.println("  [-] " + dataMap.get(header).toString().substring(0,100));
					//					System.out.println("#################################################################");
					try {
						sectionHander = DataSectionHandler.getSectionHandler(header);
					} catch (Exception e) {
						System.out.println("[Error] handling" + header + ". Msg:" + e.getMessage());
						continue;
					}
					//System.out.println(header+":"+ sectionHander.getClass().getCanonicalName());
					String ret = sectionHander.dealSection(dataMap.get(header).toString());
					dataMap.put(header, ret);
				}

				//get main business
				String lastest = (String) dataMap.get(Instants.DATA_SECTION_HEADER_LATEST);
				String keyword0 = "│行业";
				String keyword1 = "│主营";
				String keyword2 = "───";
				int p0 = lastest.indexOf(keyword0);
				int p1 = lastest.indexOf(keyword1);

				if (p0 < p1 && p0 > -1) {
					p1 = p0;
				}

				int p2 = lastest.indexOf(keyword2, p1 + 1);

				String main_busi = "Unknown";
				if (p2 > p1 && p1 > -1) {
					main_busi = lastest.substring(p1, p2);
					main_busi = main_busi.replaceAll("\n.*│", " ");

					main_busi = main_busi.replaceAll("│", " ");
					while (main_busi.indexOf("  ") > -1) {
						main_busi = main_busi.replaceAll("  ", " ");
					}

				} else {
					main_busi = main_busi + " p1->" + p1 + " p2->" + p2;
				}

				String fileName = code + "_" + name + ".html";

				//cal
				boolean isChosen = false;
				try {
					isChosen = ha.analyze( dataMap, name, code,
							main_busi);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				//export to file.
				try {
					System.out.println("    [-] Export: " + fileName);
					if (isChosen) {
						exportBasicData(chosen, fileName, dataMap);
					} else {
						exportBasicData(buildout, fileName, dataMap);
					}
				} catch (Exception e) {
					System.out.println("[Error] can not write basic data file. Msg:" + e.getMessage());
					continue;
				}

			} catch (Exception e) {
				System.out.println("Skip current!!! Msg:" + e.getMessage());
				e.printStackTrace();
			}
		}

		System.out.println("============================================================");
		System.out.println("Export report file!");
		File reportFile = new File(buildout.getParent(), "report.html");
		ha.exportReport(reportFile);
		System.out.println("Export report file. Done.");
	}

	private void exportBasicData(File buildout, String filename, HashMap<String, Object> map) throws IOException {

		File f = new File(buildout, filename);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));

			bw.write(HtmlUtils.getHeader(filename));
			bw.write("\n\n");

			bw.write("<pre>");
			for (String header : headerList) {
				String s = (String) map.get(header);
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

	private List<File> getFileList(File p) {
		List<File> list = new ArrayList<File>();

		if (!p.exists()) {
			throw new RuntimeException("Path not exist! " + p.getAbsolutePath());
		}

		if (p.isDirectory()) {
			File[] fs = p.listFiles();
			for (int i = 0; i < fs.length; i++) {
				list.addAll(getFileList(fs[i]));
			}
		} else if (p.isFile() && p.getAbsolutePath().toLowerCase().endsWith(".txt")) {
			list.add(p);
		}

		return list;
	}

	public static void main(String[] args) {
		//String path = "/data/depot/share/vs/data";
		String path = "/home/esun/Desktop/tt";
		if (args.length > 0) {
			path = args[0];
		}

		BasicDataAnalyzer basicDataExtracter = new BasicDataAnalyzer();
		basicDataExtracter.doWork(path);
	}

}
