/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.manpage;

import at.redeye.klippingtool.ListDataContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ManPageActionListener implements ActionListener {

    private static final Logger logger = Logger.getLogger(ManPageActionListener.class.getName());
    JPanel jPanelManPage;
    ListDataContainer cont;

    public ManPageActionListener(ListDataContainer cont, JPanel jPanelManPage) {
        this.jPanelManPage = jPanelManPage;
        this.cont = cont;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getID() == SimpleLookUpManPage.ACTION_ID.CLEAR.ordinal()) {
            jPanelManPage.removeAll();
            JTabbedPane tabPanel = new JTabbedPane();
            jPanelManPage.add(tabPanel);
            tabPanel.setVisible(true);
            logger.debug("CLEAR");
        }

        if (e.getID() == SimpleLookUpManPage.ACTION_ID.FOUND_MANPAGE.ordinal()) {

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

            if (e instanceof SimpleLookUpManPage.ActionManPage) {
                SimpleLookUpManPage.ActionManPage em = (SimpleLookUpManPage.ActionManPage) e;
                title = em.getTitle();
            }

            panel.add(new JScrollPane(editor));

            tabPanel.addTab(title, panel);
            jPanelManPage.revalidate();
            jPanelManPage.updateUI();
        }

    }
}
