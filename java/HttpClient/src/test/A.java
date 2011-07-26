/**
 * HttpClient_declaration
 */
package test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Feb 2, 2010 5:04:06 PM
 */
public class A {
	public static void main1(String[] args) {
		//		String s = "[北京-宿州:K]=http://piao.kuxun.cn/search.php?T=Ticket&Cat=sale&From=%E5%8C%97%E4%BA%AC&q=%E5%AE%BF%E5%B7%9E&Cat2=%E5%8D%A7%E9%93%BA&start=0";
		//		String re = "\\[(.*):(.*)\\]=(.*)";
		//		Pattern p = Pattern.compile(re);
		//		Matcher m = p.matcher(s);
		//		if (m.matches()) {
		//
		//			int pp = m.groupCount();
		//			for (int i = 0; i < pp+1; i++) {
		//				System.out.println(m.group(i));
		//			}
		//		} else {
		//			System.out.println("=");
		//		}
		int p = run();
		System.out.println(p);

	}

	public static int run() {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		//		ScriptEngineManager m = new ScriptEngineManager();
		//		ScriptEngine engine = m.getEngineByExtension("js");
		BufferedReader reader = null;
		try {
			//engine.eval("var t = 'hi'");
			reader = new BufferedReader(new FileReader(new File("a.js")));
			engine.eval(reader);
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {

			}
		}
		String ss = engine.get("targetu").toString();
		System.out.println(ss);
		return 1;
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
					Thread.sleep(1000);
				}
				sqcer.stop();
				sqcer.close();

			} catch (Exception e) {
				System.out.println("play mid failed:" + e.getMessage());

			}
		}

	}

	public static void main(String[] args) {
		A a = new A();
		a.playMid();
	}
}
