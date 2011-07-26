/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.analyze;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:Oct 24, 2010 4:45:46 PM
 */
public class HtmlUtils {
	public static final String footer = "</body></html>";
	public static final String header = "<html>\n<head>\n<title>%s</title>" +
			"\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=%s\">" +
			"\n<script language=\"javascript\"> \nfunction toggleDisplay(divName) " +
			"{ var divD = document.getElementById(divName); " +
			"if(divD.style.display==\"none\") { divD.style.display = \"block\"; } " +
			"else { divD.style.display = \"none\"; } } \n</script>" +
			"\n</head>\n<body>";

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
