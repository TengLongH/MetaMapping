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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;

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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.Attr;
import common.MyButton;
import common.MyDefaultTableModel;
import common.MyTreeCellRender;
import common.MyTreeNode;
import utils.Lang;

public class TitleArchitectureFile extends JFrame
implements ActionListener, TableModelListener, MouseListener, WindowListener{

	private static final long serialVersionUID = 1L;

	private boolean ignorTableValueChange = true;
	private File sourceFile;

	private JTree tree;
	private JTable editTable;
	private MyButton delButton;
	private MyButton addButton;

	private MyButton sheetButton;
	private MyButton mergButton;
	private MyButton fieldButton;

	private MyButton saveButton;
	// data
	private TreePath curPath;

	private Workbook book;
	private File excel;

	private String defName="";
	
	private int tableRow = 0;
	
	public TitleArchitectureFile(File excel) {
		this("tree/sys/blankSourceTree.xml", excel);
	}

	public TitleArchitectureFile(String xml, File excel) {
		super( Lang.get("metadata") );
		
		this.addWindowListener(this);
		setLayout(new GridLayout(1, 2));
		this.sourceFile = new File(xml);
		setExcel(excel);
		try {
			tree = new JTree(utils.Utils.createTree(sourceFile.getAbsolutePath()));
			tree.setLargeModel(true);
			tree.setRowHeight(18);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Create source tree failed, file unfind or the format is unilegal");
			e.printStackTrace();
			return;
		}

		MyTreeCellRender render = new MyTreeCellRender();
		tree.setCellRenderer(render);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener( this );

		add(new JScrollPane(tree));
	
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		delButton = new MyButton("button-remove");
		delButton.addActionListener(this);
		leftPanel.add(delButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		addButton = new MyButton("button-add");
		leftPanel.add(addButton, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));

		JPanel addPanel = new JPanel();
		addPanel.setLayout(new GridLayout(1, 3));
		sheetButton = new MyButton("button-sheet");
		sheetButton.addActionListener(this);
		mergButton = new MyButton("button-columns");
		mergButton.addActionListener(this);
		fieldButton = new MyButton("button-column");
		fieldButton.addActionListener(this);
		addPanel.add(sheetButton);
		addPanel.add(mergButton);
		addPanel.add(fieldButton);

		leftPanel.add(addPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
		MyButton editButton = new MyButton("button-edit");
		leftPanel.add(editButton, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		createTable();
		
		leftPanel.add(new JScrollPane(editTable), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		saveButton = new MyButton("button-save");
		saveButton.addActionListener(this);
		leftPanel.add(saveButton, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		add(leftPanel);
		
		setSize(new Dimension(650, 700));
		setVisible(true);
		setLocationRelativeTo(null);
		
		openExcel();

		setCurrentPath(null);
	}

	
	private void setExcel(File inExcel) {
		if( null == inExcel || !inExcel.exists() ){
			this.excel = null;
			return ;
		}
		int index = inExcel.getName().lastIndexOf('.');
		String suffix = inExcel.getName().substring(index);
		this.defName = inExcel.getName().substring(0, index);
		String name =  UUID.randomUUID() + suffix;
		Path path = Paths.get("tree","sys", "temp", name );
		this.excel = path.toFile();
		InputStream in = null;
		try {
			in = new FileInputStream(inExcel);
			Files.copy(in, path );
		} catch ( Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					this, Lang.get("warning2"),
					Lang.get("warning"), JOptionPane.WARNING_MESSAGE);
		}finally{
			try{
				if( null != in )in.close();
			}catch( Exception e){}
		}
	}

	private void openExcel(){
		try {
			if( null != excel ){
				this.book = WorkbookFactory.create(this.excel);
				initTree( this.defName );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeExcel(){
		System.out.println("close excel file");
		try {
			if( null != book )book.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			if( null != excel )this.excel.delete();
		}catch( Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void initTree( String excelName ) {
		MyTreeNode root = (MyTreeNode) tree.getModel().getRoot();
		Element source = (Element) root.getUserObject();
		source.setAttribute("name", excelName);
		curPath = new TreePath(new Object[]{root} );
		for( Sheet sheet : this.book ){
			addElement("sheet", new Attr( "name", sheet.getSheetName() ));
		}
		curPath = null;
	}

	private void createTable() {
		Vector<String> columNames = new Vector<String>(2);
		columNames.add( Lang.get("table-attribute"));
		columNames.add( Lang.get("table-value") );
		Vector<Vector<String>> data = new Vector<Vector<String>>(5);
		for (int i = 0; i < 5; i++) {
			Vector<String> item = new Vector<String>(2);
			item.add("");
			item.add("");
			data.add(item);
		}
		editTable = new JTable(new MyDefaultTableModel(data, columNames));
		editTable.getModel().addTableModelListener(this);
	}

	private void addElement(String tagName, Attr... attrs) {
		if (null == curPath)
			return;
		MyTreeNode leaf = null;
		Node docNode = null;
		Element docChild = null;
		leaf = (MyTreeNode) curPath.getLastPathComponent();
		docNode = (Node) leaf.getUserObject();
		// create new node
		docChild = docNode.getOwnerDocument().createElement(tagName);
		docChild.setAttribute("name", tagName + "Name");
		if (tagName.equals("sheet")) {
			docChild.setAttribute("data", "0");
			docChild.setAttribute("unit", "0");
			docChild.setAttribute("title", "0");
		} else if (tagName.equals("field")) {
			docChild.setAttribute("colum", "0");
		}
		for (Attr a : attrs) {
			docChild.setAttribute(a.getName(), a.getValue());
		}
		docNode.appendChild(docChild);
		leaf.add(new MyTreeNode(docChild));
		// make new node visible
		ArrayList<Object> o = new ArrayList<Object>();
		o.addAll(Arrays.asList(curPath.getPath()));
		o.add(leaf);
		tree.makeVisible(new TreePath(o.toArray()));

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
				if (removeEmptyElement((Element) node)) {
					parent.removeChild(node);
					countElement--;
					index--;
				}
			}
		}
		String nodeName = parent.getNodeName();
		return (countElement == 0 && (nodeName.equals("sheet") || nodeName.equals("merg")));
	}

	public void setCurrentPath(TreePath path) {
		curPath = path;
		cleanTable();
		if (null == curPath) {
			delButton.setEnabled(false);
			sheetButton.setEnabled(false);
			mergButton.setEnabled(false);
			fieldButton.setEnabled(false);
			return;
		}
		// if not null the get the new current path leaf node
		MyTreeNode leaf = (MyTreeNode) curPath.getLastPathComponent();
		Element node = (Element) leaf.getUserObject();
		// Change add buttons enable stage
		leaf.getParent();
		delButton.setEnabled(leaf.getParent() != null);
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

	private void cleanTable() {
		ignorTableValueChange = true;
		TableModel model = editTable.getModel();
		for (int i = 0; i < 5; i++) {
			model.setValueAt("", i, 0);
			model.setValueAt("", i, 1);
		}
		tableRow = 0;
		ignorTableValueChange = false;
	}

	private void setTableValue(Element node) {
		int count = 0;
		String attrs[] = new String[] { "name", "data", "unit", "title", "colum" };
		TableModel model = editTable.getModel();
		ignorTableValueChange = true;
		for (int i = 0; i < attrs.length; i++) {
			String value = node.getAttribute(attrs[i]); 
			
			if (!value.trim().equals("") ) {
				model.setValueAt( attrs[i], count, 0);
				model.setValueAt( value, count, 1);
				count++;
			}
		}
		tableRow = count - 1;
		ignorTableValueChange = false;
	}

	private void readTitle(String sheetName, int titlIndex, int unitIndex) throws Exception {
		if (null == book)
			return;
		Sheet sheet = book.getSheet(sheetName);
		if (null == sheet) {
			throw new Exception("can't open sheet " + sheetName);
		}
		Row titleRow = sheet.getRow(titlIndex);
		if (null == titleRow) {
			throw new Exception("can't read title row " + titlIndex);
		}
		Row unitRow = sheet.getRow(unitIndex);
		String unitStr = "", titleStr;
		for (Cell cell : titleRow) {
			titleStr = utils.Utils.getCellStringValue(cell);
			Cell unitCell = unitRow.getCell(cell.getColumnIndex());
			if (null != unitCell) {
				unitStr = utils.Utils.getCellStringValue(unitCell);
			}
			if (null != titleStr) {
				int columnIndex = cell.getColumnIndex();
				addElement("field", new Attr("name", titleStr), new Attr("unit", unitStr),
						new Attr("colum", String.valueOf(columnIndex)));
			}
		}
	}

	public static void main(String[] args) {
		new TitleArchitectureFile("tree/major.xml", null);
	}


	public void dispose() {
		closeExcel();
		super.dispose();
	}

	public String readTableValue(String attr) {
		int index = 0;
		TableModel model = editTable.getModel();
		try {
			while (!attr.equals(model.getValueAt(index, 0))) {
				index++;
			}
		} catch (Exception e) {
			return null;
		}
		return model.getValueAt(index, 1).toString();
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		JTree source = (JTree) e.getSource();
		TreePath path = source.getPathForLocation(e.getX(), e.getY());
		if (null != path) {
			setCurrentPath(path);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		System.out.println( e.getFirstRow() + ":" + tableRow );
		if (ignorTableValueChange)
			return;
		if (!editTable.isCellEditable(e.getFirstRow(), e.getColumn()))
			return;
		
		if (null == curPath)
			return;
		
		
		TableModel model = editTable.getModel();
		if( e.getFirstRow() > tableRow ){
			return ;
		}
		String attr = model.getValueAt(e.getFirstRow(), e.getColumn() - 1).toString();
		
		String changedValue = model.getValueAt(e.getFirstRow(), e.getColumn()).toString();
		changedValue = changedValue.trim();

		// if is title then read the column title
		if (attr.equals("title")) {
			String sheetName = readTableValue("name");
			String title = readTableValue("title");
			String unit = readTableValue("unit");
			try {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) curPath.getLastPathComponent();
				treeNode.removeAllChildren();
				readTitle(sheetName, Integer.parseInt(title), Integer.parseInt(unit));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage());
			}
		}

		// change value on xml element
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) curPath.getLastPathComponent();
		Element docNode = (Element) treeNode.getUserObject();
		docNode.setAttribute(attr, changedValue);
		tree.updateUI();
	}


	private File saveXMLFile(){
		DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) tree.getModel().getRoot();
		if (null == treeRoot)return null;
		Node docNode = (Node) treeRoot.getUserObject();
		if (!format(docNode.getOwnerDocument()))return null;
		if( this.defName.equals("") )
			defName = utils.Utils.getFileName(this.sourceFile.getName());
		if( this.defName.equals("") )
			defName = "descript";
		File file = utils.Utils.saveXML(docNode.getOwnerDocument(), this.defName );
		return file;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		MyButton button = (MyButton) e.getSource();
		System.out.println("click button" + button.getId() );

		switch (button.getId() ) {
		case "button-remove":
			if (null == curPath)
				return;
			MyTreeNode leaf = (MyTreeNode) curPath.getLastPathComponent();
			if (leaf.getParent() == null)
				return;
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
		case "button-sheet":
			addElement("sheet");
			break;
		case "button-columns":
			addElement("merg");
			break;
		case "button-column":
			addElement("field");
			break;
		case "button-save":
			File file = saveXMLFile();
			if( null != file ){
				new AttributeMappingFile(file);
			}
			dispose();
			break;
		default:
			return;
		}
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}
	
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

}
