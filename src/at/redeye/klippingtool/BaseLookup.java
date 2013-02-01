/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.WorkerThread.WorkInterface;
import at.redeye.FrameWork.utilities.WorkerThread.WorkerThread;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class BaseLookup {
    public static final Logger logger = Logger.getLogger(BaseLookup.class.getName());
    
    WorkerThread worker_thread;        
    Root root;
    
    public BaseLookup(Root root)
    {
       this.root = root;
       createWorker();       
    }
    
    public BaseLookup()
    {
       createWorker();       
    }    
        
    private void createWorker()
    {
        worker_thread  = new WorkerThread(at.redeye.klippingtool.chm.FindCHMFor.class.getName());
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
    
    
    public void addWorker( WorkInterface worker_job )
    {
        worker_thread.add(worker_job);
    }
}   