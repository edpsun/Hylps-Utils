/**
 * HttpClient_declaration
 */
package test;

import java.io.FileInputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.hylps.tscan.config.Condition;
import com.hylps.tscan.config.TscanConfig;

/**
 * @author esun
 * @version HttpClient_version
 * Create Date:Jan 27, 2011 5:01:27 PM
 */
public class C {
	public static void main(String[] args) throws Exception {
		String config_file = "/export/work/workbench/apr_workspace/HttpClient/src/xsd/tsacn.xml";
		JAXBContext jc = JAXBContext.newInstance("com.hylps.tscan.config");
		Unmarshaller u = jc.createUnmarshaller();

		//System.out.println(o.getClass().getCanonicalName());

		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		JAXBElement<TscanConfig> jo = (JAXBElement<TscanConfig>) u.unmarshal(new FileInputStream(config_file));

		TscanConfig config = jo.getValue();
		List<Condition> list = config.getCondition();

		for (Condition condition : list) {
			System.out.println("Name :" + condition.getName());
			System.out.println("Type :" + condition.getType());
			System.out.println("Value:" + condition.getValue());
			System.out.println("Desc :" + condition.getDesc());
		}

		if (config.getFilter() == null) {
			System.out.println("[INFO] NO");
		}else{
			System.out.println("OK");
		}

	}
}
