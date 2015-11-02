package common;

import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class MyTreeNode extends DefaultMutableTreeNode{

	private static final long serialVersionUID = 1L;

	public MyTreeNode(Object userObject) {
        super(userObject, true);
    }
	
	@Override
	public String toString(){
		if( !(userObject instanceof Node) )return super.toString();
		
		Node node = (Node) userObject;
		StringBuffer buf = new StringBuffer();
		NamedNodeMap map = node.getAttributes();
		Node temp = map.getNamedItem("name");
		buf.append(temp.getNodeValue() );
		temp = map.getNamedItem("row");
		if( null != temp ){
			buf.append(" row:");
			buf.append(temp.getNodeValue());
		}
		temp = map.getNamedItem("colum");
		if( null != temp ){
			buf.append(" column:");
			buf.append(temp.getNodeValue());
		}
		return buf.toString();
	}
}
