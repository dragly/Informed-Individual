/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author svenni
 */
@Entity
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String upccode;
    public Product() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpccode() {
        return upccode;
    }

    public void setUpccode(String upccode) {
        this.upccode = upccode;
    }
    
}
