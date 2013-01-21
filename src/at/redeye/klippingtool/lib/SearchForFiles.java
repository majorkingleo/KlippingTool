/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool.lib;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 *
 * @author moberza
 */
public class SearchForFiles
{
    public static class DirFilter implements FileFilter
    {
        @Override
        final public boolean accept(final File pathname) {
           return pathname.isDirectory();
        }        
    }
    
    private static final DirFilter directory_filter = new DirFilter();
    final FilenameFilter filter;
    final FileFoundInterface file_found;

    public SearchForFiles( final FilenameFilter filter, final FileFoundInterface file_found )
    {
        this.filter = filter;
        this.file_found = file_found;        
    }

    public boolean findFiles(final File dir) throws FileNotFoundException
    {
       if( !dir.exists() ) {
           throw new FileNotFoundException( 
                  String.format("Directory '%s' does not exists",dir.toString()) );
        }    
       
       return findFilesImpl(dir);
    }
    
    private boolean findFilesImpl(final File dir) throws FileNotFoundException
    {               
       final File files[] = dir.listFiles();               
               
        for (File file : files) {
            
            if( directory_filter.accept(file) ) {
                try {
                    // do not follow symlinks                    
                    if (file.getAbsolutePath().equals(file.getCanonicalPath())) {
                        if (file_found.diveIntoSubDir(file)) {
                            if (!findFilesImpl(file)) {
                                return false;
                            }
                        }
                    }
                } catch (IOException ex) {
                }
            } 
            else if( filter.accept(dir, file.getName()) )
            {
                if (!file_found.fileFound(file)) {
                    return false;
                }
            }
        }

       return true;
    }    
}
