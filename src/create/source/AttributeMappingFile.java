package create.source;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.tree.TreeSelectionModel;
import common.LeafMap;
import common.MyTreeNode;


public class AttributeMappingFile extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JTree templateTree;
	private JTree sourceTree;
	private JPanel btnPanel;
	private JButton equalBtn;
	
	private ButtonHandler btnHandler;
	private DefaultListModel<LeafMap> model;
	private JList<LeafMap> mapList;
	private JButton removeBtn;
	private JButton saveBtn;
	private JButton bigBtn;
	private JButton smallBtn;
	public AttributeMappingFile(){
		super("metadata mapping");
		btnHandler = new ButtonHandler();
		this.setLayout( new GridBagLayout() );
		
		
		try {
			templateTree = new JTree( utils.Utils.createTree("tree/templateTree.xml"));
		} catch (Exception e) {
			System.out.println("Can't open templateTree.xml");
			e.printStackTrace();
			return ;
		}
		templateTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane templatePane = new JScrollPane( templateTree );
		
		try {
			sourceTree = new JTree( utils.Utils.createTree("tree/blankSourceTree.xml"));
		} catch (Exception e) {
			System.out.println("Can't open blankSourceTree.xml");
			e.printStackTrace();
			return ;
		}
		sourceTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane sourcePane = new JScrollPane(sourceTree);
		
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridBagLayout() );
		equalBtn = new JButton("=");
		equalBtn.addActionListener(btnHandler);
		bigBtn = new JButton(">");
		bigBtn.addActionListener(btnHandler);
		smallBtn = new JButton("<");
		smallBtn.addActionListener(btnHandler);
		btnPanel.add( new JPanel(), new GridBagConstraints( 0, 0, 100, 40, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0 , 0 ));
		btnPanel.add( equalBtn,     new GridBagConstraints( 0, 40, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
				0 , 0 ));
		btnPanel.add( smallBtn,     new GridBagConstraints( 0, 50, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
				0 , 0 ));
		btnPanel.add( bigBtn,     new GridBagConstraints( 0, 60, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
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
		
		model = new DefaultListModel<>();
		mapList = new JList<LeafMap>(model);
		JScrollPane mapPane = new JScrollPane(mapList);
		removeBtn = new JButton("Remove");
		removeBtn.addActionListener(btnHandler);
		saveBtn = new JButton("Save");
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
	}
	class ButtonHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			MyTreeNode s = null;
			MyTreeNode t = null;
			AbstractButton btn = (AbstractButton) e.getSource();
			System.out.println( btn.getText() );
			switch( btn.getText() ){
			case "=":
			case "<":
			case ">":
				s = (MyTreeNode) sourceTree.getLastSelectedPathComponent();
				t = (MyTreeNode) templateTree.getLastSelectedPathComponent();
				if( s.isLeaf() && t.isLeaf() ){
					model.addElement( new LeafMap( s,btn.getText(), t )) ;
				}else{
					JOptionPane.showMessageDialog(null, "just field node can mapping");
				}
				break;
			case "Remove":
				int index = mapList.getSelectedIndex();
				if( index > 0 ){
					model.remove(index);
				}
				break;
			default:
			}
			
		}
		
	}
	public static void main( String[] args ){
		new AttributeMappingFile();
	}
}
