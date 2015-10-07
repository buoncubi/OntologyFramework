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


/* 
 * ColorRenderer.java (compiles with releases 1.2, 1.3, and 1.4) is used by 
 * TableDialogEditDemo.java.
 */

package ontologyFramework.OFErrorManagement.OFGUI.allInstancesGUI;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import ontologyFramework.OFErrorManagement.OFGUI.ClassExcange;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

public class ColorRenderer extends JLabel
							implements TableCellRenderer {
    
	private static final long serialVersionUID = 1L;
	Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;
    
        
    Map<String, Color> all;

    public ColorRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.        
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object text,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
    
    	if( text instanceof String){
			String str = (String) text;
			setText( str);
			str = str.replace( ClassExcange.nonSameIndividual, "");
			if( ClassExcange.isColorMatchSearch()){
				// match all the string
				if( ClassExcange.getAllColorToFollow().keySet().contains( str)){
					setForeground( ClassExcange.getAllColorToFollow().get( str));
				}else{
					setForeground(ClassExcange.getNullcolor());
				}		
			} else { 
				// search for string which contains
				for( String stri : ClassExcange.getAllColorToFollow().keySet()){
					String st = stri.toLowerCase();
					String s = str.toLowerCase();
					if( s.contains( st)){
						setForeground( ClassExcange.getAllColorToFollow().get( stri));
						break;
					} else
						setForeground(ClassExcange.getNullcolor());
				}
			}
    	}
    	
    	// change colour of what are you going to choose
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		if (isSelected) {
            setBackground( defaults.getColor("List.selectionBackground"));
        } else {
            setBackground( table.getBackground());
        }
    	
	    return this;
    }

    
 }
