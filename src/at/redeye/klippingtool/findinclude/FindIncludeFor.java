/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.findinclude;

import at.redeye.FrameWork.utilities.WorkerThread.WorkerThread;
import at.redeye.klippingtool.BaseLookup;
import at.redeye.klippingtool.ListDataContainer;
import at.redeye.klippingtool.StatusInformation;
import java.io.File;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class FindIncludeFor extends BaseLookup
{
    WorkerThread worker_thread;        
    
    public FindIncludeFor()
    {
       super();
    }
    
    public void findIncludeFor( ListDataContainer cont, Vector<String> listSources, StatusInformation statusinfo )
    {
        if( listSources == null || cont == null )
            return;
                
        for( String source_dir : listSources ) {
            File dir = new File( source_dir );
            
            if( dir.exists() && dir.isDirectory() && cont.getClipData().trim().length() > 2 ) {
                addWorker(new SimpleFindIncludeFor(cont, source_dir, statusinfo ));
            }
        }
    }       
}
