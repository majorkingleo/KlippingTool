/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.base.BaseAppConfigDefinitions;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.PrmCustomChecksInterface;
import at.redeye.FrameWork.base.prm.PrmDefaultChecksInterface;
import at.redeye.FrameWork.base.prm.PrmListener;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.prm.impl.PrmActionEvent;
import at.redeye.FrameWork.base.prm.impl.PrmDefaultCheckSuite;

/**
 *
 * @author martin
 */
public class AppConfigDefinitions extends BaseAppConfigDefinitions {

    interface UpdateListenerLoadChangesFromString
    {                
        void doUpdate( String value );
    }
    
    static class UpdateListener implements PrmListener
    {
         UpdateListenerLoadChangesFromString updater;

        public UpdateListener( Root root, DBConfig config, UpdateListenerLoadChangesFromString updater )
        {
            this.updater = updater;
            
            config.addPrmListener(this);
            
            updater.doUpdate(root.getSetup().getLocalConfig(config));
        }
        
        @Override
        public void onChange(PrmDefaultChecksInterface check, PrmActionEvent event) {
            if(check.doChecks(event) == true )
            {
                updater.doUpdate(event.getNewPrmValue().getValue());
            }
        }

        @Override
        public void onChange(PrmCustomChecksInterface check, PrmActionEvent event) {
            if(check.doCustomChecks(event) == true )
            {
                updater.doUpdate(event.getNewPrmValue().getValue());
            }                        
        }        
    }
    
    public static DBConfig MaxNumClipHistory = new DBConfig("MaxNumClipHistory","10000","Maximale Anzahl an Datensätze die geispeichert werden soll", 
            new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_LONG));    
    public static DBConfig MaxLineWidth = new DBConfig("MaxLineWidth","100","Anzahl der Zeichen die in der Liste dargestellt werden.", 
            new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_LONG));     
    public static DBConfig RequestBeforeCleaningQueue = new DBConfig("RequestBeforeCleaningQueue","true","Nachfragen, bevor die Liste gelelöscht wird", 
            new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_TRUE_FALSE));    
    public static DBConfig NiceHtmlList = new DBConfig("NiceHtmlList","true","Hübsch formartierte Liste.", 
            new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_TRUE_FALSE));   
    public static DBConfig NiceHtmlListInfoTextColor = new DBConfig("NiceHtmlListInfoTextColor","dddddd","Farbe für die nebensächlichen Informationen, wie Datum und beliebtheit.");
    
    public static void registerDefinitions(Root root) {

        BaseRegisterDefinitions();

        addLocal(MaxNumClipHistory);
        addLocal(MaxLineWidth);
        addLocal(RequestBeforeCleaningQueue);
        addLocal(NiceHtmlList);
        addLocal(NiceHtmlListInfoTextColor);
        
        new UpdateListener( root, MaxLineWidth, new UpdateListenerLoadChangesFromString() {

            @Override
            public void doUpdate(String value) {
                ListDataContainer.setMaxTitleLength(Integer.valueOf(value));                
            }
        });                  
    }            
}
