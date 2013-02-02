/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.manpage;

import at.redeye.klippingtool.BaseLookup;
import at.redeye.klippingtool.ListDataContainer;
import at.redeye.klippingtool.MainWin;
import at.redeye.klippingtool.StatusInformation;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

/**
 *
 * @author martin
 */
public class FindManPageFor extends BaseLookup {
    
    public FindManPageFor(MainWin mainwin, JPanel panel )
    {
        super(mainwin, panel);
    }
    
    public void findManPageFor( ListDataContainer cont, StatusInformation statusinfo , ActionListener listener )
    {                
        if( cont == null )
            return;                        
        
        logger.debug("searching for " + cont.getClipData());
        
        addWorker( new SimpleLookUpManPage(cont, listener ));
    }

    @Override
    public void lookUp(ListDataContainer cont) {
        
        findManPageFor(cont, getMainWin(), new ManPageActionListener(cont,  getPanel()));

    }
}
