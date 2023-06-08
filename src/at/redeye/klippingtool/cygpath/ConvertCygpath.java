/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.cygpath;

import at.redeye.klippingtool.*;

/**
 *
 * @author martin
 */
public class ConvertCygpath extends BaseLookup
{    
    public ConvertCygpath(MainWin mainwin )
    {
       super(mainwin, null);
    }
    
    public void convertToUnix( ListDataContainer cont, StatusInformation statusinfo  )
    {                    
       if( cont.getClipData().trim().length() > 2 ) {
            addWorker(new SimpleConverter(cont, statusinfo ));
       }
    }

    @Override
    public void lookUp(ListDataContainer cont) {           
        convertToUnix(cont, getMainWin());        
    }
          
}
