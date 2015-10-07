package ontologyFramework.OFErrorManagement.OFGUI.allInstancesGUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;

import ontologyFramework.OFErrorManagement.OFGUI.ClassExcange;

@SuppressWarnings("serial")
public class Broswere extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void openBroswer() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Broswere frame = new Broswere();
					ClassExcange.setBroswareFrame( frame);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public Broswere() {
		setBounds(100, 100, 459, 312);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JFileChooser fileChooser = ClassExcange.getFileChooser();
		fileChooser = new MyFileChooser();
		fileChooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		fileChooser.setMultiSelectionEnabled( true);
		fileChooser.setCurrentDirectory(
				fileChooser.getFileSystemView().getParentDirectory(
                    new File( System.getProperty("user.dir"))));  
		contentPane.add(fileChooser, BorderLayout.CENTER);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public synchronized void windowClosing(WindowEvent e) {
		    	ClassExcange.getBroswareFrame().dispose();
		    	ClassExcange.setBroswareFrame( null);
			}
		});
		this.setTitle( "OFSystem State");
	}

}


@SuppressWarnings("serial")
class MyFileChooser extends JFileChooser {

	@Override
	public void approveSelection() {
        if (getSelectedFile().isFile()) {
        	ClassExcange.getLoadState_btn().setEnabled( true);
        	ClassExcange.getSaveState_btn().setEnabled( false);
        	
        	File[] files = this.getSelectedFiles();
        	String basePath = null;
        	Set< String> paths = new HashSet<String>(); 
        	for( int i = 0; i < files.length; i++){
        		paths.add( files[i].getAbsolutePath());
        		basePath = files[i].getPath();
        	}
        	ClassExcange.getBroswarePathtextField().setText( basePath);
        	ClassExcange.setChosenLoadingPaths( paths); 
        	closeDialog();
        } else {
        	if( this.getSelectedFiles().length == 1){
	        	ClassExcange.getLoadState_btn().setEnabled( false);
	        	ClassExcange.getSaveState_btn().setEnabled( true);
	        	
	        	String path = this.getSelectedFile().getAbsolutePath();
	        	ClassExcange.getBroswarePathtextField( ).setText( path);
	        	closeDialog();
        	}
        }
    }
	
    @Override
    public void cancelSelection() {
    	closeDialog();
    }
    
    private void closeDialog(){
    	ClassExcange.getBroswareFrame().dispose();
    	ClassExcange.setBroswareFrame( null);
    }
    
}