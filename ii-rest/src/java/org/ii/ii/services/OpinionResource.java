/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.ii.ii.entities.Login;
import org.ii.ii.entities.Opinion;
import org.ii.ii.entities.Product;
import org.ii.ii.entities.Trust;
import org.ii.ii.serviceEntities.CalculatedOpinion;
import org.ii.ii.serviceEntities.LoginOpinion;
import org.ii.ii.serviceEntities.LoginW;
import org.ii.ii.utilities.EMF;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.restlet.ext.xstream.*;

/**
 *
 * @author svenni
 */
public class OpinionResource extends ServerResource {

    String trustid = "not set :(";
    String identifier;
    String type;
    private EntityManager em;

    @Override
    protected void doInit() throws ResourceException {
        // Get the "itemName" attribute value taken from the URI template
        // /items/{itemName}.
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
        this.trustid = (String) getRequest().getAttributes().get("trustid");
        this.identifier = (String) getRequest().getAttributes().get("identifier");
        this.type = (String) getRequest().getAttributes().get("type");
        System.out.println(type + " " + identifier + " " + trustid);
        setExisting(true);
    }

    @Get("xml")
    public CalculatedOpinion retrieve() {
        try {
            em = EMF.get().createEntityManager();
            em.getTransaction().begin();
            CalculatedOpinion opinion = new CalculatedOpinion();
            Product product = getProduct(identifier, type);
            System.out.println("Found product " + product.getUpccode());
            // Finding the trust
            Query query2 = em.createQuery("SELECT t FROM Trust t");
//            query2.setParameter("trustid", trustid);
            query2.setMaxResults(1);
            List<Trust> trusts = query2.getResultList();
            if (trusts == null || trusts.isEmpty()) {
                em.getTransaction().rollback();
                em.close();
                throw new Exception("Trust not found");
            }
            Trust trust = trusts.get(0);
            System.out.println("Trust found " + trust.getId());
            int counter = 0;
            int sum = 0;
            int rel = 0;
            int clogops = 0;
            List<LoginOpinion> lops = new ArrayList();
            for (Login login : trust.getLogins()) {
                LoginW loginw = new LoginW(login);
                System.out.println("Check login " + login.getDisplayname());
                for (Opinion op : login.getOpinions()) {
                    System.out.println("Check opinion " + op.getDescription());
                    if (op.getProduct().equals(product)) {
                        System.out.println("we have a match!");
                        LoginOpinion logop = new LoginOpinion(loginw);
                        for (LoginOpinion logopFor : lops) { // loop through all the gathered scores to gather the distances from this score
                            rel += 100 - Math.abs(logopFor.getScore() - op.getScore()); // add the distance to the sum
                            clogops++; // add the number of distances
                        }
                        logop.setScore(op.getScore());
                        lops.add(logop); // add this to the list of scores
                        sum += op.getScore(); // add the score to the sum
                        counter++;
                    }
                }
            }
            int relevance = (int) ((float) rel / (float) clogops); // The relevance is the average of all the distances
            int score = (int) ((float) sum / ((float) counter)); // The score is the average of scores
            opinion.setScore(score);
            opinion.setRelevance(relevance);
            opinion.setLoginopinions(lops);
            em.getTransaction().rollback();
            em.close();
            //Returns object with score, relevance and a list over related logins with displayname and score
            return opinion; // this returns the object using xstream. The output is currently ugly. TODO: Make output look good using XStreamConverter.



            // New stuff
//            DomRepresentation representation = new DomRepresentation(
//                    MediaType.TEXT_XML);
//            // Generate a DOM document representing the item.
//            Document d = representation.getDocument();
//
//            Element eltItem = d.createElement("opinions");
//            eltItem.setAttribute("identifier", identifier);
//            eltItem.setAttribute("trustid", trustid);
//            eltItem.setAttribute("score", String.valueOf(score));
//
//            d.appendChild(eltItem);
//
//
//
//            d.normalizeDocument();
//
//            // Returns the XML representation of this document.
//            return representation;
        } catch (Exception ex) {
            Logger.getLogger(OpinionResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private Product getProduct(String identifier, String type) throws Exception {
        Query query = em.createQuery("SELECT p FROM Product p WHERE p.upccode = :identifier");
        query.setParameter("identifier", identifier);
        query.setMaxResults(1);
        List<Product> result = (List<Product>) query.getResultList();
        if (result == null || result.isEmpty()) {
            em.getTransaction().rollback();
            em.close();
            throw new Exception("Product not found");
        }
        Product product = result.get(0);
        return product;
    }
}
