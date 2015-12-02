package common;

import javax.swing.JButton;

import utils.Lang;

public class MyButton extends JButton {

	private static final long serialVersionUID = 1L;
	private String id;
	public MyButton( String id ){
		super( Lang.get(id) );
		this.id = id ;
	}
	
	public String getId(){
		return id;
	}
}
