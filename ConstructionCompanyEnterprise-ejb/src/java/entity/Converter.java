/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;

/**
 *
 * @author nik
 */
public interface Converter {
    public Integer getId();
    public JsonObjectBuilder toJSON();
    public JsonObject fromJSON(JsonObject obj, EntityManager em);
}
