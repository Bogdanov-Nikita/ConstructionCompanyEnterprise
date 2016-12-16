/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import Resources.R;
import entity.Client;
import entity.ConstructionOrder;
import entity.Converter;
import entity.Estimate;
import entity.Manager;
import entity.Master;
import entity.Resource;
import entity.ResourceItem;
import entity.Storage;
import entity.Work;
import entity.WorkItem;
import java.util.List;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.persistence.TransactionRequiredException;

/**
 *
 * @author nik
 */
@Stateless
public class ManagerBean  extends AbstractRole<Manager>  implements ManagerBeanRemote {

    public ManagerBean() {
        super(Manager.class);
    }
    
    @Override
    public JsonObject getAllByLogin(String login) {
        Manager m = getEntityByLogin(login);
        JsonBuilderFactory factory = Json.createBuilderFactory(null);        
        JsonArrayBuilder orderArray = factory.createArrayBuilder();
        //Order
        for(ConstructionOrder item : m.getConstructionOrderList()){
            orderArray = orderArray.add(item.toJSON());
        }
        //Storage
        JsonArrayBuilder storageArray = getArrayJSON("Storage.findAll",factory);
        //Masters
        JsonArrayBuilder masterArray = getArrayJSON("Master.findAll",factory);
        //Clients
        JsonArrayBuilder clientArray = getArrayJSON("Client.findAll",factory);
        //Works
        JsonArrayBuilder workArray = getArrayJSON("WorkItem.findAll",factory);
        //Resource
        JsonArrayBuilder resourceArray = getArrayJSON("Resource.findAll",factory);
        return m.toJSON()
                .add("login", login)
                .add(R.ArraysName.ORDER, orderArray)
                .add(R.ArraysName.STORAGE, storageArray)
                .add(R.ArraysName.WORK, workArray)
                .add(R.ArraysName.RESOURCE, resourceArray)
                .add(R.ArraysName.MASTER, masterArray)
                .add(R.ArraysName.CLIENT, clientArray)
                .build();
    }
    
    <T extends Converter> JsonArrayBuilder getArrayJSON(String namedQuery, JsonBuilderFactory factory){
        JsonArrayBuilder array = factory.createArrayBuilder();
        List<T> objectList = em.createNamedQuery(namedQuery).getResultList();
        for(T item : objectList){
            array = array.add(item.toJSON());
        }
        return array;
    }
    
    <T extends Converter> JsonObject getArrayJSON(String namedQuery, String arrayName, JsonBuilderFactory factory){
        return factory.createObjectBuilder()
                .add(arrayName,getArrayJSON(namedQuery,factory))
                .build();
    }

    <T extends Converter> JsonObject getArrayElement(int Id, String namedQuery, String arrayName, JsonBuilderFactory factory){
    List<T> list = em.createNamedQuery(namedQuery).getResultList();
        for(T item : list){
            if(item.getId() == Id){
                return item.toJSON().build();
            }
        }
        return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                .build();
    }
    
    <T extends Converter> JsonObject add(T entity, JsonObject obj){
        JsonObject answer;
        answer = entity.fromJSON(obj, em);
        em.persist(entity);
        return answer;
    }
    
    /**
     * подходит не для всех позиций: не для order по остальным надо проверить
     */
    <T extends Converter> JsonObject edit(Class<T> className, int id , JsonObject obj){
        T entity = em.find(className, id);
        if(entity == null){
            return Json
                    .createBuilderFactory(null)
                    .createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_ID)
                    .build();
        }else{
            JsonObject answer;
            answer = entity.fromJSON(obj, em);
            em.merge(entity);
            return answer;
        }
    }
    
    <T extends Object> JsonObject delete(T entity,JsonBuilderFactory factory){
        try{
            em.remove(em.merge(entity));
        }catch(IllegalArgumentException|TransactionRequiredException e){
            return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR,R.ErrMsg.DELETE_ERROR)
                .build();
        }
        return factory.createObjectBuilder()
                .add(R.InfoMsg.INFO,R.InfoMsg.SUCSESS)
                .build();
    }

    @Override
    public JsonObject getArrayByName(String login, String arrayName) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        switch(arrayName){
            case R.ArraysName.ORDER:
                //частичный набор для полного нужно обратится к одному элементу
                return getArrayJSON("ConstructionOrder.findAll",R.ArraysName.ORDER,factory);
            case R.ArraysName.STORAGE:
                //Storage с ресурсами
                return getArrayJSON("Storage.findAll",R.ArraysName.STORAGE,factory);
            case R.ArraysName.WORK :
                //Work с ресурсами
                return getArrayJSON("Work.findAll",R.ArraysName.WORK,factory);
            case R.ArraysName.RESOURCE:
                //RESOURCE
                return getArrayJSON("Resource.findAll",R.ArraysName.RESOURCE,factory);
            case R.ArraysName.ESTIMATE:
                //ESTIMATE
                 return getArrayJSON("Estimate.findAll",R.ArraysName.ESTIMATE,factory);
            case R.ArraysName.MANAGER:
                //MANAGER
                return getArrayJSON("Manager.findAll",R.ArraysName.MANAGER,factory);
            case R.ArraysName.CLIENT:
                //CLIENT
                return getArrayJSON("Client.findAll",R.ArraysName.CLIENT,factory);
            case R.ArraysName.MASTER:                
                //MASTER
                return getArrayJSON("Master.findAll",R.ArraysName.MASTER,factory);
            default :
                   return factory.createObjectBuilder()
                        .add(R.ErrMsg.ERROR,R.ErrMsg.ARRAY_ERROR)
                        .build();
        }
    }
    
    @Override
    public JsonObject setArrayElementById(String login, String arrayName, String id, JsonObject obj) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);//TODO: заполнить тут
        int Id;
        try{
            Id = Integer.parseInt(id);
        }catch(NumberFormatException e){
            Id = -1;
        }
        if(Id >0){
            switch(arrayName){
                    case R.ArraysName.ORDER:
                        //TODO: нужно заменить внутренную часть т.к. для редактирования нужны другие данные
                        return edit(ConstructionOrder.class,Id,obj);
                    case R.ArraysName.ESTIMATE:
                        return edit(Estimate.class,Id,obj);
                    case R.ArraysName.STORAGE:
                        return edit(Storage.class,Id,obj);
                    case R.ArraysName.WORK:
                        return edit(WorkItem.class,Id,obj);
                    case R.ArraysName.ESTIMATE_WORK:
                        return edit(Work.class,Id,obj);
                    case R.ArraysName.RESOURCE:
                        return edit(Resource.class,Id,obj);
                    case R.ArraysName.RESOURCE_ITEM:
                        return edit(ResourceItem.class,Id,obj);
                    case R.ArraysName.MANAGER:
                        return edit(Manager.class,Id,obj);
                    case R.ArraysName.MASTER:
                        return edit(Master.class,Id,obj);
                    case R.ArraysName.CLIENT:
                        return edit(Client.class,Id,obj);
                    default:
                        return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1)
                            .build();
            }
        }else{
            return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                .build();
        }
    }

    @Override
    public JsonObject addArrayElement(String login, String arrayName, JsonObject obj) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        switch(arrayName){//TODO: продолжить тут, последнее редактирование
                case R.ArraysName.ORDER://заказ без всего
                    return add(new ConstructionOrder(),obj);
                case R.ArraysName.ESTIMATE://смета без всего только с номером заказа
                    return add(new Estimate(),obj);
                case R.ArraysName.STORAGE://включая id ресурсы
                    return add(new Storage(),obj);
                case R.ArraysName.WORK://создание описание работы не привязана к смете
                    return add(new WorkItem(),obj);
                case R.ArraysName.ESTIMATE_WORK://создание работы для сметы
                    return add(new Work(),obj);
                case R.ArraysName.RESOURCE://ресурс без указания где он находится
                    return add(new Resource(),obj);
                case R.ArraysName.RESOURCE_ITEM://только описание ресурса
                    return add(new ResourceItem(),obj);
                case R.ArraysName.MANAGER:
                    return add(new Manager(),obj);
                case R.ArraysName.MASTER:
                    return add(new Master(),obj);
                case R.ArraysName.CLIENT:
                    return add(new Client(),obj);
                default:
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1)
                            .build();
        }
    }
    //TODO: подумать как можно модифицировать этот метод испольуя функцию delete
    @Override
    public JsonObject deleteArrayElementById(String login, String arrayName, String id) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        int Id, OrderId = -1,EstimateId = -1;
        if(arrayName.equals(R.ArraysName.ESTIMATE)){
            String array[] = id.split("&");
            if(array.length == 2){
                try{
                    Id = Integer.parseInt(array[0]);
                    OrderId = Integer.parseInt(array[1]);
                }catch(NumberFormatException e){
                    Id = -1;
                }
            }else{Id = -1;}
        }else{
            if(arrayName.equals(R.ArraysName.WORK)){
                String array[] = id.split("&");
                if(array.length == 3){
                    try{
                        Id = Integer.parseInt(array[0]);
                        OrderId = Integer.parseInt(array[1]);
                        EstimateId = Integer.parseInt(array[2]);
                    }catch(NumberFormatException e){
                        Id = -1;
                    }
                }else{Id = -1;}
            }else{
                try{
                    Id = Integer.parseInt(id);
                }catch(NumberFormatException e){
                    Id = -1;
                }
            }
        }
        if(Id > 0){
            Manager m = getEntityByLogin(login);
            switch(arrayName){
                    case R.ArraysName.ORDER://только принадлежащие менеджеру
                        for(int i = 0; i < m.getConstructionOrderList().size(); i++){
                            if(m.getConstructionOrderList().get(i).getId() == Id){
                                delete(m.getConstructionOrderList().get(i),factory);
                            }
                        }
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.NOT_CORRECT_ID_OR_ACSESS_ERROR)
                                .build();
                    case R.ArraysName.ESTIMATE:
                        //только принадлежащие менеджеру
                        if(OrderId > 0){
                            for (int i = 0; i < m.getConstructionOrderList().size(); i++){
                                if(m.getConstructionOrderList().get(i).getId() == OrderId){
                                    for(int j = 0; j < m.getConstructionOrderList().get(i).getEstimateList().size(); j++){
                                        if(m.getConstructionOrderList().get(i).getEstimateList().get(j).getId() == Id){
                                            return delete(m.getConstructionOrderList().get(i).getEstimateList().get(j),factory);
                                        }
                                    }
                                }
                            }
                            return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.NOT_CORRECT_ID_OR_ACSESS_ERROR)
                                .build();
                        }else{
                            return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                        }
                    case R.ArraysName.WORK:
                        //need estimate and order id//только принадлежащие менеджеру
                        if(OrderId > 0 && EstimateId > 0){
                            for (int i = 0; i < m.getConstructionOrderList().size(); i++){
                                if(m.getConstructionOrderList().get(i).getId() == OrderId){
                                    for(int j = 0; j < m.getConstructionOrderList().get(i).getEstimateList().size(); j++){
                                        if(m.getConstructionOrderList().get(i).getEstimateList().get(j).getId() == EstimateId){
                                            for(int k = 0 ; k < m.getConstructionOrderList().get(i).getEstimateList().get(j).getWorkList().size(); k++){
                                                if(m.getConstructionOrderList().get(i).getEstimateList().get(j).getWorkList().get(k).getId() == Id ){
                                                    return delete(m.getConstructionOrderList().get(i).getEstimateList().get(j).getWorkList().get(k),factory);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.NOT_CORRECT_ID_OR_ACSESS_ERROR)
                                .build();
                        }else{
                            return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                        }
                    case R.ArraysName.STORAGE:
                        List<Storage> storages = em.createNamedQuery("Storage.findAll")
                                .getResultList();
                        for(int i = 0; i < storages.size(); i++){
                            if(storages.get(i).getId() == Id){
                                return delete(storages.get(i),factory);                                
                            }
                        }
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                    case R.ArraysName.RESOURCE:
                        List<ResourceItem> resources = em.createNamedQuery("ResourceItem.findAll")
                                .getResultList();
                        for(int i = 0; i < resources.size(); i++){
                            if(resources.get(i).getId() == Id){
                                return delete(resources.get(i),factory);                                
                            }
                        }
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                    case R.ArraysName.MANAGER:
                        List<Manager> managers = em.createNamedQuery("Manager.findAll")
                                .getResultList();
                        for(int i = 0; i < managers.size(); i++){
                            if(managers.get(i).getId() == Id){
                                return delete(managers.get(i),factory);                                
                            }
                        }
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                    case R.ArraysName.MASTER:
                        List<Master> masters = em.createNamedQuery("Master.findAll")
                                .getResultList();
                        for(int i = 0; i < masters.size(); i++){
                            if(masters.get(i).getId() == Id){
                                return delete(masters.get(i),factory);                                
                            }
                        }
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                    case R.ArraysName.CLIENT:
                        List<Client> clients = em.createNamedQuery("Client.findAll")
                                .getResultList();
                        for(int i = 0; i < clients.size(); i++){
                            if(clients.get(i).getId() == Id){
                                return delete(clients.get(i),factory);                                
                            }
                        }
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                    default:
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
            }
        }else{
            return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                .build();
        }
    }
    
    @Override
    public JsonObject getArrayElementById(String login, String arrayName, String id) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        int Id;
        try{
            Id = Integer.parseInt(id);
        }catch(NumberFormatException e){
            Id = -1;
        }
        if(Id > 0){
            switch(arrayName){
                case R.ArraysName.ORDER:
                    //Заказы с всеме элементами
                    return getArrayElement(Id,"ConstructionOrder.findAll",R.ArraysName.ORDER,factory);
                case R.ArraysName.STORAGE:
                    //Storage с ресурсами
                    return getArrayElement(Id,"Storage.findAll",R.ArraysName.STORAGE,factory);
                case R.ArraysName.WORK:
                    //Work с ресурсами
                    return getArrayElement(Id,"Work.findAll",R.ArraysName.WORK,factory);
                case R.ArraysName.RESOURCE:
                    //ресурсы из таблицы Resourceinformation, но не количество
                    return getArrayElement(Id,"Resource.findAll",R.ArraysName.RESOURCE,factory);
                case R.ArraysName.ESTIMATE:
                    //только записи из таблицы Estimate и некоторые из Order
                    return getArrayElement(Id,"Estimate.findAll",R.ArraysName.ESTIMATE,factory);
                case R.ArraysName.MANAGER:
                    //Manager
                    return getArrayElement(Id,"Manager.findAll",R.ArraysName.MANAGER,factory);
                case R.ArraysName.CLIENT:
                    //Clients
                    return getArrayElement(Id,"Client.findAll",R.ArraysName.CLIENT,factory);
                case R.ArraysName.MASTER:                
                    //Masters
                    return getArrayElement(Id,"Master.findAll",R.ArraysName.MASTER,factory);
                default :
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR,R.ErrMsg.ARRAY_ERROR)
                            .build();
            }
        }else{
            return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                .build();
        }
    }

}
