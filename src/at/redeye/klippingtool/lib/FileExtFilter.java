/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool.lib;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author moberza
 */
public class FileExtFilter implements FilenameFilter
{
    // private static final Logger logger = Logger.getLogger("FileExtFilter");

    final private Pattern pattern;

    public FileExtFilter( String extensions )            
    {
        final String ext[] = extensions.split("[ \t;,]");
        
        final StringBuilder regex_string = new StringBuilder("^.*\\.");
        boolean first_regex = true;

        regex_string.append("(");

        for( String s : ext )
        {
            // remove *.
            s = s.replaceAll("\\*\\.", "");

            String s_lower = s;

            if( first_regex )
                first_regex = false;
            else
                regex_string.append("|");                
            
            regex_string.append("(");
            
            for( int i = 0; i < s_lower.length(); i++ )
            {
                char c_lower = s_lower.charAt(i);                

                if (c_lower == '*') {
                    regex_string.append(".*");
                } else if(c_lower == '.') {
                    regex_string.append("\\.");
                } else {                    
                    regex_string.append(c_lower);                    
                }
            }
            
            regex_string.append(")");
        }

          regex_string.append(")");

         regex_string.append("$");

         // logger.info("regex: " + regex_string);

         pattern = Pattern.compile(regex_string.toString(),Pattern.CASE_INSENSITIVE);
    }

    public boolean accept(File dir, String name)
    {
        //logger.info(name);

        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

}
