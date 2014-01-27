/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool.chm;

import at.redeye.klippingtool.ListDataContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class CHMActionListener implements ActionListener {
    
    private static final Logger logger = Logger.getLogger(CHMActionListener.class.getName());

    JPanel jPanelCHM;
    ListDataContainer cont;
    
    public CHMActionListener( ListDataContainer cont, JPanel jPanelCHM )
    {
        this.jPanelCHM = jPanelCHM;
        this.cont = cont;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getID() == SimpleLookUpCHM.ACTION_ID.CLEAR.ordinal()) {
            jPanelCHM.removeAll();
            JTabbedPane tabPanel = new JTabbedPane();
            jPanelCHM.add(tabPanel);
            logger.debug("CLEAR");
        }

        if (e.getID() == SimpleLookUpCHM.ACTION_ID.FOUND_CHM.ordinal()) {

            logger.debug("ADD " + e.getActionCommand());

            JPanel panel = new JPanel();
            panel.setLayout(new java.awt.BorderLayout());
            JTabbedPane tabPanel = (JTabbedPane) jPanelCHM.getComponent(0);

            try {
                JEditorPane editor = new JEditorPane();
                editor.setContentType("text/html");
                editor.setCaretPosition(0);
                editor.setEditable(false);
                editor.setPage(new URL(e.getActionCommand()));
                adjustFont(editor);

                String title = cont.getClipData();

                if (e instanceof SimpleLookUpCHM.ActionCHM) {
                    SimpleLookUpCHM.ActionCHM em = (SimpleLookUpCHM.ActionCHM) e;
                    title = em.getTitle();
                }

                panel.add(new JScrollPane(editor));

                tabPanel.addTab(title, panel);
                jPanelCHM.revalidate();
                jPanelCHM.updateUI();
            } catch (IOException ex) {
                logger.error(ex, ex);
            }
        }        
        
       
    }
    
    private void adjustFont(JEditorPane body)
    {
        int font_size = 12;
        EditorKit editor = body.getEditorKit();
   
        if (editor instanceof HTMLEditorKit) {                                   
            
            Source source = new Source(body.getText());
            source.fullSequentialParse();

            ArrayList<String> tags = new ArrayList();

            for (StartTag tag : source.getAllStartTags()) {                
                tags.add(tag.getName());
            }
            
            
            HTMLEditorKit html_editor = (HTMLEditorKit) editor;

            System.out.println("Value: " + font_size);

            StyleSheet sheet = html_editor.getStyleSheet();          
            
            String rule = "{ font-size: " + (font_size) + "pt; }";

            StringBuilder sb = new StringBuilder();

            for (String tag : tags) {
                sb.append(tag);
                sb.append(rule);
            }

            sheet.addRule(sb.toString());

            String text = body.getText();
            body.setDocument(html_editor.createDefaultDocument());
            body.setText(text);                       
            body.setCaretPosition(0);

        }
    }
}
