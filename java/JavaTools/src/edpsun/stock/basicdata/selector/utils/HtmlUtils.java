/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.utils;

import java.io.InputStream;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 24, 2010 4:45:46 PM
 */
public class HtmlUtils {
	public static String jqery = "jquery.min.js";
	public static String bda = "bda.js";
	public static final String footer = "</body></html>";
	public static final String header = "<html>\n<head>\n<title>%s</title>"
			+ "\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=%s\">" + "\n<script src=\"res/" + jqery
			+ "\" language=\"javascript\" type=\"text/javascript\"></script> " + "\n<script src=\"res/" + bda
			+ "\"        language=\"javascript\" type=\"text/javascript\"></script> " + "\n</head>\n<body>";

	public static String getFooter() {
		return footer;
	}

	public static String getHeader(String title, String encoding) {
		if (title == null) {
			title = "Unspecified";
		}

		String h = String.format(header, title, encoding);
		return h;
	}

	public static String getHeader(String title) {
		return getHeader(title, "UTF-8");
	}

}
