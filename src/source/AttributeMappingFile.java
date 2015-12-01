package source;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.FieldMapping;
import common.MyTreeCellRender;
import common.MyTreeNode;


public class AttributeMappingFile extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JTree templateTree;
	private JTree sourceTree;
	private JPanel btnPanel;
	private JButton equalBtn;
	
	private ButtonHandler btnHandler;
	private DefaultListModel<FieldMapping> model;
	private JList<FieldMapping> mapList;
	private JButton removeBtn;
	private JButton saveBtn;
	private JButton bigBtn;
	private JButton smallBtn;
	private JFrame frame;
	
	private TreeSelectHandler treeHandler;
	private File file;
	private Document doc ;
	//源文件被选中节点在模板中的映射节点
	private ArrayList<MyTreeNode> mappedNode = new ArrayList<MyTreeNode>();
	
 	public AttributeMappingFile( File file){
		super("Metadata Mapping");
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.frame = this;
		
		btnHandler = new ButtonHandler();
		treeHandler = new TreeSelectHandler();
		
		this.setLayout( new GridBagLayout() );
		this.file = file;
		try {
			templateTree = new JTree( utils.Utils.createTree("tree/sys/templateTree.xml") );
			templateTree.setLargeModel(true);
			templateTree.setRowHeight(18);
		} catch (Exception e) {
			System.out.println("Can't open templateTree.xml");
			e.printStackTrace();
			return ;
		}
		MyTreeCellRender render = new MyTreeCellRender();
		templateTree.setCellRenderer(render);
		templateTree.addTreeSelectionListener(treeHandler);
		templateTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane templatePane = new JScrollPane( templateTree );
		
		try {
			sourceTree = new JTree( utils.Utils.createTree(file));
			sourceTree.setLargeModel(true);
			sourceTree.setRowHeight(18);
			MyTreeNode root = (MyTreeNode) sourceTree.getModel().getRoot();
			this.doc = ((Node)root.getUserObject()).getOwnerDocument();
		} catch (Exception e) {
			System.out.println("Can't open blankSourceTree.xml");
			e.printStackTrace();
			return ;
		}
		sourceTree.setCellRenderer(render);
		sourceTree.addTreeSelectionListener(treeHandler);
		sourceTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane sourcePane = new JScrollPane(sourceTree);
		
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridBagLayout() );
		equalBtn = new JButton("=");
		equalBtn.setToolTipText("equals");
		equalBtn.addActionListener(btnHandler);
		bigBtn = new JButton(">");
		bigBtn.setToolTipText("superclass");
		bigBtn.addActionListener(btnHandler);
		smallBtn = new JButton("<");
		smallBtn.setToolTipText("subclass");
		smallBtn.addActionListener(btnHandler);
		btnPanel.add( new JPanel(), new GridBagConstraints( 0, 0, 100, 40, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0 , 0 ));
		btnPanel.add( equalBtn,     new GridBagConstraints( 0, 40, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),
				0 , 0 ));
		btnPanel.add( smallBtn,     new GridBagConstraints( 0, 50, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),
				0 , 0 ));
		btnPanel.add( bigBtn,     new GridBagConstraints( 0, 60, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),
				0 , 0 ));
		btnPanel.add( new JPanel(), new GridBagConstraints( 0, 70, 100, 30, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0 , 0 ));
		
		JPanel topPane = new JPanel();
		topPane.setLayout(new GridLayout(1, 2));
		//--------change topPane layout
		topPane.setLayout(new GridBagLayout() );
		topPane.add( sourcePane, new GridBagConstraints( 0,0,40,1,0.5,0.5,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0),
				0,0));
		topPane.add( btnPanel, new GridBagConstraints( 40,0,20,1,0,0,
				GridBagConstraints.CENTER,
				GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0),
				0,0));
		topPane.add( templatePane, new GridBagConstraints( 60,0,40,1,0.5,0.5,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0),
				0,0));
		
		add(topPane, new GridBagConstraints( 0, 0, 100, 70, 1, 0.7,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0),
				0, 0 ) );
		
		model = new DefaultListModel<FieldMapping>();
		mapList = new JList<FieldMapping>(model);
		initMapList();
		JScrollPane mapPane = new JScrollPane(mapList);
		removeBtn = new JButton("Remove");
		removeBtn.addActionListener(btnHandler);
		saveBtn = new JButton("Save");
		saveBtn.addActionListener(btnHandler);
		JMenuBar btnsBar = new JMenuBar();
		FlowLayout btnsBarlayout = new FlowLayout();
		btnsBarlayout.setAlignment(FlowLayout.CENTER);
		btnsBar.setLayout(btnsBarlayout);
		btnsBar.add(saveBtn);
		btnsBar.add(removeBtn);
		JPanel bottom = new JPanel();
		bottom.setLayout( new BorderLayout() );
		bottom.add(mapPane, BorderLayout.CENTER);
		bottom.add( btnsBar, BorderLayout.SOUTH );
		add(bottom, new GridBagConstraints(0, 70, 100, 30, 1, 0.3, 
				GridBagConstraints.CENTER, 
				GridBagConstraints.BOTH, 
				new Insets(0, 0, 0, 0),
				0, 0 ) );
		setSize( 600, 600 );
		setLocationRelativeTo(null);
		setVisible(true);
		
		MyTreeNode r = (MyTreeNode) templateTree.getModel().getRoot();
		MyTreeNode major = (MyTreeNode) r.getChildAt(2);
		
		System.out.println( major );
		((MyTreeNode) major.getChildAt(10)).setMatched(true);
		((MyTreeNode) major.getChildAt(11)).setMatched(true);
		((MyTreeNode) major.getChildAt(12)).setMatched(true);	
		
	}
	private void initMapList() {
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(this.file);
			Element book = doc.getDocumentElement();
			NodeList list = book.getElementsByTagName("field");
			for( int i = 0; i < list.getLength(); i++ ){
				Element ele = (Element) list.item(i);
				String mapping = ele.getAttribute("mapping").trim();
				if( !mapping.equals("") ){
					String path = utils.Utils.elementPathToString(ele);
					ArrayList<MyTreeNode> nodes = getTreeNodeByPath(sourceTree,path);
					MyTreeNode key = nodes.get( nodes.size() - 1 );
					String relation = mapping.substring(0, 2).trim();
					path = mapping.substring(2).trim();
					nodes = getTreeNodeByPath(templateTree,path);
					MyTreeNode value = nodes.get( nodes.size() - 1 );
					model.addElement(new FieldMapping(key, relation, value) );
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void removeMapItem() {
		int index = mapList.getSelectedIndex();
		if( index < 0 )return ;
		FieldMapping item = model.remove(index);
		MyTreeNode node = item.getKey();
		node.setMappedNode(false);
		Element element = (Element) node.getUserObject();
		element.removeAttribute("mapping");
		System.out.println("remove: " + item.toString() );
		sourceTree.updateUI();
	}
	
	class ButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			AbstractButton btn = (AbstractButton) e.getSource();
			System.out.println( btn.getText() );
			switch( btn.getText() ){
			case "=":
				addMapItem("=");
				break;
			case "<":
				addMapItem("<");
				break;
			case ">":
				addMapItem(">");
				break;
			case "Remove":
				removeMapItem();
				break;
			case "Save":
				utils.Utils.saveXML( doc , file);
				
				frame.dispose();
				break;
			default:
			}
		}

		
	}
	
	class TreeSelectHandler implements TreeSelectionListener{

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			
			JTree select = (JTree) e.getSource();
			if( select == templateTree )return ;
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) 
					select.getLastSelectedPathComponent();
			if( null == treeNode )return ;
			markMappedNode(treeNode);
			markMapItem( treeNode );
			templateTree.updateUI();
		}

		private void markMapItem(DefaultMutableTreeNode treeNode) {
			Element docNode = (Element) treeNode.getUserObject();
			String mapping = docNode.getAttribute("mapping").trim();
			if( mapping.equals("") )return ;
			String path = utils.Utils.elementPathToString(docNode);
			
			for( int i = 0; i < model.getSize(); i++ ){
				FieldMapping fm = model.get(i);
				if(fm.keyString().equals(path) ){
					mapList.setSelectedIndex(i);
					mapList.ensureIndexIsVisible(i);
					break;
				}
			}
		}

		private void markMappedNode(DefaultMutableTreeNode treeNode) {
			for( int i = 0; i < mappedNode.size(); i++ ){
				mappedNode.get(i).setMappedNode(false);
			}
			mappedNode.clear();
			Element docNode = (Element) treeNode.getUserObject();
			String mapping = docNode.getAttribute("mapping").trim();
			if( !mapping.equals("") ){
				ArrayList<MyTreeNode> nodes = getTreeNodeByPath(templateTree,mapping);
				templateTree.scrollPathToVisible(new TreePath( nodes.toArray()));
				MyTreeNode last = nodes.get(nodes.size()-1);
				last.setMappedNode(true);
				mappedNode.add(last);
			}
		}
		
	} 
	private void addMapItem(String relation){
		MyTreeNode s= (MyTreeNode) sourceTree.getLastSelectedPathComponent();
		MyTreeNode t= (MyTreeNode) templateTree.getLastSelectedPathComponent();
		if( checkMapping( s, t ) ){
			FieldMapping m = new FieldMapping( s , relation, t );
			Element keyEle = (Element) s.getUserObject();
			keyEle.setAttribute("mapping",  m.mapString() );
			for( int i = 0; i < model.size(); i++ ){
				FieldMapping fm = model.getElementAt(i);
				if( fm.keyString().equals(m.keyString()) ){
					model.remove(i);
				}
			}
			model.addElement( m );
			mapList.setSelectedIndex(model.size()-1);
			mapList.ensureIndexIsVisible(model.size()-1);
		}else{
			JOptionPane.showMessageDialog(null, "fubidden mapping");
		}
	}
	//不允许同一张Sheet里的两列对应到同一列，extraColumn除外
	private boolean checkMapping( MyTreeNode key, MyTreeNode value ) {
		if( null == key || !key.isLeaf() )return false;
		if( null == value || !value.isLeaf() )return false;
		Element valueEle = (Element) value.getUserObject();
		if( valueEle.getAttribute("name").trim().equals("ExtraColumn")){
			return true;
		}
		Element element = (Element) key.getUserObject();
		while( null != element && !element.getNodeName().equals("sheet") ){
			element = (Element)element.getParentNode();
		}
		if( null == element )return false;
		
		String mapInfo = utils.Utils.elementPathToString(valueEle);
		NodeList list = element.getElementsByTagName("field");
		for( int i = 0; i < list.getLength(); i++ ){
			Element field = (Element) list.item(i);
			String map = field.getAttribute("mapping");
			if( map.endsWith( mapInfo ) ){
				return false;
			}
		}
		return true;
	}
	
	
	public ArrayList<MyTreeNode> getTreeNodeByPath( JTree tree, String path ){
		String[] nodes = path.split(">");
		MyTreeNode parent = (MyTreeNode) 
				tree.getModel().getRoot();
		MyTreeNode node = null;
		ArrayList<MyTreeNode> nodeList = new ArrayList<MyTreeNode>();
		nodeList.add( parent );
		for( int i = 1; i < nodes.length; i++ ){
			node = getTreeNodeByDocNodeName(parent, nodes[i] );
			if( null == node )break;
			parent = node;
			nodeList.add( parent );
		}
		return nodeList;
	}
	public MyTreeNode getTreeNodeByDocNodeName( DefaultMutableTreeNode parent, String name ){
		@SuppressWarnings("unchecked")
		Enumeration<MyTreeNode> enumrate = parent.children();
		while( enumrate.hasMoreElements() ){
			MyTreeNode child = enumrate.nextElement();
			Element e = (Element) child.getUserObject();
			String value = e.getAttribute("name");
			if( value.trim().equals(name))return child;
		}
		return null;
	}
	public static void main( String[] args ){
		new AttributeMappingFile(new File("tree/major.xml"));
	}
}
