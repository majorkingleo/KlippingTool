/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.cygpath;

import at.redeye.FrameWork.utilities.WorkerThread.WorkInterface;
import at.redeye.klippingtool.ListDataContainer;
import at.redeye.klippingtool.StatusInformation;
import java.io.File;
import org.apache.log4j.Logger;


/**
 *
 * @author martin
 */
public class SimpleConverter implements WorkInterface
{
    private static final Logger logger = Logger.getLogger(SimpleConverter.class.getName());
    ListDataContainer cont;
    StatusInformation statusinfo;
    
    public SimpleConverter( ListDataContainer cont, StatusInformation mainwin )
    {
        this.cont = cont;   
        this.statusinfo = mainwin;
    }

    @Override
    public void work() {

        File file = new File(cont.getClipData());
        if (file.exists()) {
            final String unix_style_file = file.getAbsolutePath().replaceAll("\\\\", "/");
            
            {
                String file_cygwin = unix_style_file;
                file_cygwin = file_cygwin.replaceFirst("^([A-Za-z]+):", "/cygdrive/$1");
                cont.addIncludeString(file_cygwin);
                
                if( doEscapeString(file_cygwin)) {    
                    cont.addIncludeString("\"" + file_cygwin + "\"");  
                }
            }
              
            {
                String file_msys = unix_style_file;
                file_msys = file_msys.replaceFirst("^([A-Za-z]+):", "/$1");
                cont.addIncludeString(file_msys);

                if (doEscapeString(file_msys)) {
                    cont.addIncludeString("\"" + file_msys + "\"");
                }
            }
        }

    }
    
    private boolean doEscapeString(String s) {

        if (s.contains(" ")
                || s.contains("\t")) {
            return true;
        }

        return false;
    }

    @Override
    public void workDone() {
        
    }

    @Override
    public void pleaseStopWorking() {
        
    }
    
}
