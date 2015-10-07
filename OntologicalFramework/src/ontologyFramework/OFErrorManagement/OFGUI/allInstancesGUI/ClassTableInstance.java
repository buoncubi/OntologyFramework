
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

package ontologyFramework.OFErrorManagement.OFGUI.allInstancesGUI;

/*
 * TableDialogEditDemo.java requires these files:
 *   ColorRenderer.java
 *   ColorEditor.java
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.AbstractTableModel;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@SuppressWarnings("serial")
public class ClassTableInstance extends JPanel implements MouseListener {
    
    private static JTable table;
    private JFrame frame;
    private JScrollPane scrollPane;
    
    private static int selected;
    
    private static MyTableModel model;

	private static final String[] columnNames = new String[] {	"Class", "Instances", "Log"};

    
	// constructor
	public ClassTableInstance( JFrame jframe) {
		super( new BorderLayout(0, 0));
				
        this.frame = jframe;
    	
    	model = new MyTableModel();
        table = new JTable( model);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
                
        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(table);
        
        //Enable tool tips.
        ToolTipManager.sharedInstance().registerComponent( table);
        
        // set up colour manager
        table.setDefaultRenderer( String.class,  new ColorRenderer(true));
               
        //Listen for when the selection changes.
        table.addMouseListener( this);
                
        // Custom cell weight
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
             
        // frame resize listener
        frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				double a = e.getComponent().getSize().getWidth() - scrollPane.getVerticalScrollBar().getSize().getWidth();
				setTableDimensions(( a - 15) * 0.3);
			}
		});

        //Add the scroll pane to this panel.
        add( scrollPane, BorderLayout.CENTER);
        
    }
		
	// set the preferred dimensions of every cell in accord with the type of table
	// and the frame dimension
	public static void setTableDimensions( double tableWidth){
		table.getColumnModel().getColumn( 0)
			.setPreferredWidth( ( int) (0.40 * tableWidth)); // Class
		table.getColumnModel().getColumn( 1)
			.setPreferredWidth( ( int) (0.40 * tableWidth)); // instances
		table.getColumnModel().getColumn( 2)
			.setPreferredWidth( ( int) (0.20 * tableWidth)); // follow
	}
	
	public static MyTableModel getModel() {
		return model;
	}
	
	// call the ontology printer and update the textPane
	@Override
	public synchronized void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		int selectionRow = table.getSelectedRow();
		int selectionCol = table.getSelectedColumn();
		if( selectionRow != -1 && selectionCol == 2){
			OFDebugLogger tmp = OFDebugLogger.getAllInstances().get( table.getValueAt( selectionRow, 1));
			Boolean boo = (Boolean) table.getValueAt( selectionRow, selectionCol);
			table.setValueAt( !boo, selectionRow, selectionCol);
			tmp.setFlagToFollow( !boo);//.setFlagToFollow( !boo, this);
			update();
			model.fireTableDataChanged();
		}
		table.repaint();
	}
	  
	public static void saveSelection(){
		selected = table.getSelectedRow();
	}
	
	public static void restoreSelection(){
		if( selected < table.getRowCount() && selected >= 0)
			table.setRowSelectionInterval( selected, selected);
	}
	
	public JTable getTable() {
		return table;
	}
	
	private static String[] CN ;
	private static Object[][] D;
	public synchronized static void update(){ 
		CN = columnNames;
		D = OFDebugLogger.getTableInfo(); 
	}
	
	// table renderer
	// the last must be "follow"
	// only the last column is editable
    public class MyTableModel extends AbstractTableModel {
        //private String[] columnNames =  CN;
        //private Object[][] data = D;
        
        public MyTableModel(){
        	update( );
        }
        
        public int getColumnCount() {
            return CN.length; //columnNames.length;
        }

        public int getRowCount() {
			return D.length; //data.length;
        }

        public String getColumnName(int col) {
            return CN[ col];//columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return D[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
		public Class<? extends Object> getColumnClass(int c) {
        	return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return true; 
        }

        public void setValueAt(Object value, int row, int col) {
            
            fireTableCellUpdated(row, col);
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