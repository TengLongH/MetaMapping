package common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import source.TitleArchitectureFile;

public class XMLList extends JFrame 
implements ActionListener, WindowListener, Watcher{

	private static final long serialVersionUID = 1L;

	private DefaultListModel<String> model;
	private JList<String> list;
	
	private JButton editButton;
	private JButton importButton;
	private JButton createButton;
	private JButton removeButton;
	private JButton saveButton;
	
	private JFileChooser xmlChooser ;
	
	private File excel;
	public XMLList( File excel ){
		this.excel = excel;
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		updateList();
		add( new JScrollPane( list )  );
		
		JMenuBar bar = new JMenuBar();
		FlowLayout lay = new FlowLayout(); 
		lay.setAlignment(FlowLayout.RIGHT);
		bar.setLayout(lay);
		bar.setMargin(new Insets(0, 10, 0, 0));
		createButton = new JButton("Create");
		createButton.addActionListener(this);
		bar.add(createButton);
		importButton = new JButton("Import");
		importButton.addActionListener(this);
		bar.add(importButton);
		editButton = new JButton("Edit");
		editButton.addActionListener(this);
		bar.add(editButton);
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		bar.add(removeButton);
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		bar.add(saveButton);
		add( bar, BorderLayout.SOUTH );
		
		xmlChooser = new JFileChooser();
		xmlChooser.setFileFilter(new FileFilter() {
			public String getDescription() {return null;}
			public boolean accept(File f) {
				if( f.isDirectory() )return true;
				if( f.getName().endsWith(".xml") )return true;
				return false;
			}
		});
		pack();
		setSize(new Dimension(400, 300));
		setLocationRelativeTo(null);
		setVisible(true);
	}
	public void updateList() {
		System.out.println("update file list");
		File dir = new File("tree");
		if( !dir.exists() )return ;
		if( !dir.isDirectory() )return ;
		String[] xmlNames = dir.list( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		if( null == model )model = new DefaultListModel<String>();
		model.clear();
		for( String xml : xmlNames ){
			model.addElement(xml);
		}
		if( null == list ){
			list = new JList<String>( model );
		}
	}
	public static void main(String[] args) {

		new XMLList(null);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		AbstractButton button = (AbstractButton) e.getSource();
		switch( button.getText()){
		case "Edit":
			edit();
			break;
		case "Import":
			importXML();
			break;
		case "Create":
			new TitleArchitectureFile(excel);
			break;
		case "Remove":
			remove();
			break;
		case "Save":
			this.dispose();
			break;
			default:
		}
	}
	private void edit() {
		String fileName = list.getSelectedValue();
		if( null == fileName )return ;
		new TitleArchitectureFile("tree/" + fileName, excel );
	}
	private void importXML() {
		int value = xmlChooser.showOpenDialog(this);
		if( JFileChooser.APPROVE_OPTION == value ){
			File choose = xmlChooser.getSelectedFile();
			File copy = copyToLib( choose );
			if( null != copy ){
				model.addElement( copy.getName() );
			}else{
				JOptionPane.showMessageDialog(this, "import failed");
			}
		}
	}
	
	private boolean remove(){
		String name = list.getSelectedValue();
		if( null != name ){
			model.removeElement(name);
			File file = new File( "tree/" + name );
			return file.delete();
		}
		return false;
	}
	@SuppressWarnings("resource")
	private File copyToLib(File choose) {
		File file = new File("tree/" + choose.getName() );
		while( file.exists() ){
			int value = JOptionPane.showConfirmDialog(this, "file hase exist replace it?", "Replace File", JOptionPane.YES_NO_OPTION );
			if( JOptionPane.NO_OPTION == value ){
				String name = JOptionPane.showInputDialog("Name");
				if( !name.endsWith(".xml") )name += ".xml";
				file = new File( "tree/" + name );
			}else{
				break;
			}
		}
		
		FileChannel out = null;
		FileChannel  in = null;
		ByteBuffer  buf = ByteBuffer.allocate(1024);
		try {
			out = new FileOutputStream( file ).getChannel();
			in  = new FileInputStream( choose ).getChannel();
			while( true ){
				buf.clear();
				int len = in.read(buf);
				if( len < 0 ){
					break;
				}
				buf.flip();
				out.write(buf);
			}
			in.close();
			in = null;
			out.close();
			out = null;
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if( out != null )out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}
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
	@Override
	public void listChange() {
		System.out.println("XML: file list changes");
		updateList();
	}
}
