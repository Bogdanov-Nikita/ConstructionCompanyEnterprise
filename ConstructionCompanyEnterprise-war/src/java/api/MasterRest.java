/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import api.service.FacadeRestJson;
import javax.json.JsonObject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author nik
 */
@Path("master")
public class MasterRest {

    FacadeRestJson beanRestFasad;
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MasterRest
     */
    public MasterRest() {
        beanRestFasad = new FacadeRestJson("bean.MasterBeanRemote");
    }

    /**
     * @param sc for login user name
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context SecurityContext sc) {
        return beanRestFasad.getAllByLogin(
                sc.getUserPrincipal().getName());
    }

    @GET
    @Path("profile")
    @Produces(MediaType.APPLICATION_JSON)
    public String getProfile(@Context SecurityContext sc){
        return beanRestFasad.getProfileByLogin(
                sc.getUserPrincipal().getName());
    }
    
    @GET
    @Path("array/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getArrayByName(
            @Context SecurityContext sc,
            @PathParam("name") String name) {
        return beanRestFasad.getArrayByName(
                sc.getUserPrincipal().getName(), name);
    }
    
    @GET
    @Path("array/{name}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getArrayElementById(
            @Context SecurityContext sc,
            @PathParam("name") String name, 
            @PathParam("id") String id) {
        return beanRestFasad.getArrayElementById(
                sc.getUserPrincipal().getName(), name, id);
    }  

    /**
     * PUT method for updating or creating an instance of ClienRest
     * @param sc for login user name
     * @param content representation for the resource
     * @return json object
     */
    @PUT
    @Path("profile")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject putProfile(
            @Context SecurityContext sc,
            String content) {
        return beanRestFasad.setProfileByLogin(
                sc.getUserPrincipal().getName(), content);
    }
    
    /*@PUT
    @Path("array/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject addArrayElementById(
            @Context SecurityContext sc,
            String content,
            @PathParam("name") String name, 
            @PathParam("id") String id) {
        return beanRestFasad.addArrayElement(
                sc.getUserPrincipal().getName(), name, content);
        
    }*/
    
    @PUT
    @Path("array/{name}/{id}/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject putArrayElementById(
            @Context SecurityContext sc,
            String content,
            @PathParam("name") String name, 
            @PathParam("id") String id) {
        return beanRestFasad.setArrayElementById(
                sc.getUserPrincipal().getName(), name,id, content);
    }
    
    
    /*@DELETE
    @Path("array/{name}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject deleteArrayElementById(
            @Context SecurityContext sc,
            @PathParam("name") String name, 
            @PathParam("id") String id) {
        return beanRestFasad.deleteArrayElementById(
                sc.getUserPrincipal().getName(), name,id);
    }*/

}
