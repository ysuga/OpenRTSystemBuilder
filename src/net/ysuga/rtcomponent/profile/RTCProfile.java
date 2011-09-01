/**
 * RTCProfile.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/08/30
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtcomponent.profile;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * <div lang="ja">
 *
 * </div>
 * <div lang="en">
 *
 * </div>
 * @author ysuga
 *
 */
public class RTCProfile {

	private String fileName;
	
	public RTCProfile() {
		
	}
	
	
	public void save(File file) {
		
	}
	
	/**
	 * 
	 * @param file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void load(File file) throws ParserConfigurationException,
			SAXException, IOException {
		fileName = file.getName();
		Element rootElement;
		Document xmlDocument;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		db = dbf.newDocumentBuilder();
		xmlDocument = db.parse(file);
		rootElement = xmlDocument.getDocumentElement();

		load(rootElement);
		
	}
	
	
	public void load(Element element) {
		
	}
	
}
