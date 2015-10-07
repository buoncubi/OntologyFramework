package ontologyFramework.OFErrorManagement.OFGUI;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

public class ClassFindManager {

	private static int cicleFocus;
	static ArrayList<TreePath> allPaths;
	  
	static{
		ComboBoxRenderer renderer = new ComboBoxRenderer();
        ClassExcange.getFindItemObj().setRenderer(renderer);
	}

    public static ArrayList<String> settFindWiev(){
    	//cicleFocus = 0;
    	allPaths = ClassTree.find( (String)ClassExcange.getFindItemObj().getEditor().getItem(), true);
        ArrayList<String> allFindbox = new ArrayList<String>();
        for( TreePath cl : allPaths){
        	if( ClassExcange.getFindItemObj().getEditor().getItem() == null)
        		allFindbox.add( cl.toString());
    		else if( cl.toString().toLowerCase().contains( ((String) ClassExcange.getFindItemObj().getEditor().getItem()).toLowerCase()))
    			allFindbox.add( cl.toString());
        }
        return( allFindbox);
    }
	
    //simulate enter
    public static void updateComboBox(){
    	cicleFocus = ClassExcange.getFindItemObj().getSelectedIndex();       	
		ClassExcange.getTreeObj().setSelectionPath( allPaths.get( cicleFocus));
    	ClassExcange.getTreeObj().scrollPathToVisible( allPaths.get(cicleFocus++));
		if( (cicleFocus >= allPaths.size()) || ( cicleFocus < 0))
			cicleFocus = 0;	
		ClassExcange.getFindItemObj().setSelectedIndex( cicleFocus);
    }
    public static void updateComboBox( KeyEvent e){
    	ClassExcange.changeVisibilityProgressBar(true);
    	
    	ArrayList<String> allFindbox;
    	boolean init = false;
    	
    	
    	if( e.getKeyCode() == ClassExcange.ENTER){
    		if( ! init)
    			cicleFocus = ClassExcange.getFindItemObj().getSelectedIndex();       	
    		ClassExcange.getTreeObj().setSelectionPath( allPaths.get( cicleFocus));
        	ClassExcange.getTreeObj().scrollPathToVisible( allPaths.get(cicleFocus++));
    		if( (cicleFocus >= allPaths.size()) || ( cicleFocus < 0))
    			cicleFocus = 0;	
    		ClassExcange.getFindItemObj().setSelectedIndex( cicleFocus);
    	} else if ( (e.getKeyChar() == ClassExcange.ESC)){
			ClassExcange.getFindItemObj().setFocusable(false);
			ClassExcange.getFindItemObj().setFocusable(true);
			init = true; 
		}else if( (e.getKeyCode() != ClassExcange.UP) && (e.getKeyCode() != ClassExcange.DOWN) && 
				(e.getKeyCode() != ClassExcange.RIGTH) && (e.getKeyCode() != ClassExcange.LEFT) && 
				(e.getKeyCode() != ClassExcange.CANC) ){
			init = true;
			allFindbox = settFindWiev();

			// store the txt 
			Object rootClTextStatus = ClassExcange.getFindItemObj().getEditor().getItem();
			int itemcount = ClassExcange.getFindItemObj().getItemCount();

			if( allFindbox.isEmpty()){
				for( String s: allFindbox){
					// update the combo list (forall items)
					for( int i = 0; i < itemcount; i++){
						String holdIt = (String) ClassExcange.getFindItemObj().getItemAt( i);
						// remove if it does not compare in the items
						if( ! s.contains( holdIt)){
							ClassExcange.getFindItemObj().removeItem( holdIt);
							itemcount--;
						}
						if( ! holdIt.contains( s)){
							ClassExcange.getFindItemObj().addItem( holdIt);
							itemcount++;
						}
					}
				}
			}  	// show all item
			else ClassExcange.getFindItemObj().setModel(new DefaultComboBoxModel( allFindbox.toArray()));
			
			ClassExcange.getFindItemObj().getEditor().setItem( rootClTextStatus);
			ClassExcange.getFindItemObj().setSelectedItem( rootClTextStatus);
			ClassExcange.getFindItemObj().showPopup();
		}
    	
    	ClassExcange.changeVisibilityProgressBar(false);
    }


}


class ComboBoxRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	public ComboBoxRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
		
		
		// change colour of what are you going to choose
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		if (isSelected) {
            setBackground(defaults.getColor("List.selectionBackground"));
            setForeground(defaults.getColor("List.selectionForeground"));
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
		
			
		setText( hasText( value));
		ImageIcon symb = hasIcond(value);
        if ( symb != null)
        	 setIcon( symb);
        else {
        	 System.err.println("icon missing; using default.");
             setToolTipText(null); //no tool tip
        }
		return this;
	}

	protected String hasText(Object value) {
		String path = ( String) value;
		path = path.replaceAll( ClassExcange.indAssertLabel, "");
		path = path.replaceAll( ClassExcange.classAssertLabel, "");
		return( path);
    }
	
	protected ImageIcon hasIcond(Object value) {
		return( EntryInfo.path2icon( ( String) value));
    }		
}
    

