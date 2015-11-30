package common;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
public class MyTreeCellRender  extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private ImageIcon bookIcon;
	private ImageIcon sheetIcon;
	private ImageIcon mergeIcon;
	private ImageIcon fieldIcon;
	
	public MyTreeCellRender(){
		bookIcon = createImageIcon( "/common/icon/book.png" );
		sheetIcon = createImageIcon("/common/icon/sheet.png");
		mergeIcon = createImageIcon("/common/icon/merg.png");
		fieldIcon = createImageIcon("/common/icon/field.png");
	}
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		TreePath path = tree.getPathForRow(row);
		if( null == path )return this;
		MyTreeNode node = (MyTreeNode) path.getLastPathComponent();
		Node info = (Node) node.getUserObject();
	
		switch( info.getNodeName() ){
		case "book":
			setIcon( bookIcon );
			break;
		case "sheet":
			setIcon(sheetIcon);
			break;
		case "merg":
			setIcon( mergeIcon );
			break;
		case "field":
			setIcon( fieldIcon );
			Element e = (Element) info;
			if( !e.getAttribute("mapping").trim().equals("") ){
				setForeground(Color.BLUE);
			}
			if( node.isMappedNode() ){
				setForeground(Color.BLUE );
			}
			if( node.isMatched() ){
				setForeground( new Color( 205,85,85));
			}
			break;
			default:
		}
		return this;
	}
	
	ImageIcon createImageIcon( String path ){
		java.net.URL imageURL = this.getClass().getResource(path);
		try {
			Image img = ImageIO.read( imageURL );
			ImageIcon icon = new ImageIcon( img );
			return icon;
		} catch (IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
}
