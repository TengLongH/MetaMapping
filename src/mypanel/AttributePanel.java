package mypanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import common.MyTreeNode;

public class AttributePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	public AttributePanel( MyTreeNode treeNode ){
		Vector<String> title = new Vector<String>();
		title.addElement("Attribute");
		title.addElement("Value");
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Node node = (Node) treeNode.getUserObject();
		NamedNodeMap map = node.getAttributes();
		
		String attrs[] = new String[]{"name","row","colum"};
		Vector<String> item = null;
		Node temp = null;
		String attr = null;
		for( int i =0 ; i < attrs.length; i++ ){
			attr = attrs[i];
			temp = map.getNamedItem(attr);
			if( null != temp ){
				item = new Vector<String>(2);
				item.add(attr);
				item.add(temp.getNodeValue());
			}
		}
		
		table = new JTable( data, title );
		
		add( new JScrollPane(table) );
	}
}
