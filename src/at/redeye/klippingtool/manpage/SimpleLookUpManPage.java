/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.manpage;

import at.redeye.FrameWork.utilities.DownloadUrl;
import at.redeye.FrameWork.utilities.WorkerThread.WorkInterface;
import at.redeye.klippingtool.ListDataContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class SimpleLookUpManPage implements WorkInterface {

    private static final Logger logger = Logger.getLogger(SimpleLookUpManPage.class.getName());
    private static final String BASE_URL = "http://salxintsw.salomon.at/cgi-bin/man.cgi";
    private static String sections[] = {"1", "2", "3", "4", "5", "6", "all"};
    String keyword;
    ActionListener listener;                
    
    private static final String EMPTY_PAGE_STRING = 
            "<HR>\n"
            + "<PRE>\n"
            + "<!-- Manpage converted by man2html 3.0.1 -->\n"
            + "</PRE>\n"
            + "<HR>\n"
            + "<ADDRESS>";

    public enum ACTION_ID
    {
        CLEAR,
        FOUND_MANPAGE
    };
    
    public static class ActionManPage extends ActionEvent
    {
        String title;
        
        public ActionManPage( Object source, int id, String command, String title )
        {
            super( source, id, command );
            this.title = title;
        }
        
        public String getTitle()
        {
            return title;
        }
    }
    
    public SimpleLookUpManPage(ListDataContainer cont, ActionListener listener) {
        keyword = cont.getClipData();
        this.listener = listener;
    }
    
    private boolean haveContent( String data )
    {
        if( data == null )
            return false;
        
        if( data.isEmpty() )
            return false;
        
        if( data.contains(EMPTY_PAGE_STRING) )
            return false;        
        
        if( data.contains("Manpage Viewer Error") )
            return false;
        
        return true;
    }
    
    @Override
    public void work() {
        URL url;
               
        
        try {
            final SimpleLookUpManPage parent = this;            

            int count = 0;
            
            for (String section : sections) {
                url = new URL(BASE_URL + "?section=" + section + "&topic=" + URLEncoder.encode(keyword, "ISO-8859-1"));
                                
                
                DownloadUrl downloadUrl = new DownloadUrl(url);
                                
                StringBuffer sb = new StringBuffer();
                downloadUrl.download(sb, "ISO-8859-1");
                
                final String download_data =  sb.toString();
                
                if (download_data != null) {
                    
                    if( !haveContent(download_data) ) {
                        continue;
                    }
                    
                    logger.debug(download_data);
                    
                    if (count == 0) {
                        if (listener != null) {
                            java.awt.EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {

                                    listener.actionPerformed(new ActionManPage(parent, ACTION_ID.CLEAR.ordinal(), "", null));

                                }
                            });
                        }
                    }
                    
                    count++;
                    
                    if (listener != null) {
                        
                        final String title = keyword + " (" + section + ")";
                        
                        java.awt.EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {

                                listener.actionPerformed(new ActionManPage(parent, ACTION_ID.FOUND_MANPAGE.ordinal(), download_data, title));

                            }
                        });
                    }                 
                    
                }
            }            

        } catch (UnsupportedEncodingException | MalformedURLException ex) {
            logger.error(ex, ex);
        }
    }

    @Override
    public void workDone() {
    }

    @Override
    public void pleaseStopWorking() {
    }
}
