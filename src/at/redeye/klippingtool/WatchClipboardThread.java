/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

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
    
    @Override
    public void run()
    {
        final Clipboard systemClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        do
        {
            final Transferable transfer = systemClip.getContents( null );
            try {
                final String data = (String) transfer.getTransferData( DataFlavor.stringFlavor );
                
                if( data != null ) 
                {
                    if( lastClippingText == null )
                    {
                        lastClippingText = data;
                        logger.debug("clipboard changed: " + data);
                        onClipboardChanged.actionPerformed(new ActionEvent("clipboard",0,data));
                        
                    } else if( !lastClippingText.equals(data) ) {
                        
                        lastClippingText = data;
                        
                        logger.debug("clipboard changed: " + data);
                        onClipboardChanged.actionPerformed(new ActionEvent("clipboard",0,data));
                    }
                }
                
            } catch (    UnsupportedFlavorException | IOException ex) {
                logger.error(ex,ex);
            }            
            
            try {
                sleep(500);
            } catch( InterruptedException ex ) {
                
            }
        } while( true );
    }
    
}
