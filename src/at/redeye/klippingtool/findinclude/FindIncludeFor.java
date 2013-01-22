/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.findinclude;

import at.redeye.FrameWork.utilities.WorkerThread.WorkerThread;
import at.redeye.klippingtool.ListDataContainer;
import java.io.File;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class FindIncludeFor 
{
    WorkerThread worker_thread;        
    
    public FindIncludeFor()
    {
       createWorker();
    }
    
    public void findIncludeFor( ListDataContainer cont, Vector<String> listSources )
    {
        if( listSources == null || cont == null )
            return;
                
        for( String source_dir : listSources ) {
            File dir = new File( source_dir );
            
            if( dir.exists() && dir.isDirectory() && cont.getClipData().trim().length() > 2 ) {
                worker_thread.add(new SimpleFindIncludeFor(cont, source_dir));
            }
        }
    }
    
    private void createWorker()
    {
        worker_thread  = new WorkerThread(FindIncludeFor.class.getName());
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
