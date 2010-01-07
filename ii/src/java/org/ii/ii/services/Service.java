/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import org.ii.ii.entities.Login;
import org.ii.ii.entities.Opinion;
import org.ii.ii.entities.Product;
import org.ii.ii.entities.Trust;
import org.ii.ii.exceptions.ArgumentInvalidFault;
import org.ii.ii.exceptions.AuthenticationFailedFault;
import org.ii.ii.exceptions.LoginNotFoundFault;
import org.ii.ii.exceptions.NotAuthenticatedFault;
import org.ii.ii.exceptions.OpinionNotFoundFault;
import org.ii.ii.exceptions.ProductNotFoundFault;
import org.ii.ii.serviceEntities.CalculatedOpinion;
import org.ii.ii.serviceEntities.LoginOpinion;
import org.ii.ii.serviceEntities.LoginW;
import org.ii.ii.utilities.EMF;

/**
 *
 * @author svenni
 */
@WebService()
public class Service {

    private EntityManager em;
    @Resource
    private WebServiceContext wsContext;

    public Service() {
        // some dummy data =D
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
     * Get an opinion based on the selected logins contained in the hashcode.
     */
    @WebMethod(operationName = "getOpinion")
    public CalculatedOpinion getOpinion(@WebParam(name = "identifier") String identifier, @WebParam(name = "type") String type, @WebParam(name = "trustid") Long trustid) throws ProductNotFoundFault {
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        CalculatedOpinion opinion = new CalculatedOpinion();
        Product product = getProduct(identifier, type);
        System.out.println("Found product " + product.getUpccode());
        // Finding the trust
        Query query2 = em.createQuery("SELECT t FROM Trust t WHERE t.id = :trustid");
        query2.setParameter("trustid", trustid);
        query2.setMaxResults(1);
        List<Trust> trusts = query2.getResultList();
        if (trusts == null || trusts.isEmpty()) {
            em.getTransaction().rollback();
            em.close();
            throw new ProductNotFoundFault("Trust not found");
        }
        Trust trust = trusts.get(0);
        System.out.println("Trust found " + trust.getId());
        int counter = 0;
        int sum = 0;
        int rel = 0;
        int clogops = 0;
        ArrayList<LoginOpinion> lops = new ArrayList<LoginOpinion>();
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
        return opinion;
    }

    /**
     * Get an opinion from a specified login on a specified product.
     */
    @WebMethod(operationName = "opinionFromLogin")
    public LoginOpinion getOpinionFromLogin(@WebParam(name = "identifier") String identifier, @WebParam(name = "type") String type, @WebParam(name = "loginid") Long loginid, @WebParam(name = "trustid") Long trustid) throws ProductNotFoundFault, LoginNotFoundFault, OpinionNotFoundFault {
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("SELECT p FROM Login p WHERE p.id = :loginid");
        query.setParameter("loginid", loginid);
        query.setMaxResults(1);
        List<Login> loginResult = query.getResultList();
        if (loginResult == null || loginResult.isEmpty()) {
            em.getTransaction().rollback();
            em.close();
            throw new LoginNotFoundFault("Login not found: " + loginid);
        }
        Login login = loginResult.get(0);
        LoginW loginw = new LoginW(login);
        LoginOpinion logop = new LoginOpinion(loginw);
        logop.setLogin(loginw);
        Product product = getProduct(identifier, type);
        boolean found = false;
        for (Opinion opinion : login.getOpinions()) {
            if (opinion.getProduct().equals(product)) {
                logop.setScore(opinion.getScore());
                found = true;
            }
        }
        if (!found) {
            throw new OpinionNotFoundFault("No opinion was found for this product and login");
        }
        em.getTransaction().rollback();
        em.close();
        //Returns object with score, description and displayname of login
        return logop;
    }

    /**
     * Register a list of logins to be trusted. A hashcode is returned.
     */
    @WebMethod(operationName = "registerLogins")
    public Long registerLogins(@WebParam(name = "loginids") List<Long> loginids) throws LoginNotFoundFault {
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        Trust trust = new Trust();
        em.persist(trust);
        for (Long loginid : loginids) {
            Query query = em.createQuery("SELECT p FROM Login p WHERE p.id = :loginid");
            query.setParameter("loginid", loginid);
            query.setMaxResults(1);
            List<Login> loginResult = query.getResultList();
            if (loginResult == null || loginResult.isEmpty()) {
                em.getTransaction().rollback();
                em.close();
                throw new LoginNotFoundFault("Login not found: " + loginid);
            }
            trust.getLogins().add(loginResult.get(0));
        }
        em.getTransaction().commit();
        em.close();
        System.out.println("Returning an ID: " + trust.getId());
        //Returns the generated ID
        return trust.getId();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "searchLogins")
    public List<LoginW> searchLogins(@WebParam(name = "query") String querystring) throws LoginNotFoundFault {
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("SELECT p FROM Login p WHERE UPPER(p.displayname) LIKE :querystring");
        query.setParameter("querystring", '%' + querystring.toUpperCase() + '%');
        List<Login> logins = query.getResultList();
        if (logins == null || logins.isEmpty()) {
            em.getTransaction().rollback();
            em.close();
            throw new LoginNotFoundFault("Login not found: " + querystring);
        }
        List<LoginW> loginws = new ArrayList<LoginW>();
        for (Login login : logins) {
            loginws.add(new LoginW(login));
        }
        em.getTransaction().commit();
        em.close();
        return loginws;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "addOpinion")
    public String addOpinion(@WebParam(name = "identifier") String identifier, @WebParam(name = "type") String type, @WebParam(name = "score") Integer score, @WebParam(name = "description") String description) throws NotAuthenticatedFault, LoginNotFoundFault, ProductNotFoundFault {
        MessageContext mc = wsContext.getMessageContext();
        HttpSession session = ((javax.servlet.http.HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST)).getSession();
        if (session == null) {
            throw new WebServiceException("No session in WebServiceContext");
        }
        if (session.getAttribute("loggedin") == null || !session.getAttribute("loggedin").equals("true")) {
            throw new NotAuthenticatedFault("Not authenticated");
        }
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        System.out.println("Finding login using: " + session.getAttribute("email") + " " + session.getAttribute("password"));
        Query query = em.createQuery("SELECT p FROM Login p WHERE p.email = :email AND p.password = :password");
        query.setParameter("email", session.getAttribute("email"));
        query.setParameter("password", session.getAttribute("password"));
        query.setMaxResults(1);
        List<Login> logins = query.getResultList();
        if (logins == null || logins.isEmpty()) {
            throw new LoginNotFoundFault("This is strange... The login was not found.");
        }
        Login login = logins.get(0);
        Opinion opinion = new Opinion();
        Product product = getProduct(identifier, type);
        opinion.setProduct(product);
        opinion.setDescription(description);
        opinion.setLogin(login);
        opinion.setScore(score);
        login.getOpinions().add(opinion);
        em.getTransaction().commit();
        em.close();
        return "Opinion added successfully";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "register")
    public String register(@WebParam(name = "email") String email, @WebParam(name = "password") String password, @WebParam(name = "displayname") String displayname) throws ArgumentInvalidFault {
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        Login login = new Login();
        em.persist(login);
        if (email == null || password == null || displayname == null) {
            throw new ArgumentInvalidFault("One of the arguments was null");
        }
        login.setEmail(email);
        login.setDisplayname(displayname);
        login.setPassword(password);
        em.getTransaction().commit();
        em.close();
        return "Registered";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "authenticate")
    public String authenticate(@WebParam(name = "email") String email, @WebParam(name = "password") String password) throws AuthenticationFailedFault {
        MessageContext mc = wsContext.getMessageContext();
        HttpSession session = ((javax.servlet.http.HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST)).getSession();
        if (session == null) {
            throw new WebServiceException("No session in WebServiceContext");
        }
        em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("SELECT p FROM Login p WHERE p.email = :email AND p.password = :password");
        query.setParameter("email", email);
        query.setParameter("password", password);
        query.setMaxResults(1);
        List<Login> logins = query.getResultList();
        if (logins == null || logins.isEmpty()) {
            throw new AuthenticationFailedFault("Authentication failed.");
        }
        Login login = logins.get(0);
        session.setAttribute("email", email);
        session.setAttribute("password", password);
        System.out.println("username: " + email + " password: " + password + " logged in");
        session.setAttribute("loggedin", "true");
        em.getTransaction().rollback();
        em.close();
        return "Authenticated OK";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "isAuthenticated")
    public String isAuthenticated() {
        MessageContext mc = wsContext.getMessageContext();
        HttpSession session = ((javax.servlet.http.HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST)).getSession();
        // Get a session property "username" from context
        if (session == null) {
            throw new WebServiceException("No session in WebServiceContext");
        }
        String loggedin = (String) session.getAttribute("loggedin");
        return loggedin;
    }

    private Product getProduct(String identifier, String type) throws ProductNotFoundFault {
        Query query = em.createQuery("SELECT p FROM Product p WHERE p.upccode = :identifier");
        query.setParameter("identifier", identifier);
        query.setMaxResults(1);
        List<Product> result = (List<Product>) query.getResultList();
        if (result == null || result.isEmpty()) {
            em.getTransaction().rollback();
            em.close();
            throw new ProductNotFoundFault("Product not found");
        }
        Product product = result.get(0);
        return product;
    }
}
