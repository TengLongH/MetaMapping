package utils;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.MyTreeNode;

public class Utils {

	public static void saveXML(Document doc ) {
		File file = null;
		int value = 0;
		boolean cancel = false;
		String name = null;
		StringBuffer fileName = new StringBuffer();
		String message = "please input the file name";
		while( !cancel ){
			fileName.delete(0, fileName.length());
			name = JOptionPane.showInputDialog( message );
			if( null == name ){
				cancel = true;
				break;
			}
			if( name.startsWith("#") ){
				message = name + " is unvalidate please input again";
				continue;
			}
			fileName.append("tree");
			fileName.append( File.separator );
			fileName.append( name.trim() );
			if( !fileName.toString().endsWith(".xml") ){
				fileName.append(".xml");
			}
			file = new File( fileName.toString() );
			if( file.exists() ){
				value = JOptionPane.showConfirmDialog(null, 
					name + " has exists do you want to replace it?",
					"Confirm", JOptionPane.YES_NO_OPTION);
				if( JOptionPane.YES_OPTION == value )break;
			}else{
				try {
					file.createNewFile();
					break;
				} catch (IOException e) {
					message = name + " is unvalidate please input again";
					continue;
				}
			}
		}
		if( !cancel ){
			saveXML(doc, file );
		}
	}
	public static void saveXML(Document doc, File file ) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch ( TransformerException e) {
			JOptionPane.showMessageDialog(null, "save xml file failed, the file format is error");
			e.printStackTrace();
		}
		
	}
	public static void printXML(Document doc ) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch ( TransformerException e) {
			JOptionPane.showMessageDialog(null, "save xml file failed, the file format is error");
			e.printStackTrace();
		}
		
	}

	public static MyTreeNode createTree(String path) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		File source = new File(path);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(source);
		Element book = doc.getDocumentElement();
		MyTreeNode root = new MyTreeNode(book);
		iteratorCreateNode(root, book);
		return root;
	}

	private static void iteratorCreateNode(MyTreeNode root, Node info) {
		MyTreeNode treeNode = null;
		NodeList subInfos = info.getChildNodes();
		for (int i = 0; i < subInfos.getLength(); i++) {
			Node node = subInfos.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				treeNode = createTreeNode(root, node);
				iteratorCreateNode(treeNode, node);
			}
		}
	}

	private static MyTreeNode createTreeNode(MyTreeNode parent, Object info) {
		MyTreeNode child = new MyTreeNode(info);
		parent.add(child);
		return child;
	}
}
