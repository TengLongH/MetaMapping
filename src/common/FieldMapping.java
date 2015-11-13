package common;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Element;

public class FieldMapping implements Serializable{

	private DefaultMutableTreeNode key;
	private DefaultMutableTreeNode value;
	private String relation;
	public FieldMapping() {
		super();
	}
	public FieldMapping( DefaultMutableTreeNode key, String realation, DefaultMutableTreeNode value) {
		super();
		this.key = key;
		this.value = value;
		this.relation = realation;
	}
	
	public String userObjectToString( Element e ){
		String msg = e.getAttribute("name");
		/*
		if( e.getNodeName().trim().equals("field")){
			msg += String.format("(column:%s)", e.getAttribute("colum") );
		}
		*/
		return msg ;
	}
	public String toString(){
		
		StringBuffer buf = new StringBuffer();
		Object[] keyInfos = key.getUserObjectPath(); 
		Object[] valueInfos = value.getUserObjectPath(); 
		for( int i = 0; i < keyInfos.length; i++ ){
			if( i > 0 )buf.append('>');
			buf.append( userObjectToString( (Element) keyInfos[i] ));
		}
		buf.append("  ");
		buf.append( this.relation );
		buf.append("  ");
		for( int i = 0; i < valueInfos.length; i++ ){
			if( i > 0 )buf.append('>');
			buf.append( userObjectToString( (Element) valueInfos[i] ));
		}
		return buf.toString();
	}
}
