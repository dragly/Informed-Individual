/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.serviceEntities;

import java.util.ArrayList;
import java.util.List;
import org.restlet.ext.xstream.XstreamRepresentation;

/**
 *
 * @author svenni
 */
public class CalculatedOpinion {

    private int score;
    private int relevance;
    private List loginOpinions = new ArrayList();

    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<LoginOpinion> getLoginopinions() {
        return loginOpinions;
    }

    public void setLoginopinions(List<LoginOpinion> loginopinions) {
        this.loginOpinions = loginopinions;
    }
}
