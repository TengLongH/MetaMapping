package common;

import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MyTreeNode extends DefaultMutableTreeNode{

	private static final long serialVersionUID = 1L;

	private boolean map;
	private boolean synonyms;
	
	
	public MyTreeNode(Object userObject) {
        super(userObject, true);
    }
	
	@Override
	public String toString(){
		if( !(userObject instanceof Node) )return super.toString();
		
		Element node = (Element) userObject;
		StringBuffer buf = new StringBuffer();
		String name = node.getAttribute("name");
		//buf.append( Lang.get(name, name) );
		buf.append( name );
		if( !node.getAttribute("colum").trim().equals("") ){
			buf.append(",");
			buf.append(" col:");
			buf.append(node.getAttribute("colum"));
		}
		return buf.toString();
	}

	public boolean isMap() {
		return map;
	}

	public void setMap(boolean mappedNode) {
		this.map = mappedNode;
	}

	public boolean isSynonyms() {
		return synonyms;
	}

	public void setSynonyms(boolean synonyms) {
		this.synonyms = synonyms;
	}
	
}
