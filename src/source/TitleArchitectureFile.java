package source;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.poi.ss.util.CellReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.XMLList;
import common.MyDefaultTableModel;
import common.MyTreeCellRender;
import common.MyTreeNode;

public class TitleArchitectureFile extends JFrame {

	private static final long serialVersionUID = 1L;

	private boolean ignorTableValueChange = true;
	private File sourceFile;

	private JTree tree;
	private JTable editTable;
	private JButton delButton;
	private JButton addButton;

	private JButton sheetButton;
	private JButton mergButton;
	private JButton fieldButton;

	private ButtonsListener buttonListener = new ButtonsListener();
	// data
	private TreePath curPath;

	private JButton saveButton;

	private JFrame frame ;
	public TitleArchitectureFile(XMLList fileList) {
		this("tree/sys/blankSourceTree.xml", fileList );
	}

	public TitleArchitectureFile(String sourceName, XMLList fileList ) {
		super("Analysis Title");
		this.frame = this;
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		//setLayout(new GridBagLayout());
		setLayout( new GridLayout( 1, 2 ) );
		this.sourceFile = new File(sourceName);
		System.out.println(sourceFile.exists());
		
		try {
			tree = new JTree(utils.Utils.createTree(sourceFile.getAbsolutePath()));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Create source tree failed, file unfind or the format is unilegal");
			e.printStackTrace();
			return;
		}

		MyTreeCellRender render = new MyTreeCellRender();
		tree.setCellRenderer(render);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(new JTreeListener());
		
		add( new JScrollPane(tree) );
		//add(new JScrollPane(tree), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
		//		GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		delButton = new JButton("Remove");
		delButton.addActionListener(buttonListener);
		leftPanel.add(delButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		addButton = new JButton("Add");
		leftPanel.add(addButton, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));

		JPanel addPanel = new JPanel();
		addPanel.setLayout(new GridLayout(1, 3));
		sheetButton = new JButton("Sheet");
		sheetButton.addActionListener(buttonListener);
		mergButton = new JButton("Columns");
		mergButton.addActionListener(buttonListener);
		fieldButton = new JButton("Column");
		fieldButton.addActionListener(buttonListener);
		addPanel.add(sheetButton);
		addPanel.add(mergButton);
		addPanel.add(fieldButton);

		leftPanel.add(addPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
		JButton editButton = new JButton("Edit");
		leftPanel.add(editButton, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		Vector<String> columNames = new Vector<String>(2);
		columNames.add("Attribute");
		columNames.add("Value");
		Vector<Vector<String>> data = new Vector<Vector<String>>(5);
		for (int i = 0; i < 5; i++) {
			Vector<String> item = new Vector<String>(2);
			item.add("");
			item.add("");
			data.add(item);
		}
		editTable = new JTable(new MyDefaultTableModel(data, columNames));
		editTable.getModel().addTableModelListener(new TableListener());
		leftPanel.add(new JScrollPane(editTable), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		saveButton = new JButton("Save");
		saveButton.addActionListener(buttonListener);
		leftPanel.add(saveButton, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		add( leftPanel );
		//add(leftPanel, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
		//		new Insets(0, 0, 0, 0), 0, 0));

		setSize(new Dimension(650, 700));
		setVisible(true);
		setLocationRelativeTo(null);
		
		setCurrentPath(null);
	}

	private void addElement(String tagName) {
		if (null == curPath)
			return;
		MyTreeNode leaf = null;
		Node docNode = null;
		Element docChild = null;
		leaf = (MyTreeNode) curPath.getLastPathComponent();
		docNode = (Node) leaf.getUserObject();
		docChild = docNode.getOwnerDocument().createElement(tagName);
		docChild.setAttribute("name", tagName + "Name");
		if (tagName.equals("sheet")) {
			docChild.setAttribute("row", "0");
		} else if (tagName.equals("field")) {
			docChild.setAttribute("colum", "0");
		}
		docNode.appendChild(docChild);
		leaf.add(new MyTreeNode(docChild));
		ArrayList<Object> o = new ArrayList<Object>();
		o.addAll( Arrays.asList(curPath.getPath()));
		o.add(leaf);
		tree.makeVisible( new TreePath( o.toArray() ) );
		tree.updateUI();
	}

	private boolean format(Document doc) {
		Element root = doc.getDocumentElement();
		removeEmptyElement(root);
		return true;
	}

	
	private boolean removeEmptyElement(Element parent) {
		int countElement = 0;
		NodeList list = parent.getChildNodes();
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				countElement++;
				if( removeEmptyElement((Element) node) ){
					parent.removeChild(node);
					countElement--;
					index--;
				}
			}
		}
		String nodeName = parent.getNodeName();
		return (countElement == 0 && (nodeName.equals("sheet") || nodeName.equals("merg"))) ;
	}

	public void setCurrentPath( TreePath path ){
		curPath = path ;
		cleanTable();
		if( null == curPath ){
			delButton.setEnabled(false);
			sheetButton.setEnabled(false);
			mergButton.setEnabled(false);
			fieldButton.setEnabled(false);
			return ;
		}
		//if not null the get the new current path leaf node
		MyTreeNode leaf = (MyTreeNode) curPath.getLastPathComponent();
		Node node = (Node) leaf.getUserObject();
		//Change add buttons enable stage
		leaf.getParent();
		delButton.setEnabled(leaf.getParent()!=null);
		switch (node.getNodeName()) {
		case "book":
			sheetButton.setEnabled(true);
			mergButton.setEnabled(false);
			fieldButton.setEnabled(false);
			break;
		case "sheet":
			sheetButton.setEnabled(false);
			mergButton.setEnabled(true);
			fieldButton.setEnabled(true);
			break;
		case "merg":
			sheetButton.setEnabled(false);
			mergButton.setEnabled(true);
			fieldButton.setEnabled(true);
			break;
		case "field":
			sheetButton.setEnabled(false);
			mergButton.setEnabled(false);
			fieldButton.setEnabled(false);
			break;
		default:
			JOptionPane.showMessageDialog(null, "unkown node type");
			return;
		}

		// JTable value changed
		setTableValue(node);
	}
	private void cleanTable(){
		ignorTableValueChange = true;
		TableModel model = editTable.getModel();
		for (int i = 0; i < 5; i++) {
			model.setValueAt("", i, 0);
			model.setValueAt("", i, 1);
		}
		ignorTableValueChange = false;
	}
	private void setTableValue( Node node ){
		Node temp = null;
		int count = 0;
		String attrs[] = new String[] { "name", "row", "colum" };
		TableModel model = editTable.getModel();
		ignorTableValueChange = true;
		for (int i = 0; i < attrs.length; i++) {
			temp = node.getAttributes().getNamedItem(attrs[i]);
			if (null != temp) {
				model.setValueAt(attrs[i], count, 0);
				model.setValueAt(temp.getTextContent(), count, 1);
				count++;
			}
		}
		ignorTableValueChange = false;
	}
	
	public static void main(String[] args) {
		new TitleArchitectureFile(null);
	}

	class JTreeListener implements MouseListener {
		@Override
		public void mousePressed(MouseEvent e) {
			JTree source = (JTree) e.getSource();
			TreePath path = source.getPathForLocation(e.getX(), e.getY());
			if( null != path ){
				setCurrentPath(path);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}

	}

	class TableListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			if (ignorTableValueChange)
				return;
			if (!editTable.isCellEditable(e.getFirstRow(), e.getColumn()))
				return;
			if (null == curPath)
				return;
			TableModel model = editTable.getModel();
			String attr = model.getValueAt(e.getFirstRow(), e.getColumn() - 1).toString();
			System.out.println(attr);
			String changedValue = model.getValueAt(e.getFirstRow(), e.getColumn()).toString();
			changedValue = changedValue.trim();
			if (attr.equals("row") || attr.equals("colum")) {
			
				try{
					Integer.parseInt(changedValue);
				}catch(Exception ex ){
					JOptionPane.showMessageDialog(null, "Integer value");
					model.setValueAt("0", e.getFirstRow(), e.getColumn());
				}
			}
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) curPath.getLastPathComponent();
			Element docNode = (Element) treeNode.getUserObject();
			docNode.setAttribute(attr, changedValue);
			tree.updateUI();
		}

	}

	class ButtonsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractButton button = (AbstractButton) e.getSource();
			System.out.println("click button" + button.getText());

			switch (button.getText().trim()) {
			case "Remove":
				if (null == curPath)return;
				MyTreeNode leaf = (MyTreeNode) curPath.getLastPathComponent();
				if( leaf.getParent() == null )return ;
				MyTreeNode parent = (MyTreeNode) leaf.getParent();
				Node docChild = (Node) leaf.getUserObject();
				Node docParent = (Node) parent.getUserObject();
				if (parent != null) {
					parent.remove(leaf);
					docParent.removeChild(docChild);
					setCurrentPath(null);
					tree.updateUI();
				}
				break;
			case "Sheet":
				addElement("sheet");
				break;
			case "Columns":
				addElement("merg");
				break;
			case "Column":
				addElement("field");
				break;
			case "Save":
				DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) tree.getModel().getRoot();
				if (null == treeRoot)
					break;
				Node docNode = (Node) treeRoot.getUserObject();
				if (!format(docNode.getOwnerDocument()))
					break;
				File file = utils.Utils.saveXML(docNode.getOwnerDocument());
				new AttributeMappingFile(file);
				frame.dispose();
				break;
			default:
				return;
			}
		}

	}
}
