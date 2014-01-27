/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.chm;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.Setup;
import at.redeye.klippingtool.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author martin
 */
public class FindCHMFor extends BaseLookup {
    String base_search_directory;
    
    public FindCHMFor(MainWin mainwin, JPanel panel )
    {
       super(mainwin, panel);
       
       base_search_directory = Setup.getAppConfigFile(mainwin.root.getAppName(), "chm");   
    }
    
    
    public void findManPageFor( ListDataContainer cont, StatusInformation statusinfo , ActionListener listener )
    {                
        if( cont == null )
            return;
        
        logger.debug("searching for " + cont.getClipData());
        
        addWorker( new SimpleLookUpCHM(cont, listener, base_search_directory ));
    }

    @Override
    public void lookUp(ListDataContainer cont ) 
    {                       
            findManPageFor(cont, getMainWin(), new CHMActionListener(cont, getPanel() ));
    }
    
    @Override
    public void addPopupMenutItemTo( JPopupMenu popup, String command )
    {
        if( !command.equals(ActionPopupSources.SOURCES_LIST) )
            return;
        
        JMenuItem menuItem = new JMenuItem(getRoot().MlM("Hilfedatei hinzufügen"));

        popup.add(menuItem);

        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                JFileChooser fc = new JFileChooser();
                
                fc.setAcceptAllFileFilterUsed(true);
                fc.setFileFilter(new CHMFileFilter());
                fc.setMultiSelectionEnabled(true);     
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                String last_path = getMainWin().getLastOpenPath();

                logger.info("last path: " + last_path);

                if (last_path != null) {
                    fc.setCurrentDirectory(new File(last_path));
                }

                int retval = fc.showOpenDialog(getMainWin());

                if (retval != 0) {
                    return;
                }

                File files[] = fc.getSelectedFiles();
                
                if( files == null )
                    return;
                
                for( File file_ : files )
                {              
                    final File file = file_;
                    
                    if (file.canRead()) {
                        new AutoMBox(FindCHMFor.class.getName()) {

                            @Override
                            public void do_stuff() throws Exception {
                                ExtractCHM extractor = new ExtractCHM(getRoot(), base_search_directory);

                                if (extractor.extractCHM(file)) {
                                    getMainWin().addSourceDirectory(file);
                                    getMainWin().setLastOpenPath(file.getParent());
                                }

                            }
                        };
                    }
                }
            }                        
        });                                
    }

    @Override
    public void removedEntry(String cont, String data_source) {
        
        if (!data_source.equals(ActionPopupSources.SOURCES_LIST)) {
            return;
        }

        ExtractCHM extractor = new ExtractCHM(getRoot(), base_search_directory);

        extractor.deleteCHMdirectory(cont);        
    }
    
    
   
}
