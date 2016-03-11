package main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import common.MyButton;
import common.WatchDir;
import common.Watcher;
import common.XMLList;
import source.Transport;
import utils.Config;
import utils.Lang;

public class Main extends JFrame implements ActionListener, Watcher{

	//private 
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> boxModel ;
	private JComboBox<String> xmlBox;
	private MyButton editXMLButton;
	private JTextField sourceField ;
	private JFileChooser sourceChooser = new JFileChooser();
	private MyButton sourceButton;
	private MyButton importButton;
	
	private File xml;
	private File source;
	
	private WatchDir watch;
	
	Thread thread;
	public Main(){

		try {
			Path dir = Paths.get("tree");
			watch = new WatchDir( dir );
			watch.addWatcher(this);
			thread = new Thread(watch);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setLayout( new GridLayout(3, 1) );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JPanel xmlPanel = new JPanel();
		xmlPanel.add(new JLabel( Lang.get("xml") + ":" ) );
		boxModel = new DefaultComboBoxModel<String>();
		xmlBox = new JComboBox<String>( boxModel );
		updateXMLBox();
		xmlBox.setPreferredSize(new Dimension(224, 26));
		xmlPanel.add( xmlBox );
		
		editXMLButton = new MyButton("button-edit");
		editXMLButton.addActionListener(this);
		editXMLButton.setPreferredSize(new Dimension(100, 26));
		xmlPanel.add( editXMLButton );
		
		
		JPanel sourcePanel = new JPanel();
		sourcePanel.add(new JLabel( Lang.get("source") + ":"));
		sourceField = new JTextField(20);
		sourceField.setPreferredSize(new Dimension(250, 26));
		sourcePanel.add(sourceField);
		sourceButton = new MyButton("button-choose");
		sourceButton.addActionListener(this);
		sourceButton.setPreferredSize(new Dimension(100, 26));
		sourceChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		sourceChooser.setFileFilter(new FileFilter() {
			public String getDescription() {return null;}
			public boolean accept(File f) {
				if( f.isDirectory() )return true;
				if( f.isHidden() )return false;
				String name = f.getName();
				if( name.startsWith("~"))return false;
				return name.endsWith(".xlsx")||name.endsWith(".xls");
			}
		});
		
		sourcePanel.add(sourceButton);
		
		
		JPanel buttonPanel = new JPanel();
		importButton = new MyButton("button-import");
		importButton.addActionListener(this);
		buttonPanel.add(importButton);
		
		add( sourcePanel );
		add( xmlPanel );
		add( buttonPanel );
		
		this.setIconImage(null);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);	
	}
	public void updateXMLBox() {
		File dir = new File("tree");
		if( !dir.exists() )return ;
		if( !dir.isDirectory() )return ;
		String[] xmlNames = dir.list( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		boxModel.removeAllElements();
		for( String item : xmlNames ){
			boxModel.addElement(item);
		}
		
	}
	
	public void dispose(){
		watch.stop();
		super.dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		MyButton button =  (MyButton) e.getSource();
		System.out.println( button.getId() );
		switch( button.getId() ){
		case "button-edit":
			editXML();
			break;
		case "button-choose":
			chooseSourceFile();
			break;
		case "button-import":
			transport();
			break;
			default:
		}
	}
	private void transport(){
		if( null == source )return ;
		Object item = boxModel.getSelectedItem();
		if( null == item || item.toString().trim().equals("") )return ;
		String xmlName = item.toString();
		xml = new File("tree/" + xmlName );
		if( !xml.exists() )return ;
		new Transport(source, xml);
	}
	private void editXML() {
		XMLList xmlList = new XMLList( source );
		watch.addWatcher(xmlList);
	}
	private void chooseSourceFile() {
		int value = sourceChooser.showOpenDialog(this);
		if( value == JFileChooser.APPROVE_OPTION ){
			source = sourceChooser.getSelectedFile();
			sourceField.setText(source.getAbsolutePath());
			System.out.println( "Select Excel:" + source.getName() );
		}
	}
	@Override
	public void listChange() {
		System.out.println("main: file list change");
		updateXMLBox();
	}

	public static void main(String[] args) {
//		try {
//			System.setErr( new PrintStream(new File("tree/sys/log.txt")));
//		} catch (FileNotFoundException e) {
//			JOptionPane.showMessageDialog(null, "can't find log file");
//			e.printStackTrace();
//		}
		
		try {
			Config.load();
			Lang.load( Config.get("lang") );
			new Main();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
