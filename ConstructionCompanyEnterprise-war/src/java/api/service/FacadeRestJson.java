/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.service;

import Resources.R;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import bean.BeanRemote;

/**
 * @author nik
 */

public class FacadeRestJson{
    
    BeanRemote bean;

    public FacadeRestJson(String name) {
        try{
            //Look up an object
            bean = ((BeanRemote)new InitialContext()
                .lookup(name));          
        }catch(NamingException e){
            bean = null;
        }
    }
    
    public String getAllByLogin(String login){
        return ((bean != null) ? bean.getAllByLogin(login): 
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build())
                .toString();
    }
    
    public String getProfileByLogin(String login){
        return ((bean != null) ? bean.getProfileByLogin(login):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build())
                .toString();
    }
    
    public JsonObject setProfileByLogin(String login, String content){
        JsonObject obj = Json.createReader(new StringReader(content)).readObject();
        return ((obj != null ) ?
                ((bean != null) ? bean.setProfileByLogin(login,obj):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.INPUT_DATA_ERROR_1).build());
    }
    
    public String getArrayByName(String login, String arrayName){
        return ((arrayName != null) ? 
                ((bean != null) ? 
                bean.getArrayByName(login, arrayName):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.ARRAY_ERROR).build()).toString();
    }
    
    public JsonObject getArrayElementById(String login, String arrayName, String id){
        return ((id != null ) ?
                ((arrayName != null) ? 
                ((bean != null) ? 
                bean.getArrayElementById(login, arrayName,id):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.ARRAY_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.INPUT_DATA_ERROR_1).build());
    }
    
    public JsonObject addArrayElement(String login, String arrayName, String content){
        JsonObject obj = Json.createReader(new StringReader(content)).readObject();
        return ((obj != null ) ?
                ((arrayName != null) ? 
                ((bean != null) ? 
                bean.addArrayElement(login, arrayName,obj):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.ARRAY_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.INPUT_DATA_ERROR_1).build());
    }
    
    public JsonObject setArrayElementById(String login, String arrayName, String id, String content){
        JsonObject obj = Json.createReader(new StringReader(content)).readObject();
        return ((id != null) ?
                ((obj != null ) ?
                ((arrayName != null) ? 
                ((bean != null) ? 
                bean.setArrayElementById(login, arrayName, id, obj):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.ARRAY_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.INPUT_DATA_ERROR_1).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.INPUT_DATA_ERROR_ID).build());
    }
    
    public JsonObject deleteArrayElementById(String login,String arrayName,String id){
        return ((id != null) ?
                ((arrayName != null) ? 
                ((bean != null) ?
                bean.deleteArrayElementById(login, arrayName, id):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.BEAN_CONNECTION_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.ARRAY_ERROR).build()):
                Json.createBuilderFactory(null).createObjectBuilder()
                .add("error", R.ErrMsg.INPUT_DATA_ERROR_ID).build());
    }
    
}
