/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.base.*;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.klippingtool.findinclude.FindIncludeFor;
import at.redeye.klippingtool.manpage.FindManPageFor;
import at.redeye.klippingtool.manpage.SimpleLookUpManPage;
import at.redeye.klippingtool.manpage.SimpleLookUpManPage.ActionManPage;
import at.redeye.klippingtool.nicehtmlist.ComponentCellRenderer;
import at.redeye.klippingtool.nicehtmlist.HtmlListFactory;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author martin
 */
public class MainWin extends BaseDialog implements StatusInformation {

    private String MESSAGE_CLEAR_QUEUE;
    private String MESSAGE_CLEAR_QUEUE_TITLE;
    Vector<ListDataContainer> listData;
    boolean firstRun = true;
    boolean changeStyle = false;
    WatchClipboardThread clipping_thread;
    String last_path;
    Vector<String> listSources;
    FindIncludeFor find_include_for;
    FindManPageFor find_manpage_for;
    String current_working_file;
    boolean nice_html_list = false;
    HtmlListFactory html_list_factory;

    /**
     * Creates new form MainWin
     */
    public MainWin(Root root) {
        super(root, root.getAppTitle());

        initComponents();

        try {
            loadDb();
        } catch (Exception ex) {
            logger.error(ex, ex);
        }


        try {
            loadDbSources();
            if (listSources != null) {
                jLSources.setListData(listSources);
            }
        } catch (Exception ex) {
            logger.error(ex, ex);
        }

        clipping_thread = new WatchClipboardThread(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        String data = e.getActionCommand();
                        appendClipData(data);
                    }
                });
            }
        });

        clipping_thread.setDaemon(true);
        clipping_thread.start();

        last_path = root.getSetup().getLocalConfig("LastPath", "");

        find_include_for = new FindIncludeFor();
        find_manpage_for = new FindManPageFor();

        getAutoRefreshTimer().schedule(new TimerTask() {

            @Override
            public void run() {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {

                        String status_text = null;

                        if (find_include_for.isIdle()) {
                            status_text = "Warte auf Arbeit ... ";
                        } else {
                            status_text = "Suche ... ";

                            if (current_working_file != null) {
                                status_text += current_working_file;
                            }
                        }

                        logger.trace(status_text);

                        if (!jLStatus.getText().equals(status_text)) {
                            jLStatus.setText(status_text);
                        }
                    }
                });
            }
        }, 1000, 500);

        // Suchmodus

        initSearchPanel();

        MESSAGE_CLEAR_QUEUE = MlM("Soll die Liste tatsächlich geleert werden?");
        MESSAGE_CLEAR_QUEUE_TITLE = MlM("Liste Leeren");


        new AppConfigDefinitions.UpdateListener(root, AppConfigDefinitions.NiceHtmlList, new AppConfigDefinitions.UpdateListenerLoadChangesFromString() {

            @Override
            public void doUpdate(String value) {
                nice_html_list = StringUtils.isYes(value);
                
                if( changeStyle )
                    changeHistListStyle();
            }
        });
        
        new AppConfigDefinitions.UpdateListener(root, BaseAppConfigDefinitions.DateFormat, new AppConfigDefinitions.UpdateListenerLoadChangesFromString() {

            @Override
            public void doUpdate(String value) {                
                if( changeStyle )
                    changeHistListStyle();
            }
        });       
        
        
        new AppConfigDefinitions.UpdateListener(root,  AppConfigDefinitions.NiceHtmlListInfoTextColor, new AppConfigDefinitions.UpdateListenerLoadChangesFromString() {

            @Override
            public void doUpdate(String value) {    
                if( html_list_factory != null )
                    html_list_factory.setListInfoTextColor(value);
                
                if( changeStyle )
                    changeHistListStyle();
            }
        });            
        
        changeHistListStyle();       
        changeStyle = true;
        jPanelManPage.setLayout(new java.awt.BorderLayout());
    }

    private void loadDb() throws IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getDbName())));

        listData = (Vector<ListDataContainer>) objIn.readObject();
        
        /*
        // create testdata
        for( int i = 0; i < 1000; i++ )
        {
            listData.add(new ListDataContainer(String.format("text%04d", i)));
        }
        * 
        */
        
    }

    private void loadDbSources() throws IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getDbSourcesName())));

        listSources = (Vector<String>) objIn.readObject();
    }
    
    private void startSearch(final ListDataContainer cont)
    {
        if( !cont.haveIncludes() )
            find_include_for.findIncludeFor(cont, listSources, this);
        
        find_manpage_for.findManPageFor(cont, this, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                                
                if( e.getID() == SimpleLookUpManPage.ACTION_ID.CLEAR.ordinal() ) {
                    jPanelManPage.removeAll();                              
                    JTabbedPane tabPanel = new JTabbedPane();
                    jPanelManPage.add(tabPanel);
                    tabPanel.setVisible(true);
                    logger.debug("CLEAR");
                }

                if( e.getID() == SimpleLookUpManPage.ACTION_ID.FOUND_MANPAGE.ordinal() ) {
                    
                    logger.debug("ADD " + e.getActionCommand());
                    
                    JPanel panel = new JPanel();
                    panel.setLayout(new java.awt.BorderLayout());
                    JTabbedPane tabPanel = (JTabbedPane) jPanelManPage.getComponent(0);
                                        
                    JEditorPane editor = new JEditorPane();
                    editor.setContentType("text/html");                                                           
                    editor.setText(e.getActionCommand());
                    editor.setCaretPosition(0); 
                    editor.setEditable(false);                                                   
                                    
                    String title = cont.getClipData();
                    
                    if( e instanceof SimpleLookUpManPage.ActionManPage ) {
                        ActionManPage em = (ActionManPage) e;
                        title = em.getTitle();
                    }
                    
                    panel.add( new JScrollPane(editor));
                    
                    tabPanel.addTab(title,panel);                                        
                    jPanelManPage.revalidate();
                    jPanelManPage.updateUI();
                }
                
            }
        });
    }

    private void appendClipData(String data) {
        if (listData == null) {
            listData = new Vector();
        }

        synchronized (listData) {

            // ignore double entries
            if (listData.size() > 0 && listData.get(0).getClipData().equals(data)) {
                if (firstRun) {
                    startSearch(listData.get(0));
                    setHistListData(listData);
                    firstRun = false;
                    listData.get(0).updateUsageDate();
                }
                return;
            }

            // don't search twice if not necessary
            boolean found = false;
            for (int i = 0; i < listData.size(); i++) {
                ListDataContainer ld = listData.get(i);

                if (ld.getClipData().equals(data)) {
                    found = true;
                    listData.removeElementAt(i);
                    listData.insertElementAt(ld, 0);
                    ld.updateUsageDate();                    
                    startSearch(listData.get(0));                    
                    break;
                }
            }

            if (!found) {
                listData.insertElementAt(new ListDataContainer(data), 0);
                startSearch(listData.get(0));
                listData.get(0).updateUsageDate();
            }
        }
        setHistListData(listData);
    }

    String getDbName() {
        return Setup.getAppConfigFile(root.getAppName(), "cliphist.ser");
    }

    String getDbSourcesName() {
        return Setup.getAppConfigFile(root.getAppName(), "sources.ser");
    }

    private void saveDB() throws IOException {
        try (ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(getDbName())))) {

            int max_data = Integer.valueOf(root.getSetup().getLocalConfig(AppConfigDefinitions.MaxNumClipHistory));

            if (listData.size() > max_data) {
                listData.setSize(max_data);
            }

            objOut.writeObject(listData);
        }
    }

    private void saveDBSources() throws IOException {
        try (ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(getDbSourcesName())))) {
            objOut.writeObject(listSources);
        }
    }

    @Override
    public void close() {
        setVisible(false);

        root.getSetup().setLocalConfig("LastPath", last_path);

        try {
            saveDB();
        } catch (IOException ex) {
            logger.error(ex, ex);
        }

        try {
            saveDBSources();
        } catch (IOException ex) {
            logger.error(ex, ex);
        }

        super.close();
    }

    public String getLastOpenPath() {
        return last_path;
    }

    public void setLastOpenPath(String path) {
        last_path = path;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLHist = new javax.swing.JList();
        jSplitPane1 = new javax.swing.JSplitPane();
        jLStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jSearch = new javax.swing.JTextField();
        jBClean = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLSources = new javax.swing.JList();
        jPanelManPage = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLHist.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jLHist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLHistMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jLHist);

        jSplitPane1.setDividerLocation(200);

        jLStatus.setFont(new java.awt.Font("Dialog", 0, 12));
        jLStatus.setText(" ");
        jSplitPane1.setLeftComponent(jLStatus);

        jBClean.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/klippingtool/icons/konquefox_erase.png"))); // NOI18N
        jBClean.setBorderPainted(false);
        jBClean.setContentAreaFilled(false);
        jBClean.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBClean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCleanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jBClean, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jBClean, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel3);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Verzeichnisse", jPanel1);

        javax.swing.GroupLayout jPanelManPageLayout = new javax.swing.GroupLayout(jPanelManPage);
        jPanelManPage.setLayout(jPanelManPageLayout);
        jPanelManPageLayout.setHorizontalGroup(
            jPanelManPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );
        jPanelManPageLayout.setVerticalGroup(
            jPanelManPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 322, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Man Page", jPanelManPage);

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

        Object obj = jLHist.getSelectedValue();
                
        ListDataContainer cont = null;
        
        if( obj instanceof ListDataContainer ) {
            cont = (ListDataContainer) obj;
        } else {
            cont = HtmlListFactory.getContainer(obj);
        }
            

        boolean do_popup = false;

        if (evt != null) {
            do_popup = evt.isPopupTrigger();
        }

        if (!do_popup && Setup.is_win_system()) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                do_popup = true;
            }
        }


        if (do_popup) {
            System.out.println("popup trigger");

            int idx = jLHist.locationToIndex(evt.getPoint());

            if (idx >= 0) {
                jLHist.setSelectedIndex(idx);
                obj = jLHist.getSelectedValue();
            }

            if (obj instanceof ListDataContainer) {
                cont = (ListDataContainer) obj;
            } else {
                cont = HtmlListFactory.getContainer(obj);
            }
        }

        if (cont == null && do_popup) {
            JPopupMenu popup = new ActionPopupClipboard(this, null);

            popup.show(evt.getComponent(), evt.getX(), evt.getY());
            return;
        }

        if (cont == null) {
            return;
        }

        if (do_popup) {
            JPopupMenu popup = new ActionPopupClipboard(this, cont);

            popup.show(evt.getComponent(), evt.getX(), evt.getY());
            // return;
        } else {

            clipping_thread.setLastClippingText(cont.getClipData());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection sel = new StringSelection(cont.getClipData());
            clipboard.setContents(sel, sel);
            cont.incCharma();

        } // else        
    }//GEN-LAST:event_jLHistMousePressed

    private void jLSourcesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLSourcesMousePressed
        System.out.println("pressed");

        String name = (String) jLSources.getSelectedValue();

        boolean do_popup = evt.isPopupTrigger();

        if (!do_popup && Setup.is_win_system()) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                do_popup = true;
            }
        }


        if (do_popup) {
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
        }
    }//GEN-LAST:event_jLSourcesMousePressed

    private void jBCleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCleanActionPerformed

        jSearch.setText("");
        jSearch.requestFocus();
        setHistListData(listData);

	}//GEN-LAST:event_jBCleanActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBClean;
    private javax.swing.JList jLHist;
    private javax.swing.JList jLSources;
    private javax.swing.JLabel jLStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelManPage;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jSearch;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    void addSourceDirectory(final File target_dir) {
        if (listSources == null) {
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

    void removeSourcesDirectory(String name) {
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
        
        if( html_list_factory != null )
            html_list_factory.cleanQueue();
        
        setHistListData(listData);
    }

    @Override
    public void setCurrentWorkingFile(String name) {
        current_working_file = name;
    }

    private void initSearchPanel() {
        jSearch.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    close();
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_KP_DOWN) {
                    jLHist.requestFocus();
                    jLHist.setSelectedIndex(0);
                    return;
                }

                String search_string = jSearch.getText();

                System.out.println(search_string);

                search_for(search_string.toLowerCase());
            }
        });

        jLHist.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    close();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jLHistMousePressed(null);
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    jLHist.setSelectedIndex(-1);

                    String new_string = jSearch.getText();
                    new_string = new_string.substring(0, new_string.length() - 1);
                    jSearch.setText(new_string);
                    jSearch.requestFocus();
                    search_for(new_string.toLowerCase());
                }

            }
        });
    }

    private void search_for(String search) {
        Vector<ListDataContainer> search_cont = new Vector();

        for (ListDataContainer cont : listData) {
            if (cont.getClipDataLowerCase().contains(search)) {
                search_cont.add(cont);
            }
        }

        Collections.sort(listData, new Comparator<ListDataContainer>() {

            @Override
            public int compare(ListDataContainer o1, ListDataContainer o2) {
                if (o1.getCharma() > o2.getCharma()) {
                    return -1;
                } else if (o1.getCharma() < o2.getCharma()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        setHistListData(search_cont);
    }

    void cleanQueueAndMayAsk() {
        if (!StringUtils.isYes(root.getSetup().getLocalConfig(AppConfigDefinitions.RequestBeforeCleaningQueue))) {
            cleanQueue();
            return;
        }

        if (JOptionPane.showConfirmDialog(rootPane, MESSAGE_CLEAR_QUEUE,
                MESSAGE_CLEAR_QUEUE_TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            cleanQueue();
        }
    }

    void changeHistListStyle() {
        if (nice_html_list) {
            if( html_list_factory == null ) {
                html_list_factory = new HtmlListFactory(root);
            }
            
            jLHist.setCellRenderer(new ComponentCellRenderer());
        } else {
            
            if( html_list_factory != null ) {
                html_list_factory.dispose();
                html_list_factory = null;
            }
                    
            jLHist.setCellRenderer(new DefaultListCellRenderer());
        }
        
        setHistListData(listData);
    }

    void setHistListData(Vector<ListDataContainer> list) {
        
        if( list == null )
            return;
                
        synchronized (list) {
            if (!nice_html_list || html_list_factory == null ) {
                jLHist.setListData(list);
                return;
            }            
            
            jLHist.setListData(html_list_factory.createLabels(list));
        }
    }
}
