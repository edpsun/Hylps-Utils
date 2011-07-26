/**
 * HttpClient_declaration
 */
package com.hylps.tscan;


/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Feb 3, 2010 10:41:40 AM
 */

public class KunxunUrlHelper {
	//	private static String kxjs = "";
	//	static {
	//		BufferedReader sb = new BufferedReader(
	//				new InputStreamReader(KunxunUrlHelper.class.getResourceAsStream("kx.js")));
	//		try {
	//			String line = null;
	//			StringBuilder sbb = new StringBuilder();
	//			while ((line = sb.readLine()) != null) {
	//				sbb.append(line).append("\n");
	//			}
	//			kxjs = sbb.toString();
	//		} catch (Exception e) {
	//			// TODO: handle exception
	//		} finally {
	//			try {
	//				sb.close();
	//			} catch (IOException e) {
	//			}
	//		}
	//	}
	//
	//	public KunxunUrlHelper(HttpClient c) {
	//		client = c;
	//	}
	//
	//	//	public String getRealUrl(String url) {
	//	//		String cont = getCont(url);
	//	//		
	//	//		String[] s = cont.split("\n");
	//	//		String thisUrl = "";
	//	//		String jsUrlLine = "";
	//	//		for (int i = 0; i < s.length; i++) {
	//	//			if (s[i].indexOf("ThisURL") > -1) {
	//	//				thisUrl = s[i];
	//	//			}
	//	//
	//	//			if (s[i].indexOf("leave.js?") > -1) {
	//	//				jsUrlLine = s[i];
	//	//				int k = jsUrlLine.indexOf("http");
	//	//				int kk = jsUrlLine.indexOf("\">");
	//	//
	//	//				jsUrlLine = jsUrlLine.substring(k, kk);
	//	//			}
	//	//
	//	//		}
	//	//
	//	//		String jsLineBlock = getCont(jsUrlLine);
	//	//		int k = jsLineBlock.indexOf("(-1,-1,");
	//	//		int kk = jsLineBlock.indexOf("F be(");
	//	//		jsLineBlock = "bDC=new Array " + jsLineBlock.substring(k, kk);
	//	//
	//	//		//		System.out.println(thisUrl);
	//	//		//		System.out.println(jsLineBlock);
	//	//
	//	//		return calculateUrl(new String[] { thisUrl, jsLineBlock });
	//	//	}
	//
	//	//
	//	//	private String calculateUrl(String[] ss) {
	//	//		ScriptEngineManager factory = new ScriptEngineManager();
	//	//		ScriptEngine engine = factory.getEngineByName("JavaScript");
	//	//
	//	//		StringBuilder js = new StringBuilder();
	//	//		for (int i = 0; i < ss.length; i++) {
	//	//			js.append(ss[i]).append("\n");
	//	//		}
	//	//		js.append(kxjs);
	//	//
	//	//		try {
	//	//			//System.out.println(js.toString());
	//	//			engine.eval(js.toString());
	//	//		} catch (Exception ex) {
	//	//			ex.printStackTrace();
	//	//		}
	//	//
	//	//		return engine.get("targetu").toString();
	//	//	}
	//	//
	//	//	private String getCont(String url) {
	//	//		url="http://huoche.kuxun.cn/"+url;
	//	//		Map<String, String> map = new HashMap<String, String>();
	//	//		GetMethod get = new GetMethod(url);
	//	//		InputStream input = null;
	//	//		try {
	//	//			try {
	//	//				int status = client.executeMethod(get);
	//	//
	//	//				if (status == 200) {
	//	//					input = get.getResponseBodyAsStream();
	//	//				} else {
	//	//					//retry
	//	//					get.releaseConnection();
	//	//					get = new GetMethod(url);
	//	//					status = client.executeMethod(get);
	//	//					if (status == 200) {
	//	//						input = get.getResponseBodyAsStream();
	//	//					} else {
	//	//						System.out.println("[Error] Failed to get real url");
	//	//					}
	//	//				}
	//	//			} catch (Exception ex) {
	//	//				ex.printStackTrace();
	//	//				throw new RuntimeException(
	//	//						"[Error] Unexpect exception in http access.");
	//	//			}
	//	//
	//	//			try {
	//	//				BufferedReader br = new BufferedReader(new InputStreamReader(
	//	//						input, "UTF-8"));
	//	//				String line = null;
	//	//				boolean hasKeyWord = false;
	//	//				int ppp = 0;
	//	//				StringBuilder sb = new StringBuilder();
	//	//				while ((line = br.readLine()) != null) {
	//	//					line = line.trim();
	//	//					sb.append(line).append("\n");
	//	//				}
	//	//				br.close();
	//	//
	//	//				return sb.toString();
	//	//
	//	//			} catch (Exception e) {
	//	//				e.printStackTrace();
	//	//				throw new RuntimeException(
	//	//						"[Error] Unexpect exception in dealing page content.");
	//	//			}
	//	//		} finally {
	//	//			get.releaseConnection();
	//	//		}
	//	//	}
	//
	//	private String getCont(String url) {
	//		url = "http://huoche.kuxun.cn/" + url;
	//		Map<String, String> map = new HashMap<String, String>();
	//		GetMethod get = new GetMethod(url);
	//		InputStream input = null;
	//		try {
	//			try {
	//				int status = client.executeMethod(get);
	//
	//				if (status == 200) {
	//					input = get.getResponseBodyAsStream();
	//				} else {
	//					//retry
	//					get.releaseConnection();
	//					get = new GetMethod(url);
	//					status = client.executeMethod(get);
	//					if (status == 200) {
	//						input = get.getResponseBodyAsStream();
	//					} else {
	//						System.out.println("[Error] Failed to get real url");
	//					}
	//				}
	//			} catch (Exception ex) {
	//				ex.printStackTrace();
	//				throw new RuntimeException("[Error] Unexpect exception in http access.");
	//			}
	//
	//			try {
	//				BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
	//				String line = null;
	//				boolean hasKeyWord = false;
	//				int ppp = 0;
	//				StringBuilder sb = new StringBuilder();
	//				while ((line = br.readLine()) != null) {
	//					if (line.indexOf("http-equiv=\"refresh\"") > -1) {
	//						line = line.trim();
	//						sb.append(line).append("\n");
	//					}
	//				}
	//				br.close();
	//
	//				return sb.toString();
	//
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//				throw new RuntimeException("[Error] Unexpect exception in dealing page content.");
	//			}
	//		} finally {
	//			get.releaseConnection();
	//		}
	//	}
	//
	//	public String getRealUrl(String url) {
	//		String cont = getCont(url);
	//		String realurl = "error!!";
	//		String captionRex = "http-equiv.*url=(.*?)\"";
	//		Pattern captionPattern = Pattern.compile(captionRex);
	//
	//		Matcher captionMatcher = captionPattern.matcher(cont);
	//		if (captionMatcher.find()) {
	//			realurl = captionMatcher.group(1);
	//		}
	//
	//		return realurl;
	//	}
	//
	//	public static void main(String[] args) {
	//		HttpClient client = new HttpClient();
	//		client.setTimeout(60 * 1000);
	//		boolean isUseProxy = false;
	//		if (isUseProxy) {
	//			client.getHostConfiguration().setProxy("www-proxy.cn.oracle.com", 80);
	//		}
	//
	//		KunxunUrlHelper kh = new KunxunUrlHelper(client);
	//		String ss = kh
	//				.getRealUrl("http://huoche.kuxun.cn/leads.php?action=piao&method=jumpurl&url=GIZ0OV%2F55IoEsfE4dBpYot4AlbI84r5wbskKLA3X0VteKdxrnJIMyFZOaWrIK10QrSjFvweJXrZWGdwZL2zEffF2WTLF55tlqiAMSn8y%2BEMitUWtLRFT3r%2BVcvy3sSfXBRIsbzdhWjqqlAAzSc2upg%3D%3D");
	//		System.out.println(ss);
	//	}
}
