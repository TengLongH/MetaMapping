package main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import descript.FileList;

public class Main extends JFrame implements ActionListener{

	//private 
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> boxModel ;
	private JComboBox<String> xmlBox;
	private JButton editXMLButton;
	private JTextField sourceField ;
	private JFileChooser sourceChooser = new JFileChooser();
	private JButton sourceButton;
	private JButton importButton;
	
	private File xml;
	private File source;
	
	public Main(){
		setLayout( new GridLayout(3, 1) );
		JPanel xmlPanel = new JPanel();
		xmlPanel.add(new JLabel("Descript:") );
		boxModel = new DefaultComboBoxModel<String>();
		xmlBox = new JComboBox<String>( boxModel );
		updateXMLBox();
		xmlBox.setPreferredSize(new Dimension(224, 26));
		xmlPanel.add( xmlBox );
		
		editXMLButton = new JButton("Edit");
		editXMLButton.addActionListener(this);
		editXMLButton.setPreferredSize(new Dimension(100, 26));
		xmlPanel.add( editXMLButton );
		add( xmlPanel );
		
		JPanel sourcePanel = new JPanel();
		sourcePanel.add(new JLabel("   Source:"));
		sourceField = new JTextField(20);
		sourceField.setPreferredSize(new Dimension(250, 26));
		sourcePanel.add(sourceField);
		sourceButton = new JButton("Choose");
		sourceButton.addActionListener(this);
		sourceButton.setPreferredSize(new Dimension(100, 26));
		sourceChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		sourceChooser.setFileFilter(new FileFilter() {
			public String getDescription() {return null;}
			public boolean accept(File f) {
				if( f.isDirectory() )return true;
				return f.getName().endsWith(".xlsx") &&
						(!f.isHidden()) && 
						(!f.getName().startsWith("~")) ;
			}
		});
		
		sourcePanel.add(sourceButton);
		add( sourcePanel );
		
		JPanel buttonPanel = new JPanel();
		importButton = new JButton("Import");
		importButton.addActionListener(this);
		buttonPanel.add(importButton);
		add( buttonPanel );
		
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
	
	public static void main(String[] args) {
		new Main();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		AbstractButton button = (AbstractButton) e.getSource();
		System.out.println( button.getText() );
		switch( button.getText() ){
		case "Edit":
			new FileList( this );
			break;
		case "Choose":
			int value = sourceChooser.showOpenDialog(this);
			if( value == JFileChooser.APPROVE_OPTION ){
				source = sourceChooser.getSelectedFile();
				sourceField.setText(source.getAbsolutePath());
			}
			break;
		case "Import":
			break;
			default:
		}
	}

	
}
