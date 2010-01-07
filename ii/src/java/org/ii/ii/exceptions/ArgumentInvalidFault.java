/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.exceptions;

import javax.xml.ws.WebFault;

/**
 *
 * @author svenni
 */
public class ArgumentInvalidFault extends Exception {

    public ArgumentInvalidFault(String message) {
        super(message);
    }
    
}
