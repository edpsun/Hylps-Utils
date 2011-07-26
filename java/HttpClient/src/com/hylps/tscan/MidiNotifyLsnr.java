/**
 * HttpClient_declaration
 */
package com.hylps.tscan;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import com.hylps.tscan.config.Condition;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 13, 2009 11:12:57 PM
 */
public class MidiNotifyLsnr extends Thread implements NotifyListener {

	ArrayList<Condition> urlNotifyList = new ArrayList<Condition>();
	NotifyEventFilter filter = null;

	public void onNotification(NotifyEvent ne) {

		String line = ne.getEventMessages()[0];

		if (filter != null) {
			if (!filter.accept(ne)) {
				if (WatchManager.isDebugMode) {
					System.out.println("[DEBUG] Filtered -> Line content:" + line);
				}
				return;
			}
		}

		synchronized (urlNotifyList) {
			if (!urlNotifyList.contains(ne.getCondition())) {
				urlNotifyList.add(ne.getCondition());
				urlNotifyList.notifyAll();
			}
		}
	}

	public void activate(Properties context) {
		this.start();
	}

	public void run() {

		while (true) {
			synchronized (urlNotifyList) {
				if (urlNotifyList.size() == 0) {
					try {
						urlNotifyList.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				urlNotifyList.remove(0);
			}

			playMid();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void playMid() {
		if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
			String alertmid = "stuff" + File.separator + "a.mp3";
			Runtime run = Runtime.getRuntime();
			try {
				String cmd = "mpg321 " + alertmid;
				Process p = run.exec(cmd);

				if (p.waitFor() != 0) {
					System.err.println("[mpg321] failed to play mp3");
				}

			} catch (Exception e) {
				System.out.println("play mid failed using wildmidi:" + e.getMessage());
			}
		} else {

			String alertmid = "stuff" + File.separator + "a.mid";

			try {
				Sequencer sqcer = MidiSystem.getSequencer();
				sqcer.open();
				Sequence midi = MidiSystem.getSequence(new File(alertmid));
				sqcer.setSequence(midi);

				sqcer.start();

				while (sqcer.isRunning()) {
					sleep(1000);
				}
				sqcer.stop();
				sqcer.close();

			} catch (Exception e) {
				System.out.println("play mid failed:" + e.getMessage());
			}
		}
	}

	public void setFilter(NotifyEventFilter _filter) {
		this.filter = _filter;
	}

}
