package ontologyFramework.OFErrorManagement.OFGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import ontologyFramework.OFErrorManagement.OFGUI.allInstancesGUI.AllinstancesRunner;
import ontologyFramework.OFErrorManagement.OFGUI.allInstancesGUI.FrameworkSerializator;
import ontologyFramework.OFErrorManagement.OFGUI.allInstancesGUI.LegendRunner;
import ontologyFramework.OFErrorManagement.OFGUI.individualGui.IndividualGuiRunner;

//import ontologyGui.allInstancesGUI.AllInstancesDebug;
//import ontologyGui.allInstancesGUI.AllinstancesRunner;
//import ontologyGui.allInstancesGUI.LegendRunner;

public class GuiRunner implements Runnable {
	
	public GuiRunner(){
		new ClassExcange();
	}
	public GuiRunner( String defaultOntoName){
		new ClassExcange(defaultOntoName);
	}
	public GuiRunner( String defaultOntoName, Long treePer, Long indPer, Long instancePer, Long savePer){
		new ClassExcange(defaultOntoName, treePer, indPer, instancePer, savePer);
	}
	
	@Override
	public void run( ) {
		long initialTime = System.currentTimeMillis();
		while( true){			
			try {
				SwingUtilities.invokeLater( new Runner());
	
				long done = System.currentTimeMillis() - initialTime; 
				Thread.sleep( ClassExcange.getTreePeriod() - done);
				initialTime = System.currentTimeMillis();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch ( java.lang.IllegalArgumentException e1){
				System.out.println( "Gui runner miss dead-line !!");
			}
		}
	}
}


class Runner implements Runnable {
	
	static boolean init = false;

	private static boolean openLegend = true;
	private static boolean openLog = true;
	
	private static final JProgressBar progressBar = new JProgressBar( 0, 100);
	
	// run the GUI
    public void run( ) {
    	if( init)
    		ClassExcange.changeVisibilityProgressBar(true);
    	
    	if( ! init){
        	//create and show GUI
			ClassExcange.getFrameObj().setBounds( 70, 70, 160, 100);
			ClassExcange.getFrameObj().setPreferredSize( new Dimension( 340, 650));
			
			LoadOntology.updateOntology( init);
    	}
    	
		new ClassTree( init);
		ClassExcange.setHoldTreeObj( ClassTree.getTree());
		
		ClassExcange.getFrameObj().setAlwaysOnTop(false);
		
		if( ! init){
			ClassRootManager.setRootClass();
			ClassRootManager.updateExpandAll( init);
		
			ClassExcange.getFrameObj().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//JFrame.EXIT_ON_CLOSE);
			setWindows();
			ClassExcange.changeVisibilityProgressBar(true);
			ClassExcange.getFrameObj().setVisible(true);
			init = true;
		}
		//ClassRootManager.updateExpandAll( init);
		ClassExcange.getFrameObj().pack();
		ClassExcange.changeVisibilityProgressBar(false);
		
    }

	private static void setWindows() {
		
		buildBottomMenu();
			
		// build option panel
		JPanel panel = new JPanel();
		ClassExcange.getFrameObj().getContentPane().add(panel, BorderLayout.NORTH);
		ClassExcange.getFrameObj().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				ClassExcange.getFrameObj().setPreferredSize( e.getComponent().getSize());
			}
		});
		
		JButton lblOntologyName = new JButton(ClassExcange.ontologyNameLabel);
		lblOntologyName.setFont(new Font("Dialog", Font.BOLD, 10));
	
		lblOntologyName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// load ontology variable and set ClassExcange
				LoadOntology.updateOntology( init);			
			}
		});
		
		// care about update ontology from name
		ClassExcange.getOntoNameObj().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
				// load ontology variable and set ClassExcange
				if( e.getKeyCode() == ClassExcange.ENTER)
					LoadOntology.updateOntology( init);
				//add hint
				ClassExcange.getOntoNameObj().setToolTipText(ClassExcange.expandeXhekBoxTip);
				
			}
		});
		ClassExcange.getOntoNameObj().setFont(new Font("Dialog", Font.PLAIN, 10));
		ClassExcange.getOntoNameObj().setColumns(10);

		JButton lblNewLabel = new JButton(ClassExcange.classRootLabel);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				ClassRootManager.updateComboBox();
			}
		});

		
		// care about root class (combobox)
		ClassExcange.getClassRootObj().setFocusable(true);
		ClassExcange.getClassRootObj().requestFocusInWindow();
		ClassExcange.getClassRootObj().setEditable(true);
		ClassExcange.getClassRootObj().setFont(new Font("Dialog", Font.BOLD, 10));
		ArrayList<String>  allClbox = ClassRootManager.settRootWiev();
		ClassExcange.getClassRootObj().setModel(new DefaultComboBoxModel( allClbox.toArray()));
	/*	ClassExcange.getClassRootObj().getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			// NOT FIRING
			@Override
			public void keyReleased(KeyEvent e) {
				ClassRootManager.updateComboBox( e);
			}
		}); */
		

		JButton lblSearch = new JButton( ClassExcange.findLabel);
		lblSearch.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				ClassFindManager.updateComboBox();
			}
		});
		
		
		
		// care about find combobox
		ClassExcange.getFindItemObj().setFocusable(true);
		ClassExcange.getFindItemObj().setEditable(true);
		ArrayList<String>  allFindbox = ClassFindManager.settFindWiev();
		ClassExcange.getFindItemObj().setModel(new DefaultComboBoxModel( allFindbox.toArray()));
	/*	ClassExcange.getFindItemObj().getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			// NOT FIRING!!!!!!
			@Override
			public void keyReleased(KeyEvent e) {
				
				ClassFindManager.updateComboBox( e);
				
			}
		});*/
		
		ClassExcange.getFindItemObj().setFont(new Font("Dialog", Font.BOLD, 10));
		
		initializeOptionPanel( panel, lblSearch, lblOntologyName, lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		ClassExcange.getFrameObj().getContentPane().add(panel_1, BorderLayout.CENTER);
		
		addProgressBar();
		
		
		// care about expand all check box
		ClassExcange.getExpandAllObj().addItemListener(new ItemListener() {
			@Override
		    public void itemStateChanged(ItemEvent e) {
				ClassRootManager.updateExpandAll( init);	
		    }
		});
		
		initailizeTreePanel( panel_1);
		
	}

	private static void buildBottomMenu( ){
	
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Dialog", Font.BOLD, 10));
		ClassExcange.getFrameObj().setJMenuBar(menuBar);
	
		JButton btnAllInstances = new JButton( ClassExcange.allInstancesButtonLabel);
		btnAllInstances.setFont(new Font("Dialog", Font.BOLD, 10));
		menuBar.add(btnAllInstances);
		
		btnAllInstances.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if( openLog){
					openLog = false;
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								AllinstancesRunner frame = new AllinstancesRunner();
								frame.setVisible(true);
								frame.addWindowListener(new WindowAdapter() {
									@Override
									public void windowClosing(WindowEvent e) {
										openLog = true;
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
		
		JButton btnLegend = new JButton(ClassExcange.legendButtonLabel);
		btnLegend.setFont(new Font("Dialog", Font.BOLD, 10));
		menuBar.add(btnLegend);
		
		btnLegend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if( openLegend){
					openLegend = false;
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								LegendRunner frame = new LegendRunner();
								frame.setVisible(true);
								frame.addWindowListener(new WindowAdapter() {
									@Override
									public void windowClosing(WindowEvent e) {
										openLegend = true;
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
		
		JButton btnSave = new JButton( ClassExcange.serialiseFrameworkButtonLabel);
		btnSave.setFont(new Font("Dialog", Font.BOLD, 10));
		menuBar.add(btnSave);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Thread t = new Thread( new FrameworkSerializator());
				t.start();
			}
		});
	}

	private static void initailizeTreePanel( JPanel panel_1){
		ClassExcange.getExpandAllObj().setSelected(true);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(28)
							.addComponent(ClassExcange.getIntestLabelObj())
							.addPreferredGap(ComponentPlacement.RELATED, 165, Short.MAX_VALUE)
							.addComponent(ClassExcange.getExpandAllObj()))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(ClassExcange.getTreePanelObj(), GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(ClassExcange.getIntestLabelObj())
						.addComponent(ClassExcange.getExpandAllObj()))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(ClassExcange.getTreePanelObj(), GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
					.addContainerGap())
		);
		panel_1.setLayout(gl_panel_1);
	}
	
	private static void initializeOptionPanel( JComponent panel, JComponent lblSearch, JComponent lblOntologyName, JComponent lblNewLabel){
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(25)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblSearch)
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addComponent(ClassExcange.getFindItemObj(), 0, 287, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblOntologyName)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(ClassExcange.getOntoNameObj(), GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(51)
							.addComponent(ClassExcange.getClassRootObj(), 0, 212, Short.MAX_VALUE)))
					.addGap(18))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblOntologyName)
								.addComponent(ClassExcange.getOntoNameObj(), GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel)
								.addComponent(ClassExcange.getClassRootObj(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSearch)
								.addComponent(ClassExcange.getFindItemObj(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						
		)));
		panel.setLayout(gl_panel);
		
		//addProgressBar();
	}
	
	private static void addProgressBar(){
		
	    progressBar.setIndeterminate( true);
	    progressBar.setValue(0);
	    progressBar.setStringPainted( false);
	    progressBar.setVisible( true);
	    ClassExcange.setProgressBar( progressBar);
		ClassExcange.getFrameObj().getContentPane().add(
				ClassExcange.getProgressBar(), BorderLayout.SOUTH);
	}
	
}
