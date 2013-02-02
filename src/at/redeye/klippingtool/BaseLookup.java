/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.WorkerThread.WorkInterface;
import at.redeye.FrameWork.utilities.WorkerThread.WorkerThread;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public abstract class BaseLookup {
    public static final Logger logger = Logger.getLogger(BaseLookup.class.getName());
    
    WorkerThread worker_thread;        
    Root root;
    MainWin mainwin;
    JPanel panel;
    
    public BaseLookup(MainWin mainwin, JPanel panel )
    {
       this.root = mainwin.getRoot();
       this.panel = panel;
       this.mainwin = mainwin;
       createWorker();       
    }
        
    
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
    
    /**
     * called from ugi thread to render the help page
     * @param cont
     * @param source_dir
     * @param mainwin 
     */
    public abstract void lookUp( ListDataContainer cont );

    public JPanel getPanel() {
        return panel;
    }
    
    public MainWin getMainWin() {
        return mainwin;
    }
    
    public Root getRoot() {
        return root;
    }
    
    /**
     * Method called for each worker, to append some context menus to 
     * ActionPopupes
     * @param popup  The Action Popup
     * @param command Identifier for the Pupup menu wille by ActionPopupSources.SOURCES_LIST for the Sources popup
     */
    public void addPopupMenutItemTo( JPopupMenu popup, String command )
    {
        
    }        
    
    /**
     * called when an entry from a list is removed. Eg in the sources list.
     * @param cont  The entry
     * @param data_source Identifier for the Pupup menu wille by ActionPopupSources.SOURCES_LIST for the Sources popup
     */
    public void removedEntry( String cont, String data_source )
    {
        
    }
    
}   