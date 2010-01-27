/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.services;

import java.util.ArrayList;
import org.ii.ii.entities.Trust;
import javax.persistence.EntityManager;
import org.ii.ii.entities.Login;
import org.ii.ii.entities.Opinion;
import org.ii.ii.entities.Product;
import org.ii.ii.utilities.EMF;
import org.ii.ii.utilities.MyXstreamConverter;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.engine.Engine;
import org.restlet.routing.Router;

/**
 *
 * @author svenni
 */
public class RestApplication extends Application {

    private EntityManager em;

    public RestApplication() {
        Engine.getInstance().getRegisteredConverters().add(0, new MyXstreamConverter());
        // some dummy data =D
        System.err.println("Creating dummy data");
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        Opinion opinion = new Opinion();
        Opinion opinion2 = new Opinion();
        Opinion opinion3 = new Opinion();
        Product product = new Product();
        Login login = new Login();
        Login login2 = new Login();
        Login login3 = new Login();
        Trust trust = new Trust();
        em.persist(trust);
        em.persist(opinion);
        em.persist(opinion2);
        em.persist(opinion3);
        em.persist(product);
        em.persist(login);
        em.persist(login2);
        em.persist(login3);
        ArrayList<Login> logins = new ArrayList<Login>();
        trust.getLogins().add(login);
        trust.getLogins().add(login2);
        trust.getLogins().add(login3);
        login.setDisplayname("Røde kors");
        login2.setDisplayname("Redd barna");
        login3.setDisplayname("FriBit");
        product.setUpccode("12345");
        opinion.setDescription("Passe bra");
        opinion.setScore(10);
        opinion.setProduct(product);
        opinion2.setDescription("Veldig dårlig");
        opinion2.setScore(30);
        opinion2.setProduct(product);
        opinion3.setDescription("Midt på treet");
        opinion3.setScore(60);
        opinion3.setProduct(product);
        login.getOpinions().add(opinion);
        login2.getOpinions().add(opinion2);
        login3.getOpinions().add(opinion3);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attach("/hello", RestResource.class);
        router.attach("/opinion/identifier/{identifier}/trustid/{trustid}/type/{type}", OpinionResource.class);

        return router;
    }
}
