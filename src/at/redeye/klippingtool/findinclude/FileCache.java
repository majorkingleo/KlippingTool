/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool.findinclude;

import at.redeye.FrameWork.utilities.ReadFile;
import java.io.File;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class FileCache 
{
    private static final Logger logger = Logger.getLogger(FileCache.class);
    
    static class CachedFile
    {
        private final File file;
        private final String content;
        
        public CachedFile( File file, String content )
        {
            this.file = file;
            this.content = content;
        }
        
        public String getContent()
        {
            return content;
        }
        
        public File getFile()
        {
            return file;
        }
    }
    
    final HashMap<String,CachedFile> file_map = new HashMap();
    // int cache_hit = 0;
    // int cache_miss = 0;
    
    public FileCache()
    {
          
    }
    
    public String readFile( File file )
    {
        CachedFile cf = null;
        
        synchronized (file_map) {
           cf = file_map.get(file.getPath());
        }        
        
        if (cf == null || cf.getFile().lastModified() != file.lastModified() ) {
            cf = new CachedFile(file, ReadFile.read_file(file.getPath()));

            synchronized (file_map) {
                file_map.put(file.getPath(), cf);
            }

            // cache_miss++;
            
            return cf.getContent();
        }
        
        // cache_hit ++;
        
        // logger.debug( "cache_hit: " + cache_hit + " misses: " + cache_miss );
        
        return cf.getContent();
    }
}
