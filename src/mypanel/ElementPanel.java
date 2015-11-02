package mypanel;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Node;

import common.MyTreeNode;
public class ElementPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel sheet =new JLabel("sheet");
	private JLabel merg =new JLabel("merg");
	private JLabel field =new JLabel("field");
	
	public ElementPanel( MyTreeNode treeNode ){
		Node node = (Node) treeNode.getUserObject();
		switch( node.getNodeName() ){
		case "book":
			add( sheet );
			break;
		case "sheet":
		case "merg":
			add( merg );
			add( field );
			break;
		case "field":
			break;
		default:
			JOptionPane.showMessageDialog(null, "Can't find node name");
			return ;
		}	
	}
}
