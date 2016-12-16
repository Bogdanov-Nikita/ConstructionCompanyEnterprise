/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import javax.json.JsonObject;

/**
 *
 * @author nik
 */
public interface BeanRemote {
    public JsonObject getAllByLogin(String login);
    public JsonObject getProfileByLogin(String login);
    public JsonObject setProfileByLogin(String login, JsonObject obj);  
    public JsonObject getArrayByName(String login,String arrayName);
    public JsonObject getArrayElementById(String login,String arrayName, String id);
    public JsonObject addArrayElement(String login,String arrayName, JsonObject obj);
    public JsonObject setArrayElementById(String login,String arrayName,String id, JsonObject obj);
    public JsonObject deleteArrayElementById(String login,String arrayName,String id);
}
