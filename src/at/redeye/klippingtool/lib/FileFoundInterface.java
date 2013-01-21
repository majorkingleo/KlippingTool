/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool.lib;

import java.io.File;

/**
 *
 * @author moberza
 */
public interface FileFoundInterface
{
    public boolean fileFound( File file );
    public boolean diveIntoSubDir( File file );
}
