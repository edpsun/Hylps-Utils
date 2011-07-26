/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.awt.Desktop;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Date;
import java.util.Properties;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 *
 * @author  esun
 */
public class NotifyFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	private static final String defaultMessage = "<html><body>Notify Area[通知]<br></body></html>";

	/** Creates new form NotifyPanel */
	public NotifyFrame() {
		initComponents();
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		notifyTextAera = new javax.swing.JEditorPane();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				NotifyFrame.this.notifyTextAera.setText(defaultMessage);
				NotifyFrame.this.setTitle("Notification! [" + new Date().toString() + "]");
				NotifyFrame.this.setVisible(false);
			}
		});

		setLocationByPlatform(true);
		setMinimumSize(new java.awt.Dimension(200, 150));
		setName("Notifiction"); // NOI18N
		NotifyFrame.this.setTitle("Notification! [" + new Date().toString() + "]");

		jPanel1.setName("jPanel1"); // NOI18N
		jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));
		jPanel1.setLayout(new java.awt.BorderLayout());

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		notifyTextAera.setEditable(false);
		//notifyTextAera.setFont(new Font("Microsoft YaHei-Plain-15", 0, 15));;
		notifyTextAera.setToolTipText("NotifyText Aera"); // NOI18N
		notifyTextAera.setName("notifyTextAera"); // NOI18N

		notifyTextAera.setContentType("text/html");
		notifyTextAera.setText(defaultMessage);
		notifyTextAera.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
			public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
				HyperLinkUdateHandler(evt);
			}
		});

		jScrollPane1.setViewportView(notifyTextAera);

		jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

		pack();
	}

	/**
	* @param args the command line arguments
	*/
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new NotifyFrame().setVisible(true);
			}
		});
	}

	public void setNotificationMessage(String s) {
		notifyTextAera.setText(s);
		//		notifyTextAera.setContentType("text/html");
		//		notifyTextAera.repaint();
	}

	public String getNotificationMessage() {
		return notifyTextAera.getText();
	}

	// Variables declaration - do not modify
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JEditorPane notifyTextAera;

	// End of variables declaration

	private void HyperLinkUdateHandler(javax.swing.event.HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			JEditorPane pane = (JEditorPane) evt.getSource();
			if (evt instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent e = (HTMLFrameHyperlinkEvent) evt;
				HTMLDocument doc = (HTMLDocument) pane.getDocument();
				doc.processHTMLFrameHyperlinkEvent(e);
			} else {
				try {
					//pane.setPage(evt.getURL());

					//					Properties properties = System.getProperties();
					//					String osName = properties.getProperty("os.name");
					//					if (osName.indexOf("Linux") != -1) {
					//						Runtime.getRuntime().exec(
					//								"firefox -new-tab www.google.com");
					//					} else if (osName.indexOf("Windwos") != -1) {
					//						Runtime.getRuntime().exec("iexplorer.exe");
					//					} else {
					//						throw new Exception("Unknown OS.");
					//					}

					Desktop desktop = Desktop.getDesktop();
					URI uri;
					try {
						uri = evt.getURL().toURI();
						desktop.browse(uri);
					} catch (Exception e) {

						System.out.println(evt.getURL().toExternalForm());

						Properties properties = System.getProperties();
						String osName = properties.getProperty("os.name");
						if (osName.indexOf("Linux") != -1) {
							Runtime.getRuntime().exec("firefox -new-tab " + evt.getURL().toExternalForm());
						} else if (osName.indexOf("XP") != -1) {
							Runtime.getRuntime().exec("explorer.exe " + evt.getURL().toExternalForm());
						} else {
							System.out.println(osName);
							throw new Exception("Unknown OS.");
						}

					}

				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
}
