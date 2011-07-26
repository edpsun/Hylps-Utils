//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.01.27 at 06:30:04 PM CST 
//

package com.hylps.tscan.config;

import java.io.FileInputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.hylps.tscan.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _Tscan_QNAME = new QName("http://www.hylps.com/tscan/filter", "tscan");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.hylps.tscan.config
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Condition }
	 * 
	 */
	public Condition createCondition() {
		return new Condition();
	}

	/**
	 * Create an instance of {@link Rule }
	 * 
	 */
	public Rule createRule() {
		return new Rule();
	}

	/**
	 * Create an instance of {@link Filter }
	 * 
	 */
	public Filter createFilter() {
		return new Filter();
	}

	/**
	 * Create an instance of {@link Group }
	 * 
	 */
	public Group createGroup() {
		return new Group();
	}

	/**
	 * Create an instance of {@link TscanConfig }
	 * 
	 */
	public TscanConfig createTscanConfig() {
		return new TscanConfig();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link TscanConfig }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.hylps.com/tscan/filter", name = "tscan")
	public JAXBElement<TscanConfig> createTscan(TscanConfig value) {
		return new JAXBElement<TscanConfig>(_Tscan_QNAME, TscanConfig.class, null, value);
	}

	public static TscanConfig unMarshall(String config_file) {
		try {
			JAXBContext jc = JAXBContext.newInstance("com.hylps.tscan.config");

			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<TscanConfig> jo = (JAXBElement<TscanConfig>) u.unmarshal(new FileInputStream(config_file));

			TscanConfig config = jo.getValue();
			List<Condition> list = config.getCondition();
			return config;
		} catch (Exception e) {
			throw new RuntimeException("[Error] read config file failure.", e);
		}
	}

}