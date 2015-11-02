package common;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class LeafMap implements Serializable{

	private DefaultMutableTreeNode key;
	private DefaultMutableTreeNode value;
	private String relation;
	public LeafMap() {
		super();
	}
	public LeafMap( DefaultMutableTreeNode key, String realation, DefaultMutableTreeNode value) {
		super();
		this.key = key;
		this.value = value;
		this.relation = realation;
	}
	
	public String toString(){
		
		StringBuffer buf = new StringBuffer();
		Object[] keyInfos = key.getUserObjectPath(); 
		Object[] valueInfos = value.getUserObjectPath(); 
		for( int i = 0; i < keyInfos.length; i++ ){
			if( i > 0 )buf.append('>');
			buf.append(keyInfos[i]);
		}
		buf.append("  ");
		buf.append( this.relation );
		buf.append("  ");
		for( int i = 0; i < valueInfos.length; i++ ){
			if( i > 0 )buf.append('>');
			buf.append(valueInfos[i]);
		}
		
		return buf.toString();
	}
}
