/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.chm;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
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
public class FindCHMFor extends BaseLookup {
    String base_search_directory;
    
    public FindCHMFor(MainWin mainwin, JPanel panel )
    {
       super(mainwin, panel);
       
       base_search_directory = Setup.getAppConfigFile(mainwin.root.getAppName(), "chm");   
    }
    
    
    public void findManPageFor( ListDataContainer cont, StatusInformation statusinfo , ActionListener listener )
    {                
        if( cont == null )
            return;
        
        logger.debug("searching for " + cont.getClipData());
        
        addWorker( new SimpleLookUpCHM(cont, listener, base_search_directory ));
    }

    @Override
    public void lookUp(ListDataContainer cont ) 
    {                       
            findManPageFor(cont, getMainWin(), new CHMActionListener(cont, getPanel() ));
    }
}
