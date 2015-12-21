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

import javax.swing.DefaultListModel;
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
	
	private MyButton editButton;
	private MyButton importButton;
	private MyButton createButton;
	private MyButton removeButton;
	private MyButton saveButton;
	
	private JFileChooser xmlChooser ;
	
	private File excel;
	public XMLList( File excel ){
		this.excel = excel;
		//setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		updateList();
		add( new JScrollPane( list )  );
		
		JMenuBar bar = new JMenuBar();
		FlowLayout lay = new FlowLayout(); 
		lay.setAlignment(FlowLayout.RIGHT);
		bar.setLayout(lay);
		bar.setMargin(new Insets(0, 10, 0, 0));
		createButton = new MyButton("button-create");
		createButton.addActionListener(this);
		bar.add(createButton);
		importButton = new MyButton("button-import");
		importButton.addActionListener(this);
		bar.add(importButton);
		editButton = new MyButton("button-edit");
		editButton.addActionListener(this);
		bar.add(editButton);
		removeButton = new MyButton("button-remove");
		removeButton.addActionListener(this);
		bar.add(removeButton);
		saveButton = new MyButton("button-save");
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
		MyButton button = (MyButton) e.getSource();
		switch( button.getId()){
		case "button-edit":
			edit();
			break;
		case "button-import":
			importXML();
			break;
		case "button-create":
			new TitleArchitectureFile(excel);
			break;
		case "button-remove":
			remove();
			break;
		case "button-save":
			this.dispose();
			break;
			default:
		}
	}
	private void edit() {
		String fileName = list.getSelectedValue();
		if( null == fileName )return ;
		new TitleArchitectureFile("tree/" + fileName, null );
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
