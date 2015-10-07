package ontologyFramework.OFErrorManagement.OFGUI;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLClass;


public class ClassRootManager {

	static{
		setRootClass();
	}
	
	public static void setRootClass(){
		// update the name, also in the intestation label
		String in = (String) ClassExcange.getClassRootObj().getSelectedItem();
		if( in != null)
			ClassExcange.setRootClassname( in);
	}

    public static ArrayList<String> settRootWiev(){
    	OWLReferences ontoRef = ClassExcange.getOntoRef(); 
    	// update root class combo box items
        Set<OWLClass> allClass = ontoRef.getOntology().getClassesInSignature();
        String tmp;
        ArrayList<String> allClbox = new ArrayList<String>();
        allClbox.add( ClassExcange.Things); // care about OWLThing
        for( OWLClass cl : allClass){
        	tmp = ClassExcange.getRenderer().render( cl);
        	if( ! cl.equals( ontoRef.getFactory().getOWLThing())){
        		if( ClassExcange.getClassRootObj().getEditor().getItem() == null)
        			allClbox.add( tmp);
        		else if( tmp.toLowerCase().contains( ((String) ClassExcange.getClassRootObj().getEditor().getItem()).toLowerCase()))
        			allClbox.add( tmp);
        		
        	} //else System.out.println( "founf OWLThing ");
        }
        return( allClbox);
    }
    
    // simulate enter
    public static void updateComboBox(){
    	synchronized( ClassExcange.getTreeObj()){
	    	setRootClass(); // update root Class
			new ClassTree( true);
			updateExpandAll( true);
			ClassExcange.getClassRootObj().setFocusable(false);
			ClassExcange.getClassRootObj().setFocusable(true);
    	}
    }
    
    public static void updateComboBox( KeyEvent e){

		ClassExcange.changeVisibilityProgressBar(true);
		
    	ArrayList<String> allClbox = null;
    	if( e.getKeyCode() == ClassExcange.ENTER){
			setRootClass(); // update root Class
			new ClassTree( true);
			updateExpandAll( true);
			ClassExcange.getClassRootObj().setFocusable(false);
			ClassExcange.getClassRootObj().setFocusable(true);
    	} else if ( (e.getKeyChar() == ClassExcange.ESC)){
			ClassExcange.getClassRootObj().setFocusable(false);
			ClassExcange.getClassRootObj().setFocusable(true);
		}else if( (e.getKeyCode() != ClassExcange.UP) && (e.getKeyCode() != ClassExcange.DOWN) && 
				(e.getKeyCode() != ClassExcange.RIGTH) && (e.getKeyCode() != ClassExcange.LEFT) && 
				(e.getKeyCode() != ClassExcange.CANC) )
			allClbox = settRootWiev();
			
		// store the txt 
		Object rootClTextStatus = ClassExcange.getClassRootObj().getEditor().getItem();
		int itemcount = ClassExcange.getClassRootObj().getItemCount();

		if( allClbox != null){
			if( allClbox.isEmpty()){
				for( String s: allClbox){
					// update the combo list (for all items)
					for( int i = 0; i < itemcount; i++){
						String holdIt = (String) ClassExcange.getClassRootObj().getItemAt( i);
						// remove if it does not compare in the items
						if( ! s.contains( holdIt)){
							ClassExcange.getClassRootObj().removeItem( holdIt);
							itemcount--;
						}
						if( ! holdIt.contains( s)){
							ClassExcange.getClassRootObj().addItem( holdIt);
							itemcount++;
						}
					}
				}
			}  	// show all item
			else 
				ClassExcange.getClassRootObj().setModel(new DefaultComboBoxModel( allClbox.toArray()));
    	}
	
		ClassExcange.getClassRootObj().getEditor().setItem( rootClTextStatus);
		ClassExcange.getClassRootObj().setSelectedItem( rootClTextStatus);
		ClassExcange.getClassRootObj().showPopup();
		
		ClassExcange.changeVisibilityProgressBar(false);
    }

    public static void updateExpandAll( boolean initialized){
    	if( initialized)
    		ClassExcange.changeVisibilityProgressBar(true);
    	
    	boolean sel = ClassExcange.getExpandAllObj().isSelected();
    	ClassTree.expandAll( sel);
    	
    	if( initialized)
    		ClassExcange.changeVisibilityProgressBar(false);
    }
}
