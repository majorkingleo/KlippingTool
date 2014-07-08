/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.klippingtool.nicehtmlist;

import at.redeye.klippingtool.ListDataContainer;

/**
 *
 * @author martin
 */
public interface Converter {

    void appendInfo(StringBuilder sb, ListDataContainer data);
    
}
