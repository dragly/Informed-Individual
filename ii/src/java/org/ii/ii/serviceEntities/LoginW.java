/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.serviceEntities;

import org.ii.ii.entities.Login;

/**
 *
 * @author svenni
 */
public class LoginW {

    private Long loginID;
    private String displayname;

    public LoginW() {
    }

    public LoginW(Login login) {
        this.setDisplayname(login.getDisplayname());
        this.setLoginID(login.getId());
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public Long getLoginID() {
        return loginID;
    }

    public void setLoginID(Long id) {
        this.loginID = id;
    }
}
