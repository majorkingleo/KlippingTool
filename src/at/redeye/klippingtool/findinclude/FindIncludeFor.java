/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.findinclude;

import at.redeye.FrameWork.utilities.WorkerThread.WorkerThread;
import at.redeye.klippingtool.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author martin
 */
public class FindIncludeFor extends BaseLookup
{
    WorkerThread worker_thread;        
    
    public FindIncludeFor(MainWin mainwin )
    {
       super(mainwin, null);
    }
    
    public void findIncludeFor( ListDataContainer cont, Vector<String> listSources, StatusInformation statusinfo )
    {
        if( listSources == null || cont == null )
            return;
                
        for( String source_dir : listSources ) {
            File dir = new File( source_dir );
            
            if( dir.exists() && dir.isDirectory() && cont.getClipData().trim().length() > 2 ) {
                addWorker(new SimpleFindIncludeFor(cont, source_dir, statusinfo ));
            }
        }
    }

    @Override
    public void lookUp(ListDataContainer cont) {
         if( !cont.haveIncludes() ) {      
            findIncludeFor(cont, getMainWin().getSources(), getMainWin());
         }
    }
    
    @Override
    public void addPopupMenutItemTo( JPopupMenu popup, String command )
    {
        if( !command.equals(ActionPopupSources.SOURCES_LIST) )
            return;
        
        JMenuItem menuItem = new JMenuItem(getRoot().MlM("Verzeichnis hinzufügen"));

        popup.add(menuItem);

        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                JFileChooser fc = new JFileChooser();
                
                fc.setAcceptAllFileFilterUsed(true);
                fc.setMultiSelectionEnabled(false);     
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                String last_path = getMainWin().getLastOpenPath();

                logger.info("last path: " + last_path);

                if (last_path != null) {
                    fc.setCurrentDirectory(new File(last_path));
                }

                int retval = fc.showSaveDialog(getMainWin());

                if (retval != 0) {
                    return;
                }

                final File target_dir = fc.getSelectedFile();
                
                if( target_dir.isDirectory() ) {
                    getMainWin().addSourceDirectory(target_dir);
                    getMainWin().setLastOpenPath(target_dir.getParentFile().getPath());
                }                                
            }                        
        });        
    }    
}
