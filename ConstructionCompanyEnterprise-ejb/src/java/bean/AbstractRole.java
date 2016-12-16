/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import entity.Converter;
import entity.Users;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author nik
 * @param <T> - entity class
 */

public abstract class AbstractRole<T extends Converter>{
    
    @PersistenceContext
    EntityManager em;
    private final Class<T> entityClass;
    
    public AbstractRole(Class<T> entityClass) {      
        this.entityClass = entityClass;
    }
    
    public T getEntityByLogin(String login) {
        Users user =
        (Users)em.createNamedQuery("Users.findByLogin")
                .setParameter("login", login)
                .getSingleResult();
        return em.find(entityClass,user.getRoleId());
    }
    
    public JsonObject setProfileByLogin(String login, JsonObject obj) {
        T m = getEntityByLogin(login);
        JsonObject ans = m.fromJSON(obj,em);
        em.merge(m);
        return ans;
    }
    
    public JsonObject getProfileByLogin(String login){
        return getEntityByLogin(login)
                .toJSON()
                .add("login", login)
                .build();
    }
    
}
