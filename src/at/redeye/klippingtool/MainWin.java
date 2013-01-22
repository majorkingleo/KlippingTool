/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.klippingtool.findinclude.FindIncludeFor;
import java.awt.ComponentOrientation;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.JPopupMenu;

/**
 *
 * @author martin
 */
public class MainWin extends BaseDialog {

    Vector<ListDataContainer> listData;
    boolean firstRun = true;
    WatchClipboardThread clipping_thread;
    String last_path;
    Vector<String> listSources;
    FindIncludeFor find_include_for;
    
    /**
     * Creates new form MainWin
     */
    public MainWin( Root root) {
        super( root, root.getAppTitle() );
        
        initComponents();
        
        try {
            loadDb();
        } catch( Exception ex ) {
            logger.error(ex,ex);
        }        
        
        
        try {
            loadDbSources();
            if( listSources != null ) {
                jLSources.setListData(listSources);
            }
        } catch( Exception ex ) {
            logger.error(ex,ex);
        }            
        
        clipping_thread = new WatchClipboardThread(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable()
                {
                    @Override
                    public void run() {
                        String data = e.getActionCommand();
                        appendClipData( data );
                    }                    
                });
            }
        });
        
        clipping_thread.setDaemon(true);
        clipping_thread.start();   
        
        last_path = root.getSetup().getLocalConfig("LastPath","");
        
        find_include_for = new FindIncludeFor();
        
        getAutoRefreshTimer().schedule(new TimerTask() {

            @Override
            public void run() {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        
                        String status_text = null;                                                
                        
                        if( find_include_for.isIdle() ) {
                            status_text = "Warte auf Arbeit ... ";
                        } else {
                            status_text = "Suche ... ";
                        }
                        
                        logger.trace(status_text);
                        
                        if( !jLStatus.getText().equals(status_text) ) {
                            jLStatus.setText(status_text);
                        }
                    }
                });                        
            }
        }, 1000, 500);
    }    

    private void loadDb() throws IOException, ClassNotFoundException
    {
        ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getDbName())));
            
        listData = (Vector<ListDataContainer>) objIn.readObject();
    }          

    private void loadDbSources() throws IOException, ClassNotFoundException
    {
        ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getDbSourcesName())));
            
        listSources = (Vector<String>) objIn.readObject();
    }     
    
    private void appendClipData( String data )
    {
        if( listData == null ) {
             listData = new Vector();
        }
        
        // ignore double entries
        if( listData.size() > 0 && listData.get(0).getClipData().equals(data) ) {
            if( firstRun ) {
                find_include_for.findIncludeFor(listData.get(0), listSources);
                jLHist.setListData(listData);
                firstRun = false;
            }
            return;
        }                
        
        // don't search twice if not necessary
        boolean found = false;
        for( ListDataContainer ld : listData )
        {
            if( ld.getClipData().equals(data) ) {
                found = true;
                listData.insertElementAt(ld,0);
                if( !ld.haveIncludes() ) {
                    find_include_for.findIncludeFor(listData.get(0), listSources);
                }
                break;                
            }
        }
        
        if( !found ) {
            listData.insertElementAt(new ListDataContainer(data),0);
            find_include_for.findIncludeFor(listData.get(0), listSources);
        }
        
        jLHist.setListData(listData);
    }
    
    String getDbName()
    {
        return Setup.getAppConfigFile(root.getAppName(), "cliphist.ser");
    }    

    String getDbSourcesName()
    {
        return Setup.getAppConfigFile(root.getAppName(), "sources.ser");
    }    
    
    private void saveDB() throws IOException
    {
        try (ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(getDbName())))) {
            objOut.writeObject(listData);
        }
    }
    
    private void saveDBSources() throws IOException
    {
        try (ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(getDbSourcesName())))) {
            objOut.writeObject(listSources);
        }
    }    
    
    @Override
    public void close()
    {
        setVisible(false);
        
        root.getSetup().setLocalConfig("LastPath", last_path);
        
        try {
            saveDB();
        } catch( IOException ex ) {
            logger.error(ex,ex);
        }
        
        try {
            saveDBSources();
        } catch( IOException ex ) {
            logger.error(ex,ex);
        }
        
        super.close();
    }
    
    public String getLastOpenPath()
    {
        return last_path;
    }

    public void setLastOpenPath(String path)
    {
        last_path = path;
    }    
            
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLHist = new javax.swing.JList();
        jLStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLSources = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLHist.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jLHist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLHistMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jLHist);

        jLStatus.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLStatus.setText(" ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 230, Short.MAX_VALUE)
                .addComponent(jLStatus))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addGap(21, 21, 21)))
        );

        jTabbedPane1.addTab("Zwischenablage", jPanel2);

        jLSources.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLSourcesMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jLSources);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Verzeichnisse", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLHistMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLHistMousePressed
        
        System.out.println("pressed");

        ListDataContainer cont = (ListDataContainer) jLHist.getSelectedValue();

        boolean do_popup = evt.isPopupTrigger();

        if( !do_popup && Setup.is_win_system() )
        {
            if( evt.getButton() == MouseEvent.BUTTON3 )
                do_popup = true;
        }


        if ( do_popup ) {
             System.out.println("popup trigger");

            int idx = jLHist.locationToIndex(evt.getPoint());

            if (idx >= 0) {
                jLHist.setSelectedIndex(idx);
            }

             cont = (ListDataContainer) jLHist.getSelectedValue();
        }

        if( cont == null && do_popup) {
            JPopupMenu popup = new ActionPopupClipboard(this, null);

            popup.show(evt.getComponent(), evt.getX(), evt.getY());                        
            return;
        }
        
        if( cont == null ) {
            return;
        }

        if (do_popup) {            
            JPopupMenu popup = new ActionPopupClipboard(this, cont);

            popup.show(evt.getComponent(), evt.getX(), evt.getY());                        
            return;
        } else {

             clipping_thread.setLastClippingText(cont.getClipData());
             Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
             StringSelection sel = new StringSelection(cont.getClipData());
             clipboard.setContents(sel, sel);
            
        } // else        
    }//GEN-LAST:event_jLHistMousePressed

    private void jLSourcesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLSourcesMousePressed
       System.out.println("pressed");

        String name = (String) jLSources.getSelectedValue();

        boolean do_popup = evt.isPopupTrigger();

        if( !do_popup && Setup.is_win_system() )
        {
            if( evt.getButton() == MouseEvent.BUTTON3 )
                do_popup = true;
        }


        if ( do_popup ) {
             System.out.println("popup trigger");

            int idx = jLHist.locationToIndex(evt.getPoint());

            if (idx >= 0) {
                jLHist.setSelectedIndex(idx);
            }

             name = (String) jLSources.getSelectedValue();
        }

        if (do_popup) {
            
            JPopupMenu popup = new ActionPopupSources(this, name);

            popup.show(evt.getComponent(), evt.getX(), evt.getY());                        
            return;
        }    
    }//GEN-LAST:event_jLSourcesMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jLHist;
    private javax.swing.JList jLSources;
    private javax.swing.JLabel jLStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    void addSourceDirectory(final File target_dir) {
        if( listSources == null ) {
            listSources = new Vector<String>();
        }
        
        new AutoMBox(MainWin.class.getName()) {

            @Override
            public void do_stuff() throws Exception {
                listSources.add(target_dir.getCanonicalPath());
                jLSources.setListData(listSources);

            }
        };        
    }
    
    void removeSourcesDirectory( String name )
    {
        listSources.remove(name);
        jLSources.setListData(listSources);
    }

    public void paste(String data) {
        clipping_thread.setLastClippingText(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection sel = new StringSelection(data);
        clipboard.setContents(sel, sel);
    }

    void cleanQueue() {
        listData.clear();
        jLHist.setListData(listData);
    }

}
