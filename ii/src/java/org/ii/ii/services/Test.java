/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ii.ii.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.ii.ii.entities.Login;
import org.ii.ii.entities.Opinion;
import org.ii.ii.entities.Product;
import org.ii.ii.utilities.EMF;

/**
 *
 * @author svenni
 */
@WebService()
public class Test {

    /**
     * Web service operation
     * @param a
     * @param b
     * @return
     */
    @WebMethod(operationName = "tester")
    public String tester(@WebParam(name = "a") int a, @WebParam(name = "b") int b) {
        EntityManager entityManager;
        entityManager = EMF.get().createEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("SELECT u FROM Login u WHERE u.displayname = :email");
        query.setParameter("email", "hei");
        query.setMaxResults(1);
        boolean existed = false;
        if(query.getResultList() != null && !query.getResultList().isEmpty()) {
            existed = true;
        }
        Login login = new Login();
        entityManager.persist(login);
        login.setDisplayname("hei");
        Product product = new Product();
        entityManager.persist(product);
        product.setDescription("Nytt produkt");
        Opinion opinion = new Opinion();
        entityManager.persist(opinion);
        opinion.setProduct(product);
        opinion.setLogin(login);
        opinion.setScore(200);
        int sum = a + b;
        entityManager.getTransaction().commit();
        return "The sum is " + sum + " and " + existed;
    }
}
