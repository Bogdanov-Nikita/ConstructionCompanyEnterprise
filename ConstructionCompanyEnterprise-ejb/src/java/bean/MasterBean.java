/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import Resources.R;
import entity.Master;
import entity.Work;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

/**
 *
 * @author nik
 */
@Stateless
public class MasterBean  extends AbstractRole<Master>  implements MasterBeanRemote {

    public MasterBean() {
        super(Master.class);
    }
    
    @Override
    public JsonObject getAllByLogin(String login) {
        Master m = getEntityByLogin(login);
        JsonBuilderFactory factory = Json.createBuilderFactory(null);        
        JsonArrayBuilder workArray = factory.createArrayBuilder();
        for(Work item: m.getWorkList()){
            workArray = workArray.add(item.toJSON());
        }
        return m.toJSON()
                .add("login", login)
                .add(R.ArraysName.WORK, workArray).build(); 
    }
    
    @Override
    public JsonObject getArrayByName(String login, String arrayName) {
        Master m = getEntityByLogin(login);
        JsonBuilderFactory factory = Json.createBuilderFactory(null);        
        if(arrayName.equals(R.ArraysName.WORK)){
            JsonArrayBuilder workArray = factory.createArrayBuilder();
                for(Work item: m.getWorkList()){
                    workArray = workArray.add(item.toJSON());
                }
            return factory.createObjectBuilder()
                    .add(R.ArraysName.WORK,workArray).build();
        }else{
            return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR, R.ErrMsg.ARRAY_ERROR).build();
        }
    }

    @Override
    public JsonObject setArrayElementById(String login, String arrayName, String id, JsonObject obj) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        if(arrayName.equals(R.ArraysName.WORK)){
            if(obj.containsKey("action")){
                String action = obj.get("action").toString();
                action = action.substring(1, action.length()-1);
                if(action.equals("WorkFinish")){
                    int workId;
                    try{
                        workId = Integer.parseInt(id);
                    }catch(NumberFormatException e){
                        workId = -1;
                    }
                    if(workId > 0){
                        Master m = getEntityByLogin(login);
                        for(int i = 0; i < m.getWorkList().size(); i++){
                            if(m.getWorkList().get(i).getId() == workId){
                                if(m.FinishWork(i)){
                                    em.merge(m);
                                    return factory.createObjectBuilder()
                                            .add(R.InfoMsg.INFO,R.InfoMsg.SUCSESS).build();
                                }else{
                                    return factory.createObjectBuilder()
                                            .add(R.ErrMsg.ERROR,R.ErrMsg.TAKE_WORK_ERROR).build();
                                }
                            }
                        }
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.RECORD_NOT_FOUND).build();
                    }else{
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID).build();
                    }
                }else{
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1).build();
                }                
            }else{
                return factory.createObjectBuilder()
                        .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1).build();
            }
        }else{
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.ARRAY_ERROR).build();
        }
    }

    @Override
    public JsonObject getArrayElementById(String login, String arrayName, String id) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        if(arrayName.equals(R.ArraysName.WORK)){
            Master m = getEntityByLogin(login);//action
            int Id;
            try{
                Id = Integer.parseInt(id);
            }catch(NumberFormatException e){
                Id = -1;
            }
            if(Id > 0){
                for(Work item: m.getWorkList()){
                    if(item.getId() == Id){
                        return item.toJSON().build();
                    }
                }
            }
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                    .build();
        }else{        
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                    .build();
        }
    }
    
    /**
     * Такой операции нету у данной роли.
     * @param login
     * @param arrayName
     * @param obj
     * @return 
     */
    @Override
    public JsonObject addArrayElement(String login, String arrayName, JsonObject obj) {
        throw new UnsupportedOperationException(R.ErrMsg.UNSUPPORT_EXEPTION);
    }

    /**
     * Такой операции нету у данной роли.
     * @param login
     * @param arrayName
     * @param id
     * @return 
     */
    @Override
    public JsonObject deleteArrayElementById(String login, String arrayName, String id) {
        throw new UnsupportedOperationException(R.ErrMsg.UNSUPPORT_EXEPTION);
    }
    
}
