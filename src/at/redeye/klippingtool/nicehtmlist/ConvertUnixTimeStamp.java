/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool.nicehtmlist;

import at.redeye.FrameWork.base.BaseAppConfigDefinitions;
import at.redeye.FrameWork.base.Root;
import at.redeye.klippingtool.AppConfigDefinitions;
import at.redeye.klippingtool.ListDataContainer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ConvertUnixTimeStamp implements Converter 
{
    private static final Logger logger = Logger.getLogger(ConvertUnixTimeStamp.class);
    
    SimpleDateFormat sdf;
        
    public ConvertUnixTimeStamp( Root root )
    {
      try {
            sdf = new SimpleDateFormat(root.getSetup().getLocalConfig(AppConfigDefinitions.DateTimeFormat));
        } catch( Exception ex ) {
            logger.error(ex,ex);
        }        
    }
    
    @Override
    public void appendInfo( StringBuilder sb, ListDataContainer data )
    {
        String sd = data.getClipData().trim();
        
        if( sd.length() > 20 ) {
            return;
        }
        
        if( sd.length() < 7 ) {
            return;
        }        
        
        if( !sd.matches("[0-9]+") ) {
            return;
        }
        
        try {
            long val = Long.valueOf(sd);
            
            sb.append( " => " + sdf.format( new Date( val * 1000 )) );
                                    
        } catch( NumberFormatException ex ) {
            return;
        }
    }
}
