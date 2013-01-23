/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.klippingtool;

import at.redeye.FrameWork.base.BaseAppConfigDefinitions;
import at.redeye.FrameWork.base.prm.PrmDefaultChecksInterface;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.prm.impl.PrmDefaultCheckSuite;

/**
 *
 * @author martin
 */
public class AppConfigDefinitions extends BaseAppConfigDefinitions {

    public static DBConfig MaxNumClipHistory = new DBConfig("MaxNumClipHistory","10000","Maximale Anzahl an Datensätze die geispeichert werden soll", 
            new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_LONG));    
   
    public static void registerDefinitions() {

        BaseRegisterDefinitions();

        addLocal(MaxNumClipHistory);
    }    
}
