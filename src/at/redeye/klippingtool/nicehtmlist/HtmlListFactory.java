/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.nicehtmlist;

import at.redeye.FrameWork.base.BaseAppConfigDefinitions;
import at.redeye.FrameWork.base.Root;
import at.redeye.klippingtool.AppConfigDefinitions;
import at.redeye.klippingtool.ListDataContainer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JLabel;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class HtmlListFactory 
{
    private static final Logger logger = Logger.getLogger(HtmlListFactory.class);

    
    public static class MyLabel extends JLabel
    {
        private ListDataContainer cont;
        
        public MyLabel( ListDataContainer cont, String text )
        {
            super(text);
            this.cont = cont;
        }
        
        public ListDataContainer getContainer()
        {
            return cont;
        }
    }
    
    SimpleDateFormat sdf;
    String inactiveColor;
    HashMap<ListDataContainer,MyLabel> label_cache;  
    
    ArrayList<Converter> converter;       
    
    public HtmlListFactory( Root root )
    {
        try {
            sdf = new SimpleDateFormat(root.getSetup().getLocalConfig(BaseAppConfigDefinitions.DateFormat));
        } catch( Exception ex ) {
            logger.error(ex,ex);
        }
        
        converter = new ArrayList<>();
        
        converter.add(new ConvertUnixTimeStamp(root));
        
        inactiveColor = root.getSetup().getLocalConfig(AppConfigDefinitions.NiceHtmlListInfoTextColor);
        
        label_cache = new HashMap<>();
    }

    public MyLabel createLabel( ListDataContainer cont )
    {
        return createLabel(cont, null);
    }
    
    private String getText4Label(  ListDataContainer cont )
    {
        StringBuilder sb = new StringBuilder("<html><body>");
        
        if( cont.getCharma() > 0 ) {
            sb.append("<font color=\"").append(inactiveColor).append("\">(");
            sb.append(cont.getCharma());
            sb.append(")</font> ");
        }
        
        // sb.append("<b>");
        sb.append(cont.toString());
        // sb.append("</b>");
        
        if( sdf != null )
        {
            sb.append(" <i><font color=\"" );
            sb.append(inactiveColor);
            sb.append("\">");
            sb.append(sdf.format(new Date(cont.getUsageDate())));
            sb.append("</font></i>");
        }
        
        for( Converter conv : converter ) {
            conv.appendInfo(sb, cont);
        }
        
        sb.append("</body></html>");
        
        return sb.toString();
    }
    
    public MyLabel createLabel( ListDataContainer cont, SimpleDateFormat sdf )
    {                
        MyLabel label = new MyLabel(cont, getText4Label(cont));
        label.setOpaque(true);
        label.setVisible(true);
        return label;
    }
    
    public Vector<MyLabel> createLabels( Collection<ListDataContainer> list )
    {
        Vector<MyLabel> labels = new Vector();
        
        for( ListDataContainer cont : list ) {
            
            MyLabel label = label_cache.get(cont);
            
            if( label != null ) {
                
                String text = getText4Label(cont);
                if( !text.equals(label.getText()) )
                    label.setText(text);

            } else {
                label = createLabel(cont, sdf);
            }
            
            labels.add(label);
            label_cache.put(cont,label);
        }
        
        return labels;
    }    


    public void dispose() {
        label_cache.clear();
    }    

    public static ListDataContainer getContainer(Object obj) {
        if( obj instanceof MyLabel )
        {
            MyLabel label = (MyLabel) obj;
            return label.getContainer();
        }
        
        return null;
    }    
    

    public void setListInfoTextColor(String value) {
        inactiveColor = value;
    }    
    
    public void cleanQueue()
    {
        label_cache.clear();
    }
}
