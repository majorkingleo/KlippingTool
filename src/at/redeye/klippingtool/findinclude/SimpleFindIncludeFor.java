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

    @Override
    public boolean fileFound(File file) {
        
        String content = ReadFile.read_file(file.getPath());
        
        if( content != null ) {
            if( content.indexOf(cont.getClipData() ) >= 0 ) {
                logger.debug("found '" + cont.toString() + "' in "  + file.getPath());
                cont.addIncludeString(String.format("#include \"%s\"\n", file.getName()) );
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
