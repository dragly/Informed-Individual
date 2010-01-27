/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.serviceEntities;

/**
 *
 * @author svenni
 */
public class LoginOpinion {

    private int score;
    private LoginW login;

    public LoginOpinion() {
    }

    public LoginOpinion(LoginW loginw) {
        setLogin(loginw);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLogin(LoginW login) {
        this.login = login;
    }
}
