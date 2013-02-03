/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.chm;

import at.redeye.FrameWork.utilities.FileExtFilter;
import at.redeye.FrameWork.utilities.ReadFile;
import at.redeye.FrameWork.utilities.WorkerThread.WorkInterface;
import at.redeye.klippingtool.ListDataContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class SimpleLookUpCHM implements WorkInterface {

    private static final Logger logger = Logger.getLogger(SimpleLookUpCHM.class.getName());
    private static final String STRINGS_FILE = "#STRINGS";
    String keyword;
    ActionListener listener;    
    String base_search_directory;

    public enum ACTION_ID
    {
        CLEAR,
        FOUND_CHM
    };
    
    public static class ActionCHM extends ActionEvent
    {
        String title;
        
        public ActionCHM( Object source, int id, String command, String title )
        {
            super( source, id, command );
            this.title = title;
        }
        
        public String getTitle()
        {
            return title;
        }
    }
    
    public SimpleLookUpCHM(ListDataContainer cont, ActionListener listener, String base_search_directory ) {
        keyword = cont.getClipData().toLowerCase();
        this.listener = listener;
        this.base_search_directory = base_search_directory;
    }

    
    @Override
    public void work() {
       
        File file = new File(base_search_directory);
        
        if( !file.exists() )
        {
            logger.error( base_search_directory + " does not exist" );
            return;                        
        }

        if( !file.isDirectory() )
        {
            logger.error( base_search_directory + " is not a directory" );
            return;                        
        }
        
        
        File subdirs[] = file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if( pathname.isDirectory() ) {
                    return true;
                }
                
                return false;
            }
        });
        
        if( subdirs == null ) {
            logger.error("no subdirs found");
            return;
        }
        
        
        for( File subdir : subdirs )
        {
            File strings_files = new File(subdir.getAbsolutePath() + "/" + STRINGS_FILE );
            if( strings_files.exists() && strings_files.canRead() ) {
                String keys = ReadFile.read_file(strings_files.getPath());
                
                if( keys == null )
                    continue;
                
                keys = keys.toLowerCase();
                
                if( keys.contains(keyword) )
                {
                    logger.debug( "found " + keyword + " in " + subdir.getName() );
                    
                    File index_files[] = subdir.listFiles((FilenameFilter)new FileExtFilter("*.hhc; *.hhk"));
                    
                    if( index_files != null )
                    {
                        for( File index_file : index_files )
                        {
                            // parse index
                            String content = ReadFile.read_file(index_file.getPath());
                            
                            lookup_keyword( content, subdir );
                        }
                    }
                }
            }
        }
    }
    
    private void lookup_keyword( String html_index_content, final File subdir )
    {
        boolean is_first_found = true;
        Source source = new Source(html_index_content);
        source.fullSequentialParse();
        int page_count = 0;
        
        List<Element> objects = source.getAllElements("object");
        
        for( Element ele : objects )        
        {
            List<Element> params = ele.getAllElements("param");                        
            
            boolean found_in_name = false;
            String subsection_name = null;
            
            for( Element param : params )
            {
                String name =  param.getAttributeValue("name");
                if( name == null )
                    continue;
                
                final String value = param.getAttributeValue("value");
                if( value == null )
                    continue;
                
                if( name.equalsIgnoreCase("name") )
                {                                        
                    if( value.toLowerCase().contains(keyword) )
                    {
                        found_in_name = true;
                        subsection_name = value;
                    }
                } else if( name.equalsIgnoreCase("local") && found_in_name ) {
                    
                    final Object parent = this;                                       
                    
                    if( is_first_found )
                    {                       
                        is_first_found = false;                                                
                        
                        if( listener != null ) {
                            java.awt.EventQueue.invokeLater(new Runnable(){

                                @Override
                                public void run() {
                                    listener.actionPerformed(new ActionCHM(parent,ACTION_ID.CLEAR.ordinal(),null,null));                                    
                                }
                                
                            });                            
                        }
                    } 
                    
                    if (listener != null) {

                        page_count++;

                        final String title = subsection_name;

                        java.awt.EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {

                                final String url = "file:///" + subdir.getAbsolutePath() + "/" + value;

                                listener.actionPerformed(new ActionCHM(parent, ACTION_ID.FOUND_CHM.ordinal(),
                                        url, title));
                            }
                        });
                    }

                    
                    break;
                }
            }
            
            if( page_count > 10 )
                break;
        }        
    }

    @Override
    public void workDone() {
    }

    @Override
    public void pleaseStopWorking() {
    }
}
