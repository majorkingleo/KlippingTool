/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool.nicehtmlist;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author martin
 */
public class ComponentCellRenderer implements ListCellRenderer {
        
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = (Component) value;                
        
        if( isSelected )
        {
            component.setBackground(list.getSelectionBackground());
            component.setForeground(list.getSelectionForeground());
        } else {
            component.setBackground(SystemColor.text);
            component.setForeground(list.getForeground());
        }        

        return component;
    }
}
