/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.chm;

import at.redeye.FrameWork.utilities.FileExtFilter;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author martin
 */
public class CHMFileFilter extends FileFilter {

    FileExtFilter filter;
    
    public CHMFileFilter()
    {
        filter = new FileExtFilter("*.chm");
    }        
    
    @Override
    public boolean accept(File f) {
        if( f.isDirectory() )
            return true;
        
        return filter.accept(f);
    }

    @Override
    public String getDescription() {
        return "*.chm";
    }
    
}
