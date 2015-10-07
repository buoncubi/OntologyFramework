package ontologyFramework.OFErrorManagement.OFGUI;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class EntryInfo {

	public String name;
	public int icon;

	private static DefaultMutableTreeNode topTree;

	public EntryInfo(String nameEntry, int iconEntry) {
		name = nameEntry;
		icon = iconEntry;
	}

	public int getIconNumber(){
		return icon;
	}

	public ImageIcon getIcon(){
		if ( icon == ClassExcange.classIcon) {
			return( ClassExcange.imClassIcon);
		} else if ( icon == ClassExcange.classInfIcon) {
			return( ClassExcange.imClassInfIcon);
		} else if ( icon == ClassExcange.individualcon) {
			return( ClassExcange.imIndividualIcon);
		} else if ( icon == ClassExcange.individualInfIcon) {
			return( ClassExcange.imIndividualInfIcon);
		} else if ( icon == ClassExcange.predClassIcon){
			return( ClassExcange.imClassPredIcon);
		} else if ( icon == ClassExcange.predIndIcon){
			return( ClassExcange.imIndividualPredIcon);
		} else return( null);
	}

	public String toString() {
		return name;
	}


	public static void setTopTree( DefaultMutableTreeNode top){
		topTree = top;
	}

	public static DefaultMutableTreeNode getTopTree(){
		return topTree;
	}

	public static ImageIcon path2icon( String path){
		Map<String, ImageIcon> imagePath = new HashMap< String, ImageIcon>();
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = topTree.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			try{
				EntryInfo nodeInfo = (EntryInfo)(node.getUserObject());
				imagePath.put( new TreePath(node.getPath()).toString(), nodeInfo.getIcon());
			}catch(java.lang.ClassCastException ez){
				ez.printStackTrace();
			}
		}
		return( imagePath.get( path));
	}


}
