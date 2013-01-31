/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.manpage;

import at.redeye.FrameWork.utilities.WorkerThread.WorkerThread;
import at.redeye.klippingtool.ListDataContainer;
import at.redeye.klippingtool.StatusInformation;
import java.awt.event.ActionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class FindManPageFor {
    private static final Logger logger = Logger.getLogger(FindManPageFor.class.getName());
    
    WorkerThread worker_thread;        
    
    public FindManPageFor()
    {
       createWorker();
    }
    
    public void findManPageFor( ListDataContainer cont, StatusInformation statusinfo , ActionListener listener )
    {                
        if( cont == null )
            return;                        
        
        logger.debug("searching for " + cont.getClipData());
        
        worker_thread.add( new SimpleLookUpManPage(cont, listener ));
    }
    
    private void createWorker()
    {
        worker_thread  = new WorkerThread(FindManPageFor.class.getName());
        worker_thread.setDaemon(true);
        worker_thread.start();        
    }
    
    public boolean isIdle() {
        
        if( !worker_thread.isAlive() )
        {
            createWorker();
        }
                
        worker_thread.callFinishedWork();
        return worker_thread.isIdle();
    }
    
}
