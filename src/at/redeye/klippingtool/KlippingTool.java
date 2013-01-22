/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.widgets.StartupWindow;

/**
 *
 * @author martin
 */
public class KlippingTool extends BaseModuleLauncher
{
    public KlippingTool( String args[] )
    {
        super(args);
        
        root  = new LocalRoot("KlippingTool", "KlippingTool", false, false);
        
        root.setBaseLanguage("de");
        root.setDefaultLanguage("en");
        
        
    }
    
    public void run()
    {
        if (splashEnabled()) {
            splash = new StartupWindow(
                    "/at/redeye/FrameWork/base/resources/pictures/redeye.png");
        }                 
         
        // AppConfigDefinitions.registerDefinitions();
	FrameWorkConfigDefinitions.registerDefinitions();
        
        // this sets the default value only
        FrameWorkConfigDefinitions.LookAndFeel.value.loadFromString("nimbus");
        
        setLookAndFeel(root);
         
        configureLogging();
               
        MainWin mainwin = new MainWin(root);
        
        closeSplash();
        
        mainwin.setVisible(true);      
        mainwin.toFront();        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        new KlippingTool(args).run();        
    }

    @Override
    public String getVersion() {
        return Version.getVersion();
    }
}
