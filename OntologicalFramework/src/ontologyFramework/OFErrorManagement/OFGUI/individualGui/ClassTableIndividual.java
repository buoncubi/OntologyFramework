
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package ontologyFramework.OFErrorManagement.OFGUI.individualGui;

/*
 * TableDialogEditDemo.java requires these files:
 *   ColorRenderer.java
 *   ColorEditor.java
 */

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFGUI.ClassExcange;
import ontologyFramework.OFErrorManagement.OFGUI.ClassTree;
import ontologyFramework.OFErrorManagement.OFGUI.EntryInfo;
import ontologyFramework.OFErrorManagement.OFGUI.LoadOntology;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is like TableDemo, except that it substitutes a
 * Favorite Color column for the Last Name column and specifies
 * a custom cell renderer and editor for the color data.
 */
@SuppressWarnings("serial")
public class ClassTableIndividual extends JPanel implements MouseListener {
    
	private boolean DEBUG = false;
	
    private static final String infoTitle =   "\n\n     ############    ONTOLOGY SOURCE    ########### \n\n";
    private static final String infodataDef = "\n\n     #######    DATA PROPERTY DEFINITION    ####### \n\n";
    private static final String infoobjeDef = "\n\n     #######   OBJECT PROPERTY DEFINITION    ###### \n\n";
    private static final String infoclasDef = "\n\n     ############   CLASS DEFINITION    ########### \n\n";
    private static final String infotypeDef = "\n\n     ###############   DATA TYPES    ############## \n\n";
    private static final String infodiffDef = "\n\n     ##########   DIFFERENT INDIVIDUALS    ######## \n\n";
    
    private final String individualname;
    private JTable table;	
    private Integer tableType;
    private List< List< Object>> tableData;
    private JTextArea textArea;
    private JFrame frame;
    private JScrollPane scrollPane;
    private MyTableModel model;

	private int selected;

	private OWLReferences ontoRef;

    private TextWorker textWorker;
    private TableWorker tableWorker;
    
	private String[] CN ;
	private Object[][] D;
	
	// constructor
	public ClassTableIndividual( String individualName, Integer tabletype, JTextArea textarea, JFrame jframe, Integer frameId) {
		super( new BorderLayout(0, 0));
		
        ontoRef = ClassExcange.getOntoRef();
		
        this.tableType = tabletype;
        this.frame = jframe;
    	this.individualname = individualName;
    	this.textArea = textarea;
    	
    	update();
    	
    	model = new MyTableModel();
        table = new JTable( model);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        //table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(table);
        
        //Enable tool tips.
        ToolTipManager.sharedInstance().registerComponent( table);
               
        //Set up renderer and editor for the Favorite Color column.
        //colorend = new ColorRenderer(true, frameId, tableType);
        //table.setDefaultRenderer( Color.class, colorend);
        table.setDefaultRenderer( String.class,  new ColorRenderer(true));
        //editor = new ColorEditor( table);
        //table.setDefaultEditor( Color.class,  editor);
       
        //Listen for when the selection changes.
        table.addMouseListener( this);
                
        // Custom cell weight
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
             
        // frame resize listener
        frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				double a = e.getComponent().getSize().getWidth() - scrollPane.getVerticalScrollBar().getSize().getWidth();
				setTableDimensions( a - 15);
			}
		});

        //Add the scroll pane to this panel.
        add( scrollPane, BorderLayout.CENTER);
           
    }
		
	public void saveSelection(){
		selected = table.getSelectedRow();
	}
	
	public void restoreSelection(){
		if( selected < table.getRowCount() && selected >= 0)
			table.setRowSelectionInterval( selected, selected);
	}
	
	// set the preferred dimensions of every cell in accord with the type of table
	// and the frame dimension
	private synchronized void setTableDimensions( double tableWidth){
		
		if( tableType == ClassExcange.dataPropertyTable){
			table.getColumnModel().getColumn( 0)
				.setPreferredWidth( ( int) (0.10 * tableWidth)); // Inferred?
			table.getColumnModel().getColumn( 1)
				.setPreferredWidth( ( int) (0.40 * tableWidth)); // property
			table.getColumnModel().getColumn( 2)
				.setPreferredWidth( ( int) (0.20 * tableWidth)); // value
			table.getColumnModel().getColumn( 3)
				.setPreferredWidth( ( int) (0.30 * tableWidth)); // type
			/*table.getColumnModel().getColumn( 4)
				.setPreferredWidth( ( int) (0.10 * tableWidth)); // flag*/
			
		} else if( tableType == ClassExcange.objectPropertyTable){
			table.getColumnModel().getColumn( 0)
				.setPreferredWidth( ( int) (0.10 * tableWidth));  // Inferred?
			table.getColumnModel().getColumn( 1)
				.setPreferredWidth( ( int) (0.40 * tableWidth)); // property
			table.getColumnModel().getColumn( 2)
				.setPreferredWidth( ( int) (0.50 * tableWidth)); // value
			/*table.getColumnModel().getColumn( 3)
				.setPreferredWidth( ( int) (0.10 * tableWidth));  // flag*/
			
		} else if( tableType == ClassExcange.classTable){
			table.getColumnModel().getColumn( 0)
				.setPreferredWidth( ( int) (0.10 * tableWidth)); // Inferred?
			table.getColumnModel().getColumn( 1)
				.setPreferredWidth( ( int) (0.35 * tableWidth)); // class
			/*table.getColumnModel().getColumn( 2)
				.setPreferredWidth( ( int) (0.10 * tableWidth)); // flag*/
			
		} else if( tableType == ClassExcange.sameIndividualTable){
			table.getColumnModel().getColumn( 0)
				.setPreferredWidth( ( int) (0.10 * tableWidth)); // Inferred?
			table.getColumnModel().getColumn( 1)
				.setPreferredWidth( ( int) (0.35 * tableWidth)); // same individual
			/*table.getColumnModel().getColumn( 2)
				.setPreferredWidth( ( int) (0.10 * tableWidth)); // flag*/
		}
	}
	
	// call the ontology printer and update the textArea in acoord with the type of table
	@Override
	public synchronized void mouseReleased(MouseEvent arg0) {
		textWorker = new TextWorker( this); // excecute by itself
	}
	
	public synchronized JTextArea renderText(){
		int selectionCount = table.getSelectedRow();
		JTextArea text = null;
		if( selectionCount != -1){
			if( tableType == ClassExcange.dataPropertyTable){
				text = dataPropertyInfo( (String) tableData.get(selectionCount).get( 1));
			} else if( tableType == ClassExcange.objectPropertyTable){
				text = objectPropertyInfo( (String) tableData.get(selectionCount).get( 1));
			} else if( tableType == ClassExcange.classTable){
				text = classInfo( (String) tableData.get(selectionCount).get( 1));
			} else if( tableType == ClassExcange.sameIndividualTable){
				text = sameIndividualInfo( (String) tableData.get(selectionCount).get( 1));
			}
			
		}
		table.repaint();
		return( text);	
	}
	// print data type title and the clicked data property definition
	private synchronized JTextArea dataPropertyInfo( String dataPropertyName) {
		
		BufferedReader br = LoadOntology.getOntologyTokens();
		StringBuilder info = new StringBuilder();
		StringBuilder typeTmp = new StringBuilder();
		StringBuilder sourceTmp = new StringBuilder();
		try {
			String line = br.readLine();

			// get title
			sourceTmp.append( infoTitle);
			while( ! line.startsWith( "AnnotationProperty: ")){ // compreso				
				if(( ( line = br.readLine()) != null) && ( ! line.trim().isEmpty())){
					sourceTmp.append( line + System.getProperty("line.separator"));
				}
			}
			
			// get data type to add at the end
			typeTmp.append( infotypeDef);
			line = br.readLine();
			while( ! line.startsWith( "ObjectProperty: ")){ // non compreso
				if(( line != null) && ( ! line.trim().isEmpty()))
					typeTmp.append( line + System.getProperty("line.separator"));
				line = br.readLine();
			}
			
			// Skip object property
			while( (line.startsWith( "ObjectProperty: ")) ||
					(line.startsWith( " ")) ||
					(line.trim().isEmpty())){
				if( line != null)
					line = br.readLine();
				else break;
			}
			
			// Catch the data property definition
			info.append( infodataDef);
			boolean stopper = (line.startsWith( "DataProperty: ")) ||
					(line.startsWith( " ")) ||
					(line.trim().isEmpty());
			while( stopper){
				if(( line != null) && ( ! line.trim().isEmpty())){ 
					if( ! line.startsWith( " ")){
						String tmp = line.replace( "DataProperty: ", "");
						if( tmp.trim().equals( dataPropertyName)){
							info.append( line + System.getProperty("line.separator"));
							line = br.readLine();
							while(( line != null) && (! line.startsWith( "DataProperty: "))
									&& ( ! line.startsWith( "Class: "))){
								info.append( line + System.getProperty("line.separator"));
								line = br.readLine();
							}
						}
					}
				}
				if( line == null)
					break;
				if( (line.startsWith( "DataProperty: ")) ||
						(line.startsWith( " ")) ||
						(line.trim().isEmpty()))
					stopper = true;
				else if( ! line.startsWith( "Class: "))
					stopper = true;
				else stopper = false;
				line = br.readLine();
			}
			info.append(sourceTmp);
			info.append( typeTmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println( info.toString());
		textArea.setWrapStyleWord(true);
		textArea.setText( info.toString());
		textArea. setCaretPosition(0);
		return( textArea);
	}
	// print title and the clicked object property definition
	private synchronized JTextArea objectPropertyInfo( String dataPropertyName) {
		
		BufferedReader br = LoadOntology.getOntologyTokens();
		StringBuilder info = new StringBuilder();
		StringBuilder typeTmp = new StringBuilder();
		StringBuilder sourceTmp = new StringBuilder();
		try {
			String line = br.readLine();
			
			// get title
			sourceTmp.append( infoTitle);
			while( ! line.startsWith( "AnnotationProperty: ")){ // compreso
				if(( ( line = br.readLine()) != null) && ( ! line.trim().isEmpty()))
					sourceTmp.append( line + System.getProperty("line.separator"));
			}
			
			// skip datata type
			line = br.readLine();
			while( (line.startsWith( "Datatype: ")) ||
					(line.startsWith( " ")) ||
					(line.trim().isEmpty())){
				line = br.readLine();
				if( line == null)
					break;
			}
			
			// chatch the object property definition
			info.append( infoobjeDef);
			boolean stopper = (line.startsWith( "ObjectProperty: ")) ||
					(line.startsWith( " ")) ||
					(line.trim().isEmpty());
			while( stopper){
				if(( line != null) && ( ! line.trim().isEmpty())){ 
					if( ! line.startsWith( " ")){
						String tmp = line.replace( "ObjectProperty: ", "");
						if( tmp.trim().equals( dataPropertyName)){
							info.append( line + System.getProperty( "line.separator"));
							line = br.readLine();
							while(( line != null) && (! line.startsWith( "ObjectProperty: "))
									&& ( ! line.startsWith( "Class: "))){
								info.append( line + System.getProperty("line.separator"));
								line = br.readLine();
							}
						}
					}
				}
				line = br.readLine();
				if( line == null)
					break;
				if( (line.startsWith( "ObjectProperty: ")) ||
						(line.startsWith( " ")) ||
						(line.trim().isEmpty()))
					stopper = true;
				else if( ! line.startsWith( "DataProperty: "))
					stopper = true;
				else stopper = false;
			}
			info.append( sourceTmp);
			info.append( typeTmp);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException ed) {}
		//System.out.println( info.toString());
		textArea.setWrapStyleWord(true);
		textArea.setText( info.toString());
		textArea. setCaretPosition(0);
		return( textArea);
	}
	// print title and the clickedclass definition
	private synchronized JTextArea classInfo( String dataPropertyName) {
		
		BufferedReader br = LoadOntology.getOntologyTokens();
		StringBuilder info = new StringBuilder();
		StringBuilder typeTmp = new StringBuilder();
		StringBuilder sourceTmp = new StringBuilder();
		try {
			String line = br.readLine();
			
			// get title
			sourceTmp.append( infoTitle);
			while( ! line.startsWith( "AnnotationProperty: ")){ // compreso
				if(( ( line = br.readLine()) != null) && ( ! line.trim().isEmpty()))
					sourceTmp.append( line + System.getProperty("line.separator"));
			}
			
			// skip data type, object and data property
			line = br.readLine();
			boolean stopper = (line.startsWith( "Datatype: ")) ||
					(line.startsWith( "ObjectProperty: ")) ||
					(line.startsWith( "DataProperty: ")) ||
					(line.startsWith( " ")) ||
					(line.trim().isEmpty());
			while( stopper){
				line = br.readLine();
				if( line == null)
					break;
				if( (line.startsWith( "Datatype: ")) ||
						(line.startsWith( "ObjectProperty: ")) ||
						(line.startsWith( "DataProperty: ")) ||
						(line.startsWith( " ")) ||
						(line.trim().isEmpty()))
					stopper = true;
				else if( ! line.startsWith( "Class: "))
					stopper = true;
				else stopper = false;
			}
			
			// chatch the object property definition
			info.append( infoclasDef);
			stopper = (line.startsWith( "Class: ")) ||
					(line.trim().isEmpty());
			while( stopper){
				if(( line != null) && ( ! line.trim().isEmpty())){ 
					if( ! line.startsWith( " ")){
						String tmp = line.replace( "Class: ", "");
						if( tmp.trim().equals( dataPropertyName)){
							info.append( line + System.getProperty( "line.separator"));
							line = br.readLine();
							while(( line != null) && (! line.startsWith( "Class: "))
									&& ( ! line.startsWith( "Class: "))){
								info.append( line + System.getProperty("line.separator"));
								line = br.readLine();
							}
						}
					}
				}
				line = br.readLine();
				if( line == null)
					break;
				if( (line.startsWith( "Class: ")) ||
						(line.startsWith( " ")) ||
						(line.trim().isEmpty()))
					stopper = true;
				else if( ! line.startsWith( "Individual: "))
					stopper = true;
				else stopper = false;
			}
			info.append( sourceTmp);
			info.append( typeTmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println( info.toString());
		textArea.setWrapStyleWord(true);
		textArea.setText( info.toString());
		textArea. setCaretPosition(0);
		return( textArea);
	}
	// print all under the individual, Disjoints.... Differents....
	private synchronized JTextArea sameIndividualInfo( String dataPropertyName) {
		
		BufferedReader br = LoadOntology.getOntologyTokens();
		StringBuilder info = new StringBuilder();
		StringBuilder typeTmp = new StringBuilder();
		StringBuilder sourceTmp = new StringBuilder();
		try {
			String line = br.readLine();
			
			// get title
			sourceTmp.append( infoTitle);
			while( ! line.startsWith( "AnnotationProperty: ")){ // compreso
				if(( ( line = br.readLine()) != null) && ( ! line.trim().isEmpty()))
					sourceTmp.append( line + System.getProperty("line.separator"));
			}
			line = br.readLine();
			
			// Skip up to the last individual
			while( !((( line.startsWith( "Disjoint")) ||
					( line.startsWith( "Different"))))){ // non compreso
				line = br.readLine();
				if( line == null)
					break;
			}
			
			// chatch the object property definition
			info.append( infodiffDef);
			while( (line != null)){
				if( ! line.trim().isEmpty()){
					info.append( line + System.getProperty( "line.separator"));
				}
				line = br.readLine();
			}
			
			info.append( sourceTmp);
			info.append( typeTmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println( info.toString());
		textArea.setWrapStyleWord(true);
		textArea.setText( info.toString());
		textArea. setCaretPosition(0);
		return( textArea);
	}
		
	// caller of table type
	public synchronized Object[][] renderItem() {
		//synchronized( ontoRef.getReasoner()){
			if( tableType == ClassExcange.dataPropertyTable){
				return( renderDataPropertyItem());
			} else if( tableType == ClassExcange.objectPropertyTable){
				return( renderObjPropertyItem());
			} else if( tableType == ClassExcange.classTable){
				return( renderClassItem());
			} else if( tableType == ClassExcange.sameIndividualTable){
				return( renderSameIndividualItem());
			}
		//}
		System.out.println( " returning null from renderIntem()");
		return null;
		
	}
    // Compute data property belong to the individual
	// boolean inferred, string DataProp, string value, string type, Boolean follow
	private synchronized Object[][] renderDataPropertyItem() {
		Map<OWLDataPropertyExpression, Set<OWLLiteral>> nonInf;
		Set<OWLDataProperty> allDataProperty;
		OWLNamedIndividual ind;
		synchronized( ontoRef.getReasoner()){
			ind = ontoRef.getFactory().getOWLNamedIndividual( individualname, ontoRef.getPm());
			nonInf = ind.getDataPropertyValues( ontoRef.getOntology());
			allDataProperty = ontoRef.getOntology().getDataPropertiesInSignature( true);
		}
	
		// get not asserted data property
		List< List< Object>> allProp = new ArrayList< List< Object>>();
		List< Object> temp = new ArrayList< Object>();
		//System.out.println( "----" + nonInf);
		for( OWLDataPropertyExpression dataProp : nonInf.keySet()){
			for( OWLLiteral literal : nonInf.get( dataProp)){
				temp.add( ClassExcange.imDataPropIcon);
				temp.add( ClassExcange.getRenderer().render( dataProp));
				temp.add( ClassExcange.getRenderer().render( literal));
				temp.add( ClassExcange.getRenderer().render( literal.getDatatype()));
				allProp.add( temp);
				//System.out.println( temp);
				temp = new ArrayList< Object>();
			}
		}

		// get all the object property and check if they can be asserted
		//System.out.println( "----" + inf);
		synchronized( ontoRef.getReasoner()){
			for ( OWLDataProperty dataProp : allDataProperty){
				Set< OWLLiteral> a = ontoRef.getReasoner().getDataPropertyValues( ind, dataProp);
				for ( OWLLiteral value : a){				
					temp.add( ClassExcange.imDataPropIcon);
					temp.add( ClassExcange.getRenderer().render( dataProp));
					temp.add( ClassExcange.getRenderer().render( value));
					temp.add( ClassExcange.getRenderer().render( value.getDatatype()));
					//System.out.println( " allProp " +allProp+ " contains temp " +temp);
					if( ! allProp.contains( temp)){
						temp.set( 0, ClassExcange.imDataPropInfIcon);
						allProp.add( temp);
					}
					//System.out.println( temp);
					temp = new ArrayList< Object>();
				}
			}
		}
		//System.out.println( " ------------------------------------------ ");
		
		// convert for JTable
		Object[][] ret = new Object[ allProp.size()][ getColoumsName().length];
		int r = 0;
		for( List<Object> row : allProp){
			//row.add( checkFollowStatus( row));
			int c = 0;
			for( Object elem : row){
				ret[ r][ c] = new Object();
				ret[ r][ c] = elem;
				c++;
			}
			r++;
		}
		tableData = allProp;
		//System.out.println( " $$$$$$$$$$$$$ " + allProp);
		return( ret);
	}
	// boolean inferred, string ObjProp, string value, Boolean follow
	private synchronized Object[][]  renderObjPropertyItem(){
		Map<OWLObjectPropertyExpression, Set<OWLIndividual>> nonInf;
		Set<OWLObjectProperty> allDataProperty;
		OWLNamedIndividual ind;
		synchronized( ontoRef.getReasoner()){
			ind = ontoRef.getFactory().getOWLNamedIndividual( individualname, ontoRef.getPm());
			nonInf = ind.getObjectPropertyValues( ontoRef.getOntology());
			allDataProperty = ontoRef.getOntology().getObjectPropertiesInSignature( true);
		}
		
		// get not asserted object property 
		List< List< Object>> allProp = new ArrayList< List< Object>>();
		List< Object> temp = new ArrayList< Object>();
		for( OWLObjectPropertyExpression dataProp : nonInf.keySet()){
			for( OWLIndividual literal : nonInf.get( dataProp)){
				temp.add( ClassExcange.imObjPropIcon);
				temp.add( ClassExcange.getRenderer().render( dataProp));
				temp.add( ClassExcange.getRenderer().render( literal));
				allProp.add( temp);
				temp = new ArrayList< Object>();
			}
		}

		// get all the object property and check if they can be asserted
		Map<OWLDataPropertyExpression, Set<OWLLiteral>> inf = new HashMap<OWLDataPropertyExpression, Set<OWLLiteral>>();
		synchronized( ontoRef.getReasoner()){
			for ( OWLObjectProperty dataProp : allDataProperty){
				Set<OWLNamedIndividual> a = ontoRef.getReasoner().getObjectPropertyValues( ind, dataProp).getFlattened();
				for ( OWLNamedIndividual value : a){				
					temp.add( ClassExcange.imObjPropIcon);
					temp.add( ClassExcange.getRenderer().render( dataProp));
					temp.add( ClassExcange.getRenderer().render( value));
					if( ! allProp.contains( temp)){
						temp.set( 0, ClassExcange.imObjPropInfIcon);
						allProp.add( temp);
					}
					temp = new ArrayList< Object>();
				}
			}	
		}
		
		// convert for JTable
		Object[][] ret = new Object[ allProp.size()][ getColoumsName().length];
		for( int r = 0; r < allProp.size(); r++){
			//allProp.get(r).add( checkFollowStatus( allProp.get(r)));
			for( int c = 0; c < allProp.get( r).size(); c++){
				ret[ r][ c] = new Object();
				ret[ r][ c] = allProp.get( r).get(c);
			}
		}		
		tableData = allProp;
		return( ret);	
	}
	// boolean inferred, string ClassName, Boolean follow
	private synchronized Object[][] renderClassItem(){
		Set<OWLClass> nonInf;
		Set<OWLClass> classes;
		synchronized( ontoRef.getReasoner()){
			OWLNamedIndividual ind = ontoRef.getFactory().getOWLNamedIndividual( individualname, ontoRef.getPm());
			nonInf = ind.getClassesInSignature();
			classes = ontoRef.getReasoner().getTypes( ind, false).getFlattened();
		}
		
		// get not asserted class
		List< List< Object>> allProp = new ArrayList< List< Object>>();
		List< Object> temp = new ArrayList< Object>();
		for( OWLClass classs : nonInf){
			temp.add( ClassExcange.imClassIcon);
			temp.add( ClassExcange.getRenderer().render( classs));
			allProp.add( temp);
			temp = new ArrayList< Object>();
		}

		// get all the object property and check if they can be asserted
		for ( OWLClass classs : classes){				
			temp.add( ClassExcange.imClassIcon);
			temp.add( ClassExcange.getRenderer().render( classs));
			if( ! allProp.contains( temp)){
				temp.set( 0, ClassExcange.imClassInfIcon);
				allProp.add( temp);
			}
			temp = new ArrayList< Object>();
		}
		
		// convert for JTable
		Object[][] ret = new Object[ allProp.size()][ getColoumsName().length];
		int r = 0;
		for( List<Object> row : allProp){
			//row.add( checkFollowStatus( row));
			int c = 0;
			for( Object elem : row){
				ret[ r][ c] = new Object();
				ret[ r][ c] = elem;
				c++;
			}
			r++;
		}
		tableData = allProp;
		return( ret);
	}
	// boolean inferred, string sameIndividual, Boolean follow
	private synchronized Object[][] renderSameIndividualItem(){
		Set<OWLIndividual> nonInf;
		Node<OWLNamedIndividual> individuals;
		synchronized( ontoRef.getReasoner()){
			OWLNamedIndividual ind = ontoRef.getFactory().getOWLNamedIndividual( individualname, ontoRef.getPm());
			nonInf = ind.getSameIndividuals( ontoRef.getOntology());
			individuals = ontoRef.getReasoner().getSameIndividuals(ind);
		}
		
		// get not asserted same individual
		// boolean inferred, string propName, string value, string type, Boolean follow 
		List< List< Object>> allProp = new ArrayList< List< Object>>();
		List< Object> temp = new ArrayList< Object>();
		for( OWLIndividual sameInd : nonInf){
			temp.add( ClassExcange.imIndividualIcon);
			temp.add( ClassExcange.getRenderer().render( sameInd));
			allProp.add( temp);
			temp = new ArrayList< Object>();
		}

		// get all the object property and check if they can be asserted
		
		for ( OWLNamedIndividual sameInd : individuals){				
			temp.add( ClassExcange.imIndividualIcon);
			temp.add( ClassExcange.getRenderer().render( sameInd));
			if( ! allProp.contains( temp)){
				if( ! temp.get( 1).equals(individualname)){
					temp.set( 0, ClassExcange.imIndividualInfIcon);
					allProp.add( temp);
				}else{
					temp.set( 0, ClassExcange.imIndividualIcon);
					temp.set( 1, temp.get( 1) + ClassExcange.nonSameIndividual);
					allProp.add( temp);
				}
			}
			tableData = allProp;
			temp = new ArrayList< Object>();
		}
		
		// convert for JTable
		Object[][] ret = new Object[ allProp.size()][ getColoumsName().length];
		int r = 0;
		for( List<Object> row : allProp){
			//row.add( checkFollowStatus( row));
			int c = 0;
			for( Object elem : row){
				ret[ r][ c] = new Object();
				ret[ r][ c] = elem;
				c++;
			}
			r++;
		}
		return( ret);
	}
	
	// search for a given row and return the past value of an element
	private synchronized Color checkFollowStatus( List<Object> tableRow){
		
		ImageIcon asserted = null;
		String property = null;
		String value = null;
		String type = null;
		
		try{  // get values
			if( tableRow.size() == 1){
				asserted = (ImageIcon) tableRow.get( 0);
			} if( tableRow.size() >= 2){
				property = (String) tableRow.get( 1);
			} if( tableRow.size() >= 3){
				value = (String) tableRow.get( 2);
			} if( tableRow.size() >= 4){
				type = (String) tableRow.get( 3);	
			}
		} catch( java.lang.ClassCastException e){
			e.printStackTrace();
		}				
		
		try{
			for (int row = 1; row < table.getColumnCount(); row++) {
				int found  = 0;
				int match = 0;
				if( asserted != null){
					match++;
					try{
						if ( asserted.equals( table.getValueAt( row, 0)))
							found++;
					}catch( java.lang.ArrayIndexOutOfBoundsException ed){
						//ed.printStackTrace(); // tree must be initialised	
					}
				}
				if( property != null){
					match++;
					try{
						if ( property.equals( table.getValueAt( row, 1)))
							found++;
					}catch( java.lang.ArrayIndexOutOfBoundsException ed){}
				}
				if( value != null){
					match++;
					try{
						if ( value.equals( table.getValueAt( row, 2)))
							found++;
					}catch( java.lang.ArrayIndexOutOfBoundsException ed){}
				}
				if( type != null){
					match++;
					try{
						if ( type.equals( table.getValueAt( row, 3)))
							found++;
					}catch( java.lang.ArrayIndexOutOfBoundsException ed){}
				}
				
				if( found == match && found != 0){
					return( (Color) table.getValueAt( 
							row, getColoumsName().length - 1));
				}
	        }
		} catch( java.lang.NullPointerException e){
			//e.printStackTrace(); // tree must be initialised
			//return(  new Color( 0, 255, 0));//ClassExcange.getNullcolor());
		} 
		return(  ClassExcange.getNullcolor());
		
	}

	// set the column title name for each type of table
	private synchronized String[] getColoumsName(){
		if( tableType == ClassExcange.dataPropertyTable){
			return( new String[] { 
					ontoRef.getReasoner().getReasonerName(), 
					"Data Property","Value","Type"});//,"Follow"});
		} else if( tableType == ClassExcange.objectPropertyTable){
			return( new String[] {
					ontoRef.getReasoner().getReasonerName(),
					"Object Property","Value"});//, "Follow"});
		} else if( tableType == ClassExcange.classTable){
			return( new String[] {
					ontoRef.getReasoner().getReasonerName(),
					"Class"});//, "Follow"});
		} else if( tableType == ClassExcange.sameIndividualTable){
			return( new String[] {
					ontoRef.getReasoner().getReasonerName(),
					"Same Individual as"});//, "Follow"});
		}
		//System.out.println( " returning null from getColumsName");
		return null;
	}

	public synchronized Integer getTableType(){
		return( tableType);
	}
	
	public synchronized String getIndividualName(){
		return( individualname);
	}
	
	public synchronized JTable getTable() {
		return table;
	}

	public synchronized  void update(){ 
		CN = getColoumsName();
		tableWorker = new TableWorker( this);//excute() by itself
	}
	
	public synchronized void setD( Object[][] d){
		D = d;
	}
	
	public MyTableModel getModel() {
		return model;
	}
	
	// table renderer
	// the last must be "follow"
	// only the last column is editable
    public class MyTableModel extends AbstractTableModel {
        private String[] columnNames =  CN;
        private Object[][] data = D;
        
        public MyTableModel(){
        	update();
        }
        
        public synchronized int getColumnCount() {
            return CN.length; //columnNames.length;
        }

        public synchronized int getRowCount() {
        	if( D != null)
        		return D.length; //data.length;
        	else return 0;
        }

        public synchronized String getColumnName(int col) {
            return CN[ col];//columnNames[col];
        }

        public synchronized Object getValueAt(int row, int col) {
            return D[row][col];//data[row][col];
            
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public synchronized Class getColumnClass(int c) {
        	//return getValueAt(0, c).getClass();
        	if( c == 0)
        		return getValueAt( 0, 0).getClass();
        	return String.class;
        }

        public synchronized boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            //if (col < CN.length - 1) {
                return false; // editable only the last coloumn
            //} else {
            //	return true;
            //}
        }

        public synchronized void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }

            //data[row][col] = renderItem()[row][col];
            //data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }
        
        private synchronized void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    
    }

    
    // UNIMPLEMENTED METHODS
    @Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}

}


class TableWorker extends SwingWorker< Object[][], String> {

	ClassTableIndividual caller;
	Object[][] o;
	boolean running = false;
	
	public TableWorker ( ClassTableIndividual caller){
		this.caller = caller;
		if( ! running)
			this.execute();
	}
	
	@Override
	protected synchronized Object[][] doInBackground() throws Exception {
		running = true;
	    ClassExcange.getProgressBar().setVisible( running);
	    
	    o = caller.renderItem();
	    
	    running = false;
	    ClassExcange.getProgressBar().setVisible( running);
	    return( o);
	}
	
	@Override 
	protected synchronized void done(){
		if( o != null)
			caller.setD(o);
	}
}

class TextWorker extends SwingWorker< JTextArea, String> {

	ClassTableIndividual caller;
	boolean running = false;
	
	public TextWorker( ClassTableIndividual caller){
		this.caller = caller;
		if( ! running)
			this.execute();
	}
	
	@Override
	protected synchronized JTextArea doInBackground() throws Exception {
		running = true;
	    ClassExcange.getProgressBar().setVisible( running);
	    
	    JTextArea a = caller.renderText();
	    
	    running = false;
	    ClassExcange.getProgressBar().setVisible( running);
	    return( a);
	}
}