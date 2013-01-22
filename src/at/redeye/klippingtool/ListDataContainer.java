/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author martin
 */
public class ListDataContainer implements Serializable
{
    private static final int MAX_LENGTH = 40;
    
    private String title;    
    private String clipData;
    private ArrayList<String> include_strings;
    private ArrayList<String> include_strings_line_in_file;
    
    public ListDataContainer( String clipData )
    {
        this.title = clipData;
        this.clipData = clipData;
        
        if( title.length() > MAX_LENGTH )
            title = title.substring(0,MAX_LENGTH - 5) + " ... ";
    }    
    
    public ListDataContainer( String title, String clipData )
    {
        this.title = title;
        this.clipData = clipData;
    }
    
    @Override
    public String toString()
    {
        return title;
    }

    public String getClipData() {
        return clipData;
    }

    public void addIncludeString( String include_string )
    {
        addIncludeString( include_string, "" );
    }
    
    public void addIncludeString( String include_string, String include_line )
    {
        if( include_strings == null ) {
            include_strings = new ArrayList();
            include_strings_line_in_file = new ArrayList();
        }
        
        include_strings.add(include_string);
        include_strings_line_in_file.add(include_line);
    }
        
    public ArrayList<String> getIncludes()
    {
        return include_strings;
    }
    
    public String getIncludeLine( int idx )
    {
        return include_strings_line_in_file.get(idx);
    }

    boolean haveIncludes() {
        if( include_strings == null )
            return false;
        
        return !include_strings.isEmpty();
    }
    
}
