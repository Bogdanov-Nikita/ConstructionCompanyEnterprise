/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import Resources.R;
import entity.Client;
import entity.ConstructionOrder;
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
public class ClientBean extends AbstractRole<Client> implements ClientBeanRemote {
        
    public ClientBean() {
        super(Client.class);
    }
    
    @Override
    public JsonObject getAllByLogin(String login) {
        Client cl = getEntityByLogin(login);
        JsonBuilderFactory factory = Json.createBuilderFactory(null);        
        JsonArrayBuilder orderArray = factory.createArrayBuilder();
        for(ConstructionOrder item : cl.getConstructionOrderList()){
            orderArray = orderArray.add(item.toJSON());
        }
        return cl.toJSON()
                .add("login", login)
                .add(R.ArraysName.ORDER, orderArray)
                .build();
    }
    
    @Override
    public JsonObject getArrayByName(String login, String arrayName) {
        Client cl = getEntityByLogin(login);
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        if(arrayName.equals(R.ArraysName.ORDER)){        
            JsonArrayBuilder orderArray = factory.createArrayBuilder();
            for(ConstructionOrder item: cl.getConstructionOrderList()){
                orderArray = orderArray.add(item.toJSON());
            }
            return factory.createObjectBuilder()
                    .add(R.ArraysName.ORDER,orderArray)
                    .build();
        }else{
            return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR, R.ErrMsg.ARRAY_ERROR).build();
        }
    }

    @Override
    public JsonObject setArrayElementById(String login, String arrayName, String id, JsonObject obj) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        if(arrayName.equals(R.ArraysName.ORDER)){
            String action;
            String estimateIdStr;
            String payStr;
            int orderId;
            int estimateId;
            double pay;
            
            Client cl = getEntityByLogin(login);
            try{
                orderId = Integer.parseInt(id);
            }catch(NumberFormatException e){
                return factory.createObjectBuilder()
                        .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID).build();
            }
            if(orderId > 0){
                if(obj.containsKey("action")){
                    action = obj.get("action").toString();
                    action = action.substring(1, action.length()-1);
                    switch(action){
                        case "payEstimate":
                            if(obj.containsKey("estimateId")){
                                estimateIdStr = obj.getString("estimateId");
                                if(obj.containsKey("pay")){
                                    payStr = obj.getString("pay");
                                    try{
                                        pay = Double.parseDouble(payStr);
                                        estimateId = Integer.parseInt(estimateIdStr);
                                    }catch(NumberFormatException e){
                                        return factory.createObjectBuilder()
                                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID).build();
                                    }
                                    if(pay <= 0){
                                        return factory.createObjectBuilder()
                                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_3).build();
                                    }
                                    if(estimateId <= 0){
                                        return factory.createObjectBuilder()
                                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID).build();
                                    }
                                    for(int i = 0; i < cl.getConstructionOrderList().size(); i++){
                                        if(cl.getConstructionOrderList().get(i).getId() == orderId){
                                            if(cl.PayEstimatePart(i,pay)){
                                                em.merge(cl);
                                                return factory.createObjectBuilder()
                                                        .add(R.InfoMsg.INFO,R.InfoMsg.SUCSESS).build();
                                            }else{
                                                return factory.createObjectBuilder()
                                                        .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_PAY_ERROR).build();
                                            }
                                        }
                                    }
                                    //ошибка нет записи
                                    return factory.createObjectBuilder()
                                            .add(R.ErrMsg.ERROR,R.ErrMsg.RECORD_NOT_FOUND).build();
                                }else{
                                    return factory.createObjectBuilder()
                                            .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1).build();
                                }
                            }else{
                                return factory.createObjectBuilder()
                                    .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1).build();
                            }
                        case "takeWork":
                            for(int i = 0; i < cl.getConstructionOrderList().size(); i++){
                                if(cl.getConstructionOrderList().get(i).getId() == orderId){
                                    if(cl.TakeWork(i)){
                                        em.merge(cl);
                                        return factory.createObjectBuilder()
                                                .add(R.InfoMsg.INFO,R.InfoMsg.SUCSESS).build();
                                    }else{
                                        return factory.createObjectBuilder()
                                                .add(R.ErrMsg.ERROR,R.ErrMsg.TAKE_WORK_ERROR).build();
                                    }
                                }
                            }
                            //ошибка нет записи
                            return factory.createObjectBuilder()
                                    .add(R.ErrMsg.ERROR,R.ErrMsg.RECORD_NOT_FOUND).build();
                        default:
                            return factory.createObjectBuilder()
                                    .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1).build();
                    }
                }else{
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1).build();           
                }
            }else{
                return factory.createObjectBuilder()
                        .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID).build();
            }
        }else{
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.ARRAY_ERROR).build();
        }
    }
    
    @Override
    public JsonObject getArrayElementById(String login, String arrayName, String id) {//подробней по работам и сметам
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        if(arrayName.equals(R.ArraysName.ORDER)){
            Client cl = getEntityByLogin(login);
            int Id;
            try{
                Id = Integer.parseInt(id);
            }catch(NumberFormatException e){
                Id = -1;
            }
            if(Id > 0){
                for(ConstructionOrder item: cl.getConstructionOrderList()){
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
