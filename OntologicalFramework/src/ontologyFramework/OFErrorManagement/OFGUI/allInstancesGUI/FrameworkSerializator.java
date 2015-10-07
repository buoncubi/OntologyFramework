package ontologyFramework.OFErrorManagement.OFGUI.allInstancesGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFGUI.ClassExcange;
import ontologyFramework.OFRunning.OFSystemState;
import ontologyFramework.OFRunning.OFSerializator;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class FrameworkSerializator implements Runnable{

	public boolean stop = false;
	
	public FrameworkSerializator(){
		stop = false;
	}
	
	public void stopRun(){
		stop = true;
	}
	
	@Override
	public void run() {
		stop = false;
		long initialTime = System.currentTimeMillis();
		SavingFrame sf = new SavingFrame( this);
		SavingFrame.initialised = false;
		try {
			EventQueue.invokeAndWait( sf);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (InvocationTargetException e2) {
			e2.printStackTrace();
		}
		
		while( !stop){
			sf.updateOntologySet( );
			sf.updateBuildedSet();
			long done = System.currentTimeMillis() - initialTime; 
			try {
				Thread.sleep( ClassExcange.getSAVINGPERIOD() - done);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch ( java.lang.IllegalArgumentException e1){
				System.out.println( "Gui serialising miss dead-line !!");
			}
			initialTime = System.currentTimeMillis();
		}
	}
}


@SuppressWarnings("serial")
class SavingFrame extends JFrame implements Runnable{
	private JPanel contentPane;
	private FrameworkSerializator caller;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private static JPanel ontologySet_pane;
	private static JPanel buildedSet_pane;
	public static boolean initialised = false;

	static boolean doitSave = true;
	static boolean doitLoad = true;
	static SavingFrame frame; 

	@Override
	public void run() {
		try {
			if( ! initialised){
				frame = new SavingFrame( caller);
				frame.addWindowListener(new WindowAdapter(){
		            @Override
		            public void windowClosing(WindowEvent e){
		            	caller.stopRun();
		            	frame.setVisible( false);
		            }
		        });
			}
			frame.setVisible(true);
			frame.revalidate();
			frame.repaint();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateOntologySet(){
		
		Set<String> in = OWLReferences.getAllInstances().keySet(); // OWLReferences.getAll...
		Map<String, JCheckBox> map = ClassExcange.getSelectedOntoSet();

		for(String s : in){
			if( ! map.keySet().contains(s)){
				JCheckBox box = new JCheckBox( s);
				box.setSelected( true);
				ClassExcange.addToSelectedOntoSet(s, box);
				ontologySet_pane.add( box);
			}
		}
		
		Set<String> toRemove = new HashSet<String>();
		for(String s : map.keySet()){
			if( ! in.contains( s)){
				toRemove.add(s);
				ontologySet_pane.remove( map.get( s));
			}
		}
		ClassExcange.removeFromSelectedOntoSet( toRemove);
	}
	public void updateBuildedSet(){
		
		Set<String> in = OFBuiltMapInvoker.getAllInstances().keySet();
		Map<String, JCheckBox> map = ClassExcange.getBuildedOntoSet();

		for(String s : in){
			if( ! map.keySet().contains(s)){
				JCheckBox box = new JCheckBox( s);
				box.setSelected( true);
				ClassExcange.addToBuildedOntoSet(s, box);
				buildedSet_pane.add(box);
			}
		}
		
		Set<String> toRemove = new HashSet<String>();
		for(String s : map.keySet()){
			if( ! in.contains( s)){
				toRemove.add(s);
				buildedSet_pane.remove( map.get( s));
			}
		}
		ClassExcange.removeFromBuildedOntoSet(toRemove);
	}
	
	/**
	 * Create the frame.
	 */
	public SavingFrame( FrameworkSerializator caller) {
		if( caller != null)
			this.caller = caller;
		
		setBounds(100, 100, 496, 356);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel SavingTitle_label = new JLabel("Save/Load Ontological Framework State");
		SavingTitle_label.setBounds(105, 12, 299, 15);
		contentPane.add(SavingTitle_label);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 73, 224, 133);
		contentPane.add(scrollPane);
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(248, 73, 224, 133);
		contentPane.add(scrollPane_1);
		
		ontologySet_pane = new JPanel();
		scrollPane.setViewportView(ontologySet_pane);
		ontologySet_pane.setLayout(new GridLayout(0, 1, 0, 0));	
		buildedSet_pane = new JPanel();
		scrollPane_1.setViewportView(buildedSet_pane);
		buildedSet_pane.setLayout(new GridLayout(0, 1, 0, 0));
	
		JLabel ontologySet_label = new JLabel("Ontology Set");
		ontologySet_label.setBounds(73, 39, 103, 15);
		contentPane.add(ontologySet_label);
		
		JLabel BuildedSet_label = new JLabel("Builded Set");
		BuildedSet_label.setBounds(316, 39, 103, 15);
		contentPane.add(BuildedSet_label);
		
		ClassExcange.getLoadState_btn().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if( doitLoad){
					Set<String> paths =ClassExcange.getChosenLoadingPaths();
					OFSerializator.deserializeOFBuildedListInvoker( paths, ClassExcange.getRunSchedulerFlag());
					
					JOptionPane a = new JOptionPane();
					JOptionPane.showMessageDialog(a, "framework serialization loaded", "OF Serializated", JOptionPane.INFORMATION_MESSAGE);
					
					
					doitLoad = false;
				} else {
					doitLoad = true;
				}
			}
		});
		ClassExcange.getLoadState_btn().setEnabled( false);
		ClassExcange.getLoadState_btn().setBounds(260, 249, 212, 25);
		contentPane.add( ClassExcange.getLoadState_btn());
		
		
		ClassExcange.getSaveState_btn().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if( doitSave){
					String path;	
					if( ClassExcange.getExportAssertionFlag())
						path = null;
					else path = ClassExcange.getBroswarePathtextField().getText();
					
					Set< String> ontoToSerializeName = ClassExcange.getTrueSelectedOntoSet();
					Set< String> listToSerializeName = ClassExcange.getTrueBuildedOntoSet();
					boolean exportInf = ClassExcange.getExportAssertionFlag();
					Set<OFSystemState> listInvokers = OFSerializator.saveFrameworkState( ontoToSerializeName, listToSerializeName, path, exportInf);
					Set<String> savingPaths = OFSerializator.serializeObjectToFile( path, null, listInvokers);
					
					JOptionPane a = new JOptionPane();
					JOptionPane.showMessageDialog(a, "framework serializated in paths : " + savingPaths, "OF Serializated", JOptionPane.INFORMATION_MESSAGE);
					
					doitSave = false;
				} else {
					doitSave = true;
				}
			}
		});
		ClassExcange.getSaveState_btn().setBounds(258, 213, 213, 24);
		contentPane.add(ClassExcange.getSaveState_btn());
		 
		ClassExcange.getBroswarePathtextField().setBounds(12, 288, 355, 22);
		contentPane.add( ClassExcange.getBroswarePathtextField());
		ClassExcange.getBroswarePathtextField().setColumns(30);
		
		final JButton broswe_btn = new JButton("Explore");
		broswe_btn.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
			public void mouseReleased(MouseEvent e) {
		    	if( broswe_btn.isEnabled())
		    		if( ClassExcange.getBroswareFrame() == null)
		    			Broswere.openBroswer();
		    }
		});
		broswe_btn.setBounds(382, 286, 93, 25);
		contentPane.add(broswe_btn);
		
		final JCheckBox exportAsserted_ckBox = new JCheckBox("    Export Asserted Axiom");
		exportAsserted_ckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				boolean flag = exportAsserted_ckBox.isSelected();
				ClassExcange.setExportAssertionFlag( flag);
			}
		});
		exportAsserted_ckBox.setSelected( ClassExcange.getExportAssertionFlag());
		exportAsserted_ckBox.setBounds(12, 214, 242, 23);
		contentPane.add(exportAsserted_ckBox);
		
		final JCheckBox usePath_ckBox = new JCheckBox("    Use Default Path");
		usePath_ckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if( usePath_ckBox.isSelected()){
					ClassExcange.getBroswarePathtextField().setText( 
							ClassExcange.DEFAULTSERIALIZATIONPATHLABEL);
					ClassExcange.getBroswarePathtextField().setEnabled( false);
					broswe_btn.setEnabled(false);
					ClassExcange.getLoadState_btn().setEnabled( false);
				} else {
					ClassExcange.getBroswarePathtextField().setEnabled( true);
					broswe_btn.setEnabled( true);
					ClassExcange.getLoadState_btn().setEnabled( true);
				}
			}
		});
		usePath_ckBox.setSelected( true);
		usePath_ckBox.setBounds(12, 257, 242, 23);
		contentPane.add(usePath_ckBox);
		ClassExcange.getBroswarePathtextField().setEnabled( false);
		broswe_btn.setEnabled(false);
		
		final JCheckBox runScheduler_ckBox = new JCheckBox("Run Scheduler After Loading");
		runScheduler_ckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				boolean flag = runScheduler_ckBox.isSelected();
				ClassExcange.setRunSchedulerFlag( flag);
			}
		});
		runScheduler_ckBox.setSelected( ClassExcange.getRunSchedulerFlag());
		runScheduler_ckBox.setBounds(12, 236, 242, 23);
		contentPane.add(runScheduler_ckBox);
		
		initialised = true;
	}
}