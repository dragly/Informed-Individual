/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.services;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author svenni
 */
public class RestResource extends ServerResource {

    @Get("xml")
    public Representation toXml() {
        String kake = "not set";
        kake = getRequest().getResourceRef().getQueryAsForm().getFirstValue("kake");
        try {
            DomRepresentation representation = new DomRepresentation(MediaType.TEXT_XML);
            // Generate a DOM document representing the item.
            Document d = representation.getDocument();
            Element eltItem = d.createElement("item");
            d.appendChild(eltItem);
            Element eltName = d.createElement("name");
            eltName.appendChild(d.createTextNode(kake));
            eltItem.appendChild(eltName);
            Element eltDescription = d.createElement("description");
            eltDescription.appendChild(d.createTextNode("there"));
            eltItem.appendChild(eltDescription);
            d.normalizeDocument();
            // Returns the XML representation of this document.
            return representation;
        } catch (IOException ex) {
            Logger.getLogger(RestResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
