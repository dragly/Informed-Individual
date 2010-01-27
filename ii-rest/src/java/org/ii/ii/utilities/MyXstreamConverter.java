/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ii.ii.utilities;

import org.restlet.data.MediaType;
import org.restlet.ext.xstream.XstreamConverter;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;

/**
 *
 * @author svenni
 */
public class MyXstreamConverter extends XstreamConverter {

    @Override
    protected <T> XstreamRepresentation<T> create(MediaType mediaType, T source) {
        return new MyXstreamRepresentation<T>(mediaType, source);
    }

//    @Override
//    protected <T> XstreamRepresentation<T> create(Representation source) {
//        return new MyXstreamRepresentation<T>(MediaType.TEXT_TSV, source);
//    }

}
