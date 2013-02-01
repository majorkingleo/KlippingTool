/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.manpage;

import at.redeye.FrameWork.utilities.WorkerThread.WorkerThread;
import at.redeye.klippingtool.BaseLookup;
import at.redeye.klippingtool.ListDataContainer;
import at.redeye.klippingtool.StatusInformation;
import java.awt.event.ActionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class FindManPageFor extends BaseLookup {
    
    public FindManPageFor()
    {
        super();
    }
    
    public void findManPageFor( ListDataContainer cont, StatusInformation statusinfo , ActionListener listener )
    {                
        if( cont == null )
            return;                        
        
        logger.debug("searching for " + cont.getClipData());
        
        addWorker( new SimpleLookUpManPage(cont, listener ));
    }       
}
