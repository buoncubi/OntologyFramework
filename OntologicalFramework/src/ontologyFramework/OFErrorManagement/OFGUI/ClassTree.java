package ontologyFramework.OFErrorManagement.OFGUI;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFGUI.individualGui.IndividualGuiRunner;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class ClassTree implements TreeSelectionListener {

	private static JTree tree;
	private static JScrollPane treePanel;
	private static boolean initialized = false;
	private static DefaultMutableTreeNode top;
	
	private static Integer individualGuiID = 0;
	private TreeRender model;
	
	private static OWLReferences ontoRef;

	// ########## CREATE TREE AND COMPONENTS ##############
	
	// constructor build up and run tree visualization
    public ClassTree( boolean init){
    	
	    ontoRef = ClassExcange.getOntoRef();
	    if( ontoRef != null){
	    	
	        new TreeWorker( this).execute();
	        
	        if( ! initialized){ // the frist time build the tree
	        	EntryInfo ei = new EntryInfo( ClassExcange.defaultRootTree, ClassExcange.classIcon);
	            top = new DefaultMutableTreeNode( ei);
	            createNodes( top, ClassExcange.getRootClassname());
	            EntryInfo.setTopTree(top);
	        	
	            ClassExcange.addtoTreePanelObj( top);
	            // configure a tree that allows one selection at a time.        
	            ClassExcange.getTreeObj().getSelectionModel().setSelectionMode
	    	           (TreeSelectionModel.SINGLE_TREE_SELECTION);
	    	    //Enable tool tips.
	            ToolTipManager.sharedInstance().registerComponent( ClassExcange.getTreeObj());
	            //Set the icon for leaf nodes.
	            model = new TreeRender();
	            ClassExcange.getTreeObj().setCellRenderer( model);
	            //Listen for when the selection changes.
	            ClassExcange.getTreeObj().addTreeSelectionListener( this);
	            
	            tree = ClassExcange.getTreeObj();
			    treePanel = ClassExcange.getTreePanelObj();
	        
	        	initialized = true;
	        }
	    }
	}
    
    /**
	 * @return the top
	 */
	protected synchronized DefaultMutableTreeNode getTop() {
		return top;
	}

	/**
	 * @param top the top to set
	 */
	protected synchronized void setTop(DefaultMutableTreeNode top) {
		ClassTree.top = top;
	}

	// create the tree recursively 
	protected synchronized DefaultMutableTreeNode createNodes(DefaultMutableTreeNode top, String superClass) {
		//call recursively go deeply in the tree 
		//while( ontoRef == null);
		synchronized( ontoRef.getReasoner()){
			// support variable to folder creation
	        DefaultMutableTreeNode category = null;
	        DefaultMutableTreeNode infcategory = null;
	        DefaultMutableTreeNode infindividual = null;
	        EntryInfo tmpUpdateNode = null;
	 
	        // initialize quantity
	        OWLClass topClass;
	        Set<OWLClass> inferedCl = null;
	        Set<OWLClassExpression> notInferedCl;
	        // manage not string for owl:Thing
	        if( superClass.equals( ClassExcange.Things)){
	        	topClass = ontoRef.getFactory().getOWLThing().asOWLClass();
	        	Set<OWLClass> tmpInf = ontoRef.getReasoner().getSubClasses( topClass, false).getFlattened();        	
	        	notInferedCl = topClass.getSubClasses( ontoRef.getOntology());
	        	for( OWLClass n :tmpInf)
	        		notInferedCl.add( n);
	        	
	        } else {
	        	topClass = ontoRef.getFactory().getOWLClass( superClass, ontoRef.getPm());
	        	inferedCl = ontoRef.getReasoner().getSubClasses( topClass, false).getFlattened();
	        	notInferedCl = topClass.getSubClasses(ontoRef.getOntology());
	        }
			
	        
	        if( ! notInferedCl.isEmpty()){
	        	// for all the non asserted class
		        for( OWLClassExpression infNo : notInferedCl){ 
		        	if( ! infNo.equals( ontoRef.getFactory().getOWLNothing())){
		        		//add a new class
			        	tmpUpdateNode = new EntryInfo( ClassExcange.getRenderer().render( infNo), ClassExcange.classIcon);
		        		category = new DefaultMutableTreeNode(tmpUpdateNode);
		        		top.add( category);
			        	
			    		// add not inferred individual
			    		Set<OWLIndividual> notInferedIn = infNo.asOWLClass().getIndividuals(ontoRef.getOntology());
			    		for( OWLIndividual noInfInd : notInferedIn){
			    			tmpUpdateNode = new EntryInfo( ClassExcange.getRenderer().render( noInfInd), ClassExcange.individualcon);
			    			category.add( new DefaultMutableTreeNode( tmpUpdateNode));
			    			
			    		}
			  
			    	
			    		// coming back up to the root of the tree NN EFFICIENTE add inferred individual
			    		boolean init = true;
						Set<OWLNamedIndividual> inferedIndividual = ontoRef.getReasoner().getInstances( infNo, false).getFlattened();
						if( ! inferedIndividual .isEmpty())
			        	for(OWLIndividual infInd : inferedIndividual){
			    			if( ! infInd.equals( ontoRef.getFactory().getOWLAnonymousIndividual())){
				    			if( ! notInferedIn.contains( infInd)){ 
				    				// if assert add notify [ ICON classInfIconn]
				    				if( init){
				    					// if is assert add class notify
				    					tmpUpdateNode = new EntryInfo( ClassExcange.indAssertLabel, ClassExcange.predIndIcon);
				    					infindividual = new DefaultMutableTreeNode( tmpUpdateNode);
				    					init = false;
				    				}
				    				tmpUpdateNode = new EntryInfo( ClassExcange.getRenderer().render(infInd), ClassExcange.individualInfIcon);
				    				DefaultMutableTreeNode a = new DefaultMutableTreeNode( tmpUpdateNode);
				    				infindividual.add( a);
				    				category.add( infindividual);
				        		}
			    			}
			    		}
			    		/*if( infindividual != null)
			    			category.add( infindividual);*/ 
			    		
			    		// call this function recorsively
			    		createNodes( category, ClassExcange.getRenderer().render(infNo));
						
			        }
		        }
	        }
	        
	        // asserted Class
	        boolean init = true;		
    		if( inferedCl != null)
				for(OWLClass inf : inferedCl){
	    			if( ! inf.equals( ontoRef.getFactory().getOWLNothing())){
		    			if( ! notInferedCl.contains( inf)){ 
		    				// if assert add notify [ ICON classInfIconn]
		    				if( init){
		    					// if is assert add class notify
		    					tmpUpdateNode = new EntryInfo( ClassExcange.classAssertLabel, ClassExcange.predClassIcon);
		    					infcategory = new DefaultMutableTreeNode( tmpUpdateNode);
		    					init = false;
		    				}
		    				tmpUpdateNode = new EntryInfo( ClassExcange.getRenderer().render(inf), ClassExcange.classInfIcon);
		    				DefaultMutableTreeNode a = new DefaultMutableTreeNode( tmpUpdateNode);
		        			infcategory.add( a);
		        		}
	    			}
	    		}
    		if( infcategory != null)
    			top.add( infcategory);
    		
    		
		}
		return( top);
    }
    
    // Required by TreeSelectionListener interface.
    // action listener: item selected
    public void valueChanged(TreeSelectionEvent e) {
        // implemented as double click
    } 
    public static synchronized void doubleClick( MouseEvent e){
    	//ClassExcange.changeVisibilityProgressBar(true);
    	if (e.getClickCount() == 2){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       tree.getLastSelectedPathComponent();
	        if (node == null) return;
	 
	        Object nodeInfo = node.getUserObject();
	        final EntryInfo entry = (EntryInfo) nodeInfo;
	        if( (entry.getIconNumber() == ClassExcange.individualcon) || (entry.getIconNumber() == ClassExcange.individualInfIcon)){
	        	
	        	//Thread t = new Thread( new IndividualGuiRunner(  entry.toString(),individualGuiID));
				//t.start();
				new IndividualGuiRunner(  entry.toString(),individualGuiID);
	        	
	        	individualGuiID++;
	        	if( individualGuiID >= Integer.MAX_VALUE)
	        		individualGuiID = 0;
	        }
	        tree.setSelectionPath(null);
		}
    	//ClassExcange.changeVisibilityProgressBar(false);
    }
    
    // manage icons in the tree
    private class TreeRender extends DefaultTreeCellRenderer {
       
		private static final long serialVersionUID = 1L;
	
         
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {
 
            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            
            synchronized( ClassExcange.getTreeObj()){
	            String txt = hasText(value);
	            if( txt != null)
	            	setText( txt);
	            
	            ImageIcon symb = hasIcond(value);
	            if ( symb != null)
	            	 setIcon( symb);
		        else {
		        	 System.err.println("icon missing; using default.");
	                 setToolTipText(null); //no tool tip
	            }
	            
	            if( txt != null && symb != null)
		            hasColor(txt);
		 
	            return this;
            }
        }
 
        protected ImageIcon hasIcond(Object value) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;
            try{
            	EntryInfo nodeInfo = (EntryInfo)(node.getUserObject());
            	return( nodeInfo.getIcon());
            }catch(java.lang.ClassCastException e){
            	e.printStackTrace();
            	return(null);
            }   
        }
        
        protected String hasText(Object value) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;
            try{
            	EntryInfo nodeInfo = (EntryInfo)(node.getUserObject());
            	return( nodeInfo.toString());
            }catch(java.lang.ClassCastException e){
            	e.printStackTrace();
            	return(null);
            }   
        }
    
        protected void hasColor( String txt) {
        	if( ClassExcange.isColorMatchSearch()){
				// match all the string
				if( ClassExcange.getAllColorToFollow().keySet().contains( txt)){
					setForeground( ClassExcange.getAllColorToFollow().get( txt));
				}else{
					setForeground(ClassExcange.getNullcolor());
				}		
			} else { 
				// search for string which contains
				for( String stri : ClassExcange.getAllColorToFollow().keySet()){
					String st = stri.toLowerCase();
					String s = txt.toLowerCase();
					if( s.contains( st)){
						setForeground( ClassExcange.getAllColorToFollow().get( stri));
						break;
					} else
						setForeground(ClassExcange.getNullcolor());
				}
			}
    	}
    }

    // ########## OPERATE OVER A TREE #############

    public static ArrayList<TreePath> find( String str, boolean exact){
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = top.depthFirstEnumeration();
        ArrayList<TreePath> paths = new ArrayList<TreePath>();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if( exact){
            	if (node.toString().equalsIgnoreCase(str)) {
            		paths.add( new TreePath(node.getPath()));
            	}
            } else {
            	if (node.toString().contains(str)) {
            		paths.add( new TreePath(node.getPath()));
            	}
            }
        }      
        return( paths);
    }
    
    // null if doesn't wxist
    // update the selected path
    // return the same path in the n
    public static TreePath isPath( JTree tree, TreePath path){
        DefaultMutableTreeNode top = (DefaultMutableTreeNode)tree.getModel().getRoot();
        if( top != null){
			Enumeration<DefaultMutableTreeNode> e = top.depthFirstEnumeration();
	        while (e.hasMoreElements()) {
	            DefaultMutableTreeNode node = e.nextElement();
	            TreePath exploring = new TreePath(node.getPath());
	        	if ( exploring.toString().equals( path.toString())){
	        		return( exploring);
	        	}
	        }
        }
        return( null);
    }
    
	public static void expandAll( boolean expand_collapse){
		try{
	    	for (int i = 1; i < tree.getRowCount(); i++)
	    		if( expand_collapse)
	    			tree.expandRow(i);
	    		else tree.collapseRow(i);
	    	treePanel.getViewport().add( ClassExcange.getTreeObj());
		}catch( ArrayIndexOutOfBoundsException e){}	
    }
	
	public static Enumeration<TreePath> saveExpansionState(JTree tree) {
        return tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
    }

    public static  void loadExpansionState(JTree tree, Enumeration<TreePath> enumeration) {
        if (enumeration != null) {
        	
            while (enumeration.hasMoreElements()) {
                TreePath treePath = enumeration.nextElement();
                treePath = isPath( tree, treePath);
                if( treePath != null)
                	tree.expandPath(treePath); // not working for leaf nodes
            }
        } else expandAll(true);
    }
    
	public static synchronized JTree getTree() {
		return tree;
	}

	/**
	 * @return the treePanel
	 */
	protected static synchronized JScrollPane getTreePanel() {
		return treePanel;
	}

	/**
	 * @param treePanel the treePanel to set
	 */
	protected static synchronized void setTreePanel(JScrollPane treePanel) {
		ClassTree.treePanel = treePanel;
	}

	/**
	 * @param tree the tree to set
	 */
	protected static synchronized void setTree(JTree tree) {
		ClassTree.tree = tree;
	}
}



class TreeWorker extends SwingWorker< DefaultMutableTreeNode, String> {

	ClassTree caller;
	DefaultMutableTreeNode top;
	boolean excecute = false;
	
	public TreeWorker( ClassTree caller){
		this.caller = caller;
		if( ! excecute)
			this.execute();
	}
	
	@Override
	protected synchronized DefaultMutableTreeNode doInBackground() throws Exception {
		ClassExcange.setNewTreeObj( caller.getTop());
		excecute = true;
	    ClassExcange.getProgressBar().setVisible( excecute);//.changeVisibilityProgressBar( true);
	    
	    EntryInfo ei  = new EntryInfo( ClassExcange.getRootClassname(), ClassExcange.classIcon);
	    
        top = new DefaultMutableTreeNode( ei);
	    top = caller.createNodes( top, ClassExcange.getRootClassname());
	    ClassExcange.setNewTreeObj( top);
		ClassExcange.getTreeObj().expandPath(new TreePath(top.getPath()));
	    
	    excecute = false;
	    ClassExcange.getProgressBar().setVisible( excecute);
	    return top;
	}
	
	@Override 
	protected synchronized void done(){
		synchronized( ClassTree.getTree()){
			if( top != null){
				caller.setTop( top);
				EntryInfo.setTopTree(top);
				
				ClassTree.setTree( ClassExcange.getTreeObj());
			    ClassTree.setTreePanel( ClassExcange.getTreePanelObj());
			    
			    //ClassExcange.setNewTreeObj( top);
			}
		}
	}
}