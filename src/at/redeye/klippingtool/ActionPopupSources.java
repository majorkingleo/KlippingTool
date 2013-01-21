/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.Root;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ActionPopupSources extends JPopupMenu
{
    private static final Logger logger = Logger.getLogger(ActionPopupSources.class.getName());

    Root root;
    MainWin  mainwin;

    public ActionPopupSources(final MainWin mainwin, final String cont) {
        this.root = mainwin.getRoot();
        this.mainwin = mainwin;


        JMenuItem menuItem = new JMenuItem(root.MlM("Verzeichnis hinzufügen"));

        add(menuItem);

        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                JFileChooser fc = new JFileChooser();
                
                fc.setAcceptAllFileFilterUsed(true);
                fc.setMultiSelectionEnabled(false);     
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                String last_path = mainwin.getLastOpenPath();

                logger.info("last path: " + last_path);

                if (last_path != null) {
                    fc.setCurrentDirectory(new File(last_path));
                }

                int retval = fc.showSaveDialog(mainwin);

                if (retval != 0) {
                    return;
                }

                final File target_dir = fc.getSelectedFile();
                
                if( target_dir.isDirectory() ) {
                    mainwin.addSourceDirectory(target_dir);
                    mainwin.setLastOpenPath(target_dir.getParentFile().getPath());
                }                                
            }                        
        });


        if (cont != null) {
            menuItem = new JMenuItem(root.MlM("Verzeichnis entfernen"));

            add(menuItem);

            menuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    mainwin.removeSourcesDirectory(cont);
                }
            });
        }
    }

}
