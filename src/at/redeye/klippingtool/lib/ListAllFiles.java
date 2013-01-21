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
public class ListAllFiles implements FileFoundInterface
{
    public boolean fileFound(File file) {
        System.out.println(file);
        return true;
    }

    public boolean diveIntoSubDir(File file) {
        return true;
    }
}
