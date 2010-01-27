/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ii.ii.serviceEntities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author svenni
 */
public class TestObject {
    String name;
    String description;
    List children = new ArrayList();

    public TestObject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addChild(TestChild child) {
        children.add(child);
    }

}
