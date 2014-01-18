/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.utilities.StringUtils;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class WatchClipboardThread extends Thread
{
    private static Logger logger = Logger.getLogger(WatchClipboardThread.class);
    String lastClippingText;
    String lastClippingSelText;
    ActionListener onClipboardChanged;    
    
    public WatchClipboardThread( ActionListener onClipboardChanged ) 
    {
        super( "WatchClipboardThread");        
        
        this.onClipboardChanged = onClipboardChanged;
    }
    
    void setLastClippingText( String text )
    {
        lastClippingText = text;
    }
    
    long logMem( String title )
    {
        return logMem( title, -1 );
    }
    
    long logMem( String title, long mem_before )
    {
        Runtime rt = Runtime.getRuntime();
        long mem = rt.totalMemory() - rt.freeMemory();
        
        if( mem_before > 0 )
            System.out.println(title + ": " + mem + " + " +  (mem - mem_before));
        else
            System.out.println(title + ": " + mem);
        
        return mem;
    }
    
    @Override
    public void run()
    {
        final Clipboard systemClip = Toolkit.getDefaultToolkit().getSystemClipboard();   
        final Clipboard selClip  = Toolkit.getDefaultToolkit().getSystemSelection();
        
        // long mem = -1;
        
        do
        {
            // mem = logMem("before transfer", mem );
            
            final Transferable transfer = systemClip.getContents( null );                        
            
            // mem = logMem("after transfer", mem );
            
            try {
                String data = null;
                
                try {
                    data = (String) transfer.getTransferData(DataFlavor.stringFlavor);

                    // mem = logMem("after getTransferData", mem );
                    
                } catch (UnsupportedFlavorException ex) {
                    logger.error(ex, ex);

                    if (ex.toString().contains("Unicode String")) {
                        data =  (String)  transfer.getTransferData(DataFlavor.getTextPlainUnicodeFlavor());
                    } else {
                        throw ex;
                    }
                }

                if (data != null && !data.trim().isEmpty()) {
                    if (lastClippingText == null) {
                        lastClippingText = data;
                        logger.debug("clipboard changed: " + data);
                        onClipboardChanged.actionPerformed(new ActionEvent("clipboard", 0, data));

                    } else if (!lastClippingText.equals(data)) {

                        lastClippingText = data;

                        logger.debug("clipboard changed: " + data);
                        onClipboardChanged.actionPerformed(new ActionEvent("clipboard", 0, data));
                    }
                }

            } catch (UnsupportedFlavorException | IOException ex) {
                logger.error(ex, ex);

            }    
                                
            if (selClip != null) {
                try {
                    final Transferable transferSel = selClip.getContents(null);
                    final String data = (String) transferSel.getTransferData(DataFlavor.stringFlavor);                    

                    if (data != null) {
                        if (lastClippingSelText == null) {
                            lastClippingSelText = data;
                            logger.debug("clipboard changed: " + data);
                            onClipboardChanged.actionPerformed(new ActionEvent("clipboard", 0, data));

                        } else if (!lastClippingSelText.equals(data)) {

                            lastClippingSelText = data;

                            logger.debug("sel clipboard changed: " + data);
                            onClipboardChanged.actionPerformed(new ActionEvent("selclipboard", 0, data));
                        }
                    }


                } catch (UnsupportedFlavorException | IOException ex) {
                    logger.error(ex, ex);
                }
            }
                
            try {
                sleep(500);
            } catch( InterruptedException ex ) {
                
            }
        } while( true );
    }
    
}
