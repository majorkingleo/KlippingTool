/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.chm;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.DeleteDir;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ExtractCHM {
    
    private static Logger logger = Logger.getLogger(ExtractCHM.class);
    
    String base_search_directory;
    Root root;
    
    public ExtractCHM( Root root, String base_search_directory )
    {
        this.root = root;
        this.base_search_directory = base_search_directory;                
    }
    
    private void checkBaseDir()
    {
        File fbasedir = new File( base_search_directory );
        if( !fbasedir.exists() )
            fbasedir.mkdirs();
    }
    
    private String getChmDirName( File chmFile )
    {
        String file_name_without_extension = chmFile.getName();
        
        int idx = file_name_without_extension.lastIndexOf('.');
        if( idx > 0 )
            file_name_without_extension = file_name_without_extension.substring(0,idx);
        
         return base_search_directory + File.separator + file_name_without_extension;
    }
    
    public boolean extractCHM( File chmFile ) throws IOException
    {        
        checkBaseDir();
        
        String command_array[] = new String[5];
        
        command_array[0] = "7z";
        command_array[1] = "-y";
        command_array[2] = "-o" + getChmDirName(chmFile);
        command_array[3] = "x";
        command_array[4] = chmFile.getPath();             
        
        if( logger.isDebugEnabled() )
        {
            StringBuilder sb = new StringBuilder();
            for( String c : command_array ) {
                sb.append(c).append(" ");
            }
            
            logger.debug(sb.toString());
        }
        
        Process p = Runtime.getRuntime().exec(command_array);
       
        try {
            int exit_state = p.waitFor();
            
            logger.debug( "exist value: " + exit_state);
            
            if( exit_state == 0 )
                return true;
            
        } catch( InterruptedException ex ) {
            logger.error(ex,ex);
            return false;
        }
        
        return false;
    }

    void deleteCHMdirectory(String sources_list_entry) 
    {
        if( !sources_list_entry.toLowerCase().endsWith(".chm") )
            return;
        
        String dirname = getChmDirName(new File(sources_list_entry));
        
        File fdir = new File( dirname );
        
        if( fdir.exists() ) {
           logger.debug("deleting directory " + dirname);
           DeleteDir.deleteDirectory(fdir); 
        }
    }
    
}
