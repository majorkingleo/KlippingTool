/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.chm;

import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.klippingtool.manpage.*;
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
public class FindCHMFor extends BaseLookup {
    String base_search_directory;
    
    public FindCHMFor(Root root)
    {
       super(root);
       
       base_search_directory = Setup.getAppConfigFile(root.getAppName(), "chm");
    }
    
    public void findManPageFor( ListDataContainer cont, StatusInformation statusinfo , ActionListener listener )
    {                
        if( cont == null )
            return;
        
        logger.debug("searching for " + cont.getClipData());
        
        addWorker( new SimpleLookUpCHM(cont, listener, base_search_directory ));
    }           
}
