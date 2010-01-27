/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ii.ii.utilities;

import com.thoughtworks.xstream.XStream;
import java.text.Annotation;
import org.ii.ii.entities.Opinion;
import org.ii.ii.serviceEntities.CalculatedOpinion;
import org.ii.ii.serviceEntities.LoginOpinion;
import org.restlet.data.MediaType;
import org.restlet.ext.xstream.XstreamRepresentation;

/**
 *
 * @author svenni
 */
public class MyXstreamRepresentation<Class> extends XstreamRepresentation<Class> {

    public MyXstreamRepresentation(MediaType mediaType, Class source) {
        super(mediaType,source);
    }

    @Override
    protected XStream createXstream(MediaType mediaType) {
        XStream xstream = super.createXstream(mediaType);
        xstream.alias("CalculatedOpinion", CalculatedOpinion.class);
        xstream.alias("LoginOpinion", LoginOpinion.class);
        xstream.useAttributeFor(int.class);
        xstream.useAttributeFor(Integer.class);
        xstream.useAttributeFor(long.class);
        xstream.useAttributeFor(Long.class);
        xstream.useAttributeFor(double.class);
        xstream.useAttributeFor(Double.class);
        xstream.useAttributeFor(String.class);
//        xstream.addImplicitCollection(CalculatedOpinion.class, "loginopinions");
        return xstream;
    }

}
