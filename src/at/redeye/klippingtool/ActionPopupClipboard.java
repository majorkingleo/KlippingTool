/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool;

import at.redeye.FrameWork.base.Root;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ActionPopupClipboard extends JPopupMenu
{
    private static final Logger logger = Logger.getLogger(ActionPopupClipboard.class.getName());

    static class ActionListenerPaste implements ActionListener
    {
        String data;      
        MainWin mainwin;
        
        ActionListenerPaste( MainWin mainwin, String data )
        {
            this.data = data;            
            this.mainwin = mainwin;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
             mainwin.paste( data );
        }
    }
    
    Root root;
    MainWin  mainwin;

    private String buildHTMLTooltip( String keyword, String tooltip )
    {
        tooltip = tooltip.replace(keyword, "<b color=\"red\">" + keyword + "</b>");
        return "<html><body>" + tooltip.replaceAll("\n", "<br/>") + "</body></html>";
    }
    
    public ActionPopupClipboard(final MainWin mainwin, final ListDataContainer cont) {
        this.root = mainwin.getRoot();
        this.mainwin = mainwin;


        if (cont.getIncludes() != null) 
        {
            ArrayList<String> includes = cont.getIncludes();
            for ( int i = 0; i < includes.size() && i < 50; i++ ) {
                String include = includes.get(i);
                
                JMenuItem menuItem = new JMenuItem(include);

                String tooltip = cont.getIncludeLine(i);
                
                if( !tooltip.trim().isEmpty() ) {
                   menuItem.setToolTipText(buildHTMLTooltip(cont.getClipData(), tooltip));
                }
                
                add(menuItem);

                menuItem.addActionListener( new ActionListenerPaste(mainwin, include));
            }
        }
    }

}
