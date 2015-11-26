package common;

import org.w3c.dom.Element;

import utils.Utils;

public class FieldMapping{

	private MyTreeNode key;
	private MyTreeNode value;
	private String relation;

	public FieldMapping( MyTreeNode key, String relation, MyTreeNode value) {
		this.key = key;
		this.value = value;
		this.relation = relation;
	}
	
	public String getRelation(){
		return this.relation;
	}
	public String keyString(){
		Element element = (Element) key.getUserObject();
		return Utils.elementPathToString(element);
	}
	public String valueString(){
		Element element = (Element) value.getUserObject();
		return Utils.elementPathToString(element);
	}
	
	public String mapString(){
		StringBuffer buf = new StringBuffer();
		buf.append( this.relation );
		buf.append("  ");
		buf.append(valueString());
		return buf.toString();
	}
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(keyString());
		buf.append("  ");
		buf.append( mapString() );
		return buf.toString();
	}

	public MyTreeNode getKey() {
		return key;
	}

	public MyTreeNode getValue() {
		return value;
	}
	
	
}
