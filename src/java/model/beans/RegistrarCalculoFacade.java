/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.beans;

import com.indra.demo.model.RegistrarCalculo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author lfcr0843
 */
@Stateless
public class RegistrarCalculoFacade extends AbstractFacade<RegistrarCalculo> {
    @PersistenceContext(unitName = "PruebaIndraWebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RegistrarCalculoFacade() {
        super(RegistrarCalculo.class);
    }
    
}
