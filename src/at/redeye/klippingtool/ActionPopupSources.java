/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool;

import at.redeye.FrameWork.base.Root;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ActionPopupSources extends JPopupMenu
{
    private static final Logger logger = Logger.getLogger(ActionPopupSources.class.getName());
    public static final String SOURCES_LIST = "SourcesList";

    Root root;
    MainWin  mainwin;

    public ActionPopupSources(final MainWin mainwin, final String cont) {
        this.root = mainwin.getRoot();
        this.mainwin = mainwin;

        
        for( BaseLookup worker : mainwin.getFindWorker() )
        {
            worker.addPopupMenutItemTo(this, SOURCES_LIST);
        }


        if (cont != null) {
            JMenuItem menuItem = new JMenuItem(mainwin.MESSAGE_REMOVE_ENTRY);

            add(menuItem);

            menuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    for (BaseLookup worker : mainwin.getFindWorker()) {
                        worker.removedEntry(cont, SOURCES_LIST);
                    }
                    
                    mainwin.removeSourcesDirectory(cont);
                }
            });
        }
    }

}
