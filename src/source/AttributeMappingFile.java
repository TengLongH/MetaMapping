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
import java.util.List;

import javax.swing.DefaultListModel;
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
import common.MyButton;
import common.MyTreeCellRender;
import common.MyTreeNode;
import utils.Lang;
import utils.Synonyms;


public class AttributeMappingFile extends JFrame 
implements ActionListener, TreeSelectionListener{
	
	private static final long serialVersionUID = 1L;
	private JTree templateTree;
	private JTree sourceTree;
	private JPanel btnPanel;
	private MyButton equalBtn;
	
	private DefaultListModel<FieldMapping> model;
	private JList<FieldMapping> mapList;
	private MyButton removeBtn;
	private MyButton saveBtn;
	private MyButton bigBtn;
	private MyButton smallBtn;
	private File file;
	private Document doc ;
	
	//源文件被选中节点在模板中的映射节点
	private ArrayList<MyTreeNode> mappedNode = new ArrayList<MyTreeNode>();
	private ArrayList<MyTreeNode> synonymsNode = new ArrayList<MyTreeNode>();
	
	List<MyTreeNode> pleaves;
	List<MyTreeNode> sleaves;
	private MyButton autoMapBtn;
	
 	public AttributeMappingFile( File file){
		super("Mapping Tool");
		//setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
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
		templateTree.addTreeSelectionListener(this);
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
		sourceTree.addTreeSelectionListener(this);
		sourceTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane sourcePane = new JScrollPane(sourceTree);
		
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridBagLayout() );
		equalBtn = new MyButton("button-equal");
		equalBtn.setToolTipText(Lang.get("button-equal-tip"));
		equalBtn.addActionListener(this);
		bigBtn = new MyButton("button-more");
		bigBtn.setToolTipText(Lang.get("button-more-tip"));
		bigBtn.addActionListener(this);
		smallBtn = new MyButton("button-less");
		smallBtn.setToolTipText(Lang.get("button-less-tip"));
		smallBtn.addActionListener(this);
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
		removeBtn = new MyButton("button-remove");
		removeBtn.addActionListener(this);
		saveBtn = new MyButton("button-save");
		saveBtn.addActionListener(this);
		autoMapBtn = new MyButton("button-automap");
		autoMapBtn.addActionListener(this);
		JMenuBar btnsBar = new JMenuBar();
		FlowLayout btnsBarlayout = new FlowLayout();
		btnsBarlayout.setAlignment(FlowLayout.CENTER);
		btnsBar.setLayout(btnsBarlayout);
		btnsBar.add(saveBtn);
		btnsBar.add(removeBtn);
		btnsBar.add( autoMapBtn );
		
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
		
		MyTreeNode root = (MyTreeNode) templateTree.getModel().getRoot();
		pleaves = utils.Utils.getLeafs( root );
		root = (MyTreeNode) sourceTree.getModel().getRoot();
		sleaves = utils.Utils.getLeafs( root );
		
		this.setIconImage(null);
		
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
		MyTreeNode keyNode = item.getKey();
		MyTreeNode valNode = item.getValue();
		keyNode.setMap(false);
		Element keyEle = (Element) keyNode.getUserObject();
		Element valEle = (Element) valNode.getUserObject();
		String keyName = keyEle.getAttribute("name");
		String valName = valEle.getAttribute("name");
		if( !keyName.equals(valName) ){
			Synonyms.remove(keyName, valName );
		}
		keyEle.removeAttribute("mapping");
		System.out.println("remove: " + item.toString() );
		sourceTree.updateUI();
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

	private void clearSynonymsMaker(){
		for( int i = 0; i < synonymsNode.size(); i++ ){
			synonymsNode.get(i).setSynonyms(false);
		}
		synonymsNode.clear();
	}
	private void clearMapMarker(){
		for( int i = 0; i < mappedNode.size(); i++ ){
			mappedNode.get(i).setMap(false);
		}
		mappedNode.clear();
	}
	private MyTreeNode getMapNode( MyTreeNode treeNode ){
		Element docNode = (Element) treeNode.getUserObject();
		String mapping = docNode.getAttribute("mapping").trim();
		if( !mapping.equals("") ){
			ArrayList<MyTreeNode> nodes = getTreeNodeByPath(templateTree,mapping);
			templateTree.scrollPathToVisible(new TreePath( nodes.toArray()));
			MyTreeNode last = nodes.get(nodes.size()-1);
			return last;
		}
		return null;
	}
	private void markMappedNode( MyTreeNode maped ) {
		maped.setMap(true);
		mappedNode.add(maped);
	}
	
	private void addMapItem(String relation){
		MyTreeNode s= (MyTreeNode) sourceTree.getLastSelectedPathComponent();
		MyTreeNode t= (MyTreeNode) templateTree.getLastSelectedPathComponent();
		try {
			addMapItem( s, relation, t );
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	private void addMapItem( MyTreeNode s, String relation, MyTreeNode t) throws Exception{
		
			FieldMapping m = new FieldMapping( s , relation, t );
			Element keyEle = (Element) s.getUserObject();
			Element valEle = (Element) t.getUserObject();
			String keyName = keyEle.getAttribute("name");
			String valName = valEle.getAttribute("name");
			
			if( !checkMapping( s, t ) ){
				StringBuffer buf = new StringBuffer();
				buf.append( valName );
				buf.append(" ");
				buf.append(Lang.get("message4"));
				throw new Exception(buf.toString());
			}
			
			if( !keyName.equals(valName) ){
				Synonyms.add(keyName, valName);
			}
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		MyButton btn = (MyButton) e.getSource();
		System.out.println( btn.getId() );
		switch( btn.getId() ){
		case "button-equal":
			addMapItem("=");
			break;
		case "button-less":
			addMapItem("<");
			break;
		case "button-more":
			addMapItem(">");
			break;
		case "button-remove":
			removeMapItem();
			break;
		case "button-save":
			utils.Utils.saveXML( doc , file);
			this.dispose();
			break;
		case "button-automap":
			autoMapping();
			break;
		default:
		}
	}
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		
		JTree select = (JTree) e.getSource();
		if( select == templateTree )return ;
		MyTreeNode treeNode = (MyTreeNode) 
				select.getLastSelectedPathComponent();
		if( null == treeNode )return ;
		clearMapMarker();
		clearSynonymsMaker();
		MyTreeNode maped = getMapNode(treeNode);
		if( null != maped ){
			markMappedNode( maped );
			markMapItem( treeNode );
		}else{
			findSynonyms(treeNode);
		}
		
		templateTree.updateUI();
	}
	
	private void autoMapping( ){
	
		for( int s = 0; s < sleaves.size(); s++ ){
			for( int p = 0; p < pleaves.size(); p++ ){
				Element selement = (Element) sleaves.get(s).getUserObject();
				if( !selement.getAttribute("mapping").equals("") ){
					continue;
				}
				if( match( sleaves.get(s), pleaves.get(p)) ){
					try {
						addMapItem( sleaves.get(s), "=", pleaves.get(p) );
					} catch (Exception e) {
						System.err.printf("%s:%s\n", 
								Lang.get("button-automap"),
								e.getMessage());
					}
				}
			} 
		}
	}
	private void findSynonyms( MyTreeNode snode ){
		Element element = (Element) snode.getUserObject();
		String name = element.getAttribute("name");
		List<String> synonyms = Synonyms.get( name );
		int i = 0;
		while( i < synonyms.size() ){
			MyTreeNode node = 
					getFieldByName(pleaves, synonyms.get(i));
			if( null == node ){
				synonyms.remove(i);
			}else{
				templateTree.scrollPathToVisible( new TreePath( node.getPath() ) );
				node.setSynonyms(true);
				synonymsNode.add(node);
				System.out.println( name + " sy: " + synonyms.get(i));
				i++;
			}
		}
	}
	private boolean match( MyTreeNode snode, MyTreeNode pnode ){
		Element selement = (Element) snode.getUserObject();
		Element pelement = (Element) pnode.getUserObject();
		String sname = selement.getAttribute("name");
		String pname = pelement.getAttribute("name");
		if( sname.equals("") )return false;
		return sname.trim().equals(pname.trim());
	}
	private MyTreeNode getFieldByName( List<MyTreeNode> list, String name ){
		
		for( MyTreeNode node: list ){
			Element element = (Element) node.getUserObject();
			if( element.getAttribute("name").equals(name.trim())){
				return node;
			}
		}
		return null;
	}
}
