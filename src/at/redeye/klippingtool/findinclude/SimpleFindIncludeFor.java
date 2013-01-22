/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.findinclude;

import at.redeye.FrameWork.utilities.ReadFile;
import at.redeye.FrameWork.utilities.WorkerThread.WorkInterface;
import at.redeye.klippingtool.ListDataContainer;
import at.redeye.klippingtool.lib.FileExtFilter;
import at.redeye.klippingtool.lib.FileFoundInterface;
import at.redeye.klippingtool.lib.SearchForFiles;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;


/**
 *
 * @author martin
 */
public class SimpleFindIncludeFor implements WorkInterface, FileFoundInterface
{
    private static final Logger logger = Logger.getLogger(SimpleFindIncludeFor.class.getName());
    ListDataContainer cont;
    String source_dir;   
    
    public SimpleFindIncludeFor( ListDataContainer cont, String source_dir )
    {
        this.cont = cont;
        this.source_dir = source_dir;        
    }

    @Override
    public void work() {
        SearchForFiles searcher = new SearchForFiles( new FileExtFilter("*.h *.hh *.hpp"), this );
        try {
            searcher.findFiles(new File(source_dir));
        } catch (FileNotFoundException ex) {
            logger.error(ex,ex);
        }
    }

    @Override
    public void workDone() {
        
    }

    @Override
    public void pleaseStopWorking() {
        
    }
    
    private String getWholeLine( String data, int idx )
    {
        int ende = data.indexOf('\n', idx);
        
        int count = 0;
        int i;
        
        for( i = idx - 1; i > 0 ; i--, count++ )
        {
            if( count > 100 )
                break;
            
            if( data.charAt(i) == '\n' )
            {
                i++;
                break;
            }
        }
        
        return data.substring(i, ende);
    }    
    
    private String getPartOfCode( String data, int idx )
    {
        int ende = data.indexOf('\n', idx);
        
        int count = 0;
        int begin = idx - 100;
        if( begin < 0 )
            begin = 0;
        
        return data.substring(begin, ende);
    }

    @Override
    public boolean fileFound(File file) {
        
        String content = ReadFile.read_file(file.getPath());
        
        if( content != null ) {
            
            int idx = 0;
            while (idx >= 0) {
                idx = content.indexOf(cont.getClipData(), idx);
                if (idx >= 0) {
                    
                    String line = getWholeLine(content, idx);
                    
                    if( line.startsWith("*") ) {
                        idx += cont.getClipData().length();
                        continue;
                    }
                    
                    logger.debug("found '" + cont.toString() + "' in " + file.getPath());
                    cont.addIncludeString(String.format("#include \"%s\"\n", file.getName()),
                            getPartOfCode(content, idx));
                    break;
                }
            }
        }
        
        return true;
    }

    @Override
    public boolean diveIntoSubDir(File file) {
        if( file.getName().equals(".svn") )
            return false;        
        
        else if( file.getName().equals("CVS") )
            return false;
        
        return true;
    }
    
}
