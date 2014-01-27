/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.detect_command;

import at.redeye.FrameWork.utilities.ThreadReader;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class DetectCommand 
{
    private DBConfig config;
    private String control_sequence = null;
    private Root root;
    
    public DetectCommand( Root root, DBConfig config, String control_sequence )
    {
        this.root = root;
        this.config = config;
        this.control_sequence = control_sequence;
    }
    
    
    public DetectCommand( DBConfig config )
    {
        this.config = config;
    }    
    
    public boolean detectCommand()
    {
        try {
            return tryDetectByHelp();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }
    
    private boolean tryDetectByHelp() throws InterruptedException, IOException
    {
        Process process = Runtime.getRuntime().exec(config.value.getValue());
        
        ThreadReader reader = new ThreadReader(process);
        
        reader.start();
        
        reader.join();
        
        String result = reader.getResult();    
        
        if( reader.getReturnValue() != 0 )
            return false;
        
        if( control_sequence != null )
        {
            if( result.contains(control_sequence))
                return true;
        }
        
        return false;
    }
}
