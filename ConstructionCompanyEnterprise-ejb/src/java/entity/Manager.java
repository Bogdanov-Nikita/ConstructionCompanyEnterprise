/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import Resources.R;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
//import utility.ErrorMsg;

/**
 *
 * @author nik
 */
@Entity
@Table(name = "\"MANAGER\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Manager.findAll", query = "SELECT m FROM Manager m"),
    @NamedQuery(name = "Manager.findById", query = "SELECT m FROM Manager m WHERE m.id = :id"),
    @NamedQuery(name = "Manager.findByName", query = "SELECT m FROM Manager m WHERE m.name = \':name\'"),
    @NamedQuery(name = "Manager.findByPhoneNumber", query = "SELECT m FROM Manager m WHERE m.phoneNumber = \':phoneNumber\'"),
    @NamedQuery(name = "Manager.findByOfficeAddress", query = "SELECT m FROM Manager m WHERE m.officeAddress = \':officeAddress\'")})
public class Manager implements Serializable, Converter {

    public final static int ESTIMATE_SUCCESS = 0x31;
    public final static int ESTIMATE_CLIENT_NEED_PAY = 0x32;                    //клиент должен оплатить приведущие задолженности.
    public final static int ESTIMATE_ERROR_CAN_NOT_ADD = 0x33;                  //невозможно добавление возможно смета пуста
    public final static int ESTIMATE_ERROR_CAN_NOT_SET_COAST = 0x34;            //Coast < 0
    
    public final static int STORAGE_NULL = 0x35;
    public final static int WORKLIST_NULL = 0x36;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"MANAGER_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"NAME\"", nullable = false, length = 255)
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 127)
    @Column(name = "\"PHONE_NUMBER\"", nullable = false, length = 127)
    private String phoneNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"OFFICE_ADDRESS\"", nullable = false, length = 255)
    private String officeAddress;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "managerId", fetch = FetchType.LAZY)
    private List<ConstructionOrder> constructionOrderList;

    public Manager() {
    }

    public Manager(Integer id) {
        this.id = id;
    }

    public Manager(Integer id, String name, String phoneNumber, String officeAddress) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.officeAddress = officeAddress;
    }

    public void CreateOrder(Date create, int Number, List<Work> workList, Client client, EntityManager em){
        ConstructionOrder order = new ConstructionOrder();
        order.setCreateDate(create);
        order.setUpdateDate((Date)create.clone());
        order.setNumber(Number);
        order.setStatus(ConstructionOrder.OPEN);
        order.setManagerId(this);
        order.setClientId(client);
        Estimate estimate = new Estimate();
        estimate.setType(Estimate.MAIN);
        estimate.setPaid(0);
        estimate.setWorkList(workList);
        em.persist(order);
        em.persist(estimate);
        em.merge(order);
        order.getEstimateList().add(estimate);
        em.merge(estimate);
        constructionOrderList.add(order);
        em.merge(constructionOrderList);
    }    
    
    public int CreateEstimate(int orderNumber, int type, List<Work> workList, EntityManager em){        
        Estimate e = new Estimate();
        e.setWorkList(workList);
        e.setType(type);
        e.setPaid(0);
        em.persist(e);
        em.merge(e);
        return (workList != null) ? 
                CreateEstimate(orderNumber,e,em) : 
                ESTIMATE_ERROR_CAN_NOT_ADD;
    }
    
    public int CreateEstimate(int orderNumber, Estimate part, EntityManager em){
        int flag = 0;
        switch(part.getType()){
            case Estimate.ADDITIONAL:
                //нужно оплатиить 85% или больше от текущей суммы заказа.
                if(constructionOrderList.get(orderNumber).getCurrentCoast() <= (constructionOrderList.get(orderNumber).CoastCalculation() * 0.15)){
                    if(constructionOrderList.get(orderNumber).getEstimateList().add(part)){
                        constructionOrderList.get(orderNumber).setStatus(ConstructionOrder.INPROGRESS);
                        flag = ESTIMATE_SUCCESS;
                    }else{
                        flag = ESTIMATE_ERROR_CAN_NOT_ADD;
                    }
                }else{
                    constructionOrderList.get(orderNumber).setStatus(ConstructionOrder.WAITING_PAY);
                    flag = ESTIMATE_CLIENT_NEED_PAY;
                }
                break;
            case Estimate.MAIN:
                if(constructionOrderList.get(orderNumber).getEstimateList().add(part)){
                    double coast = constructionOrderList.get(orderNumber).CoastCalculation();
                    if(coast > 0){
                        constructionOrderList.get(orderNumber).setCurrentCoast(coast);
                        constructionOrderList
                            .get(orderNumber)
                            .setStatus(ConstructionOrder.INPROGRESS);
                        flag = ESTIMATE_SUCCESS;
                    }else{
                    flag = ESTIMATE_ERROR_CAN_NOT_SET_COAST;
                    }
                }else{
                    flag = ESTIMATE_ERROR_CAN_NOT_ADD;
                }
                break;
        }
        em.merge(constructionOrderList);
        return flag;
    }
    
    
    /*public ArrayList<ErrorMsg> TakeResourseFromStorage(Storage store,List<Work> WorkList,List<Resource> ProcurementList){
        ArrayList<ErrorMsg> ErrorList = new ArrayList<>();
        //запрос ресурсов для работ.
        if(store != null){
            if(WorkList != null && !WorkList.isEmpty()){
                for (Work WorkList1 : WorkList) {
                    for (Resource resource1 : WorkList1.getResources()) {
                        int index = store.findResoursePositionByType(resource1.getType());
                        if (index != -1) {
                            switch (store.TakeResources(index, resource1.getAmount())) {
                                case Storage.TAKE_RESORSE_SUCCESS:
                                    //успешное выполнение
                                    break;
                                case Storage.INSUFFICIENTLY_RESORSE:
                                    //данного товара недостаточно
                                    int NeedAmount = resource1.getAmount() - store.getResource(index).getAmount();
                                    ProcurementList.add(new Resource(0,NeedAmount,
                                            store.getResource(index).getType(),
                                            store.getResource(index).getCoast(),
                                            store.getResource(index).getName()));
                                    break;
                                case Storage.RESORSE_EMPTY:
                                    //на склад данного ресурса не осталось
                                    ProcurementList.add(resource1);
                                    break;
                                case Storage.RESORSE_NOT_FOUND:
                                    //на складе такой тип ресурс не найден
                                    ErrorList.add(new ErrorMsg(Storage.RESORSE_NOT_FOUND, resource1.getType()));
                                    break;
                                case Storage.STORAGE_EMPTY:
                                    //проблема с инициализацией склада
                                    ErrorList.add(new ErrorMsg(Storage.STORAGE_EMPTY, -1));
                                    break;
                            }
                        }else{
                            ErrorList.add(new ErrorMsg(Storage.RESORSE_NOT_FOUND, -1));
                        }
                    }
                }
                /*if(!ProcurementList.isEmpty()){//Это действие (должно)может быть выполненно отдельно
                    int flag = SendResourseToStorage(store,ProcurementList);
                    if(flag != Storage.SEND_RESORSE_SUCCESS){
                        ErrorList.add(new ErrorMsg(flag, -1));
                    }
                }*//*
            }else{
                ErrorList.add(new ErrorMsg(WORKLIST_NULL, -1));
            }
        }else{
            ErrorList.add(new ErrorMsg(STORAGE_NULL, -1));
        }
        return ErrorList;
    }
    */
    
    /*
     * не забыть установить EntityManager em merge
     * @return - возвращает успех или тип ошибки. 
     */
    public int SendResourseToStorage(Storage store,List<Resource> resourseList){
        //отправка ресурсов на склад.
        int Flag;
        if(store != null){
            for (Resource elem : resourseList) {
                Flag = store.SendResources(elem.getResourceinformationId().getProductCode(),elem.getAmount());                
                if (Flag != Storage.SEND_RESORSE_SUCCESS) {
                    return Flag;
                }
            }
        }else{
            return STORAGE_NULL;
        }
        return Storage.SEND_RESORSE_SUCCESS;
    }
    
    /*
     * don't foget merge constructionorderlist
     */
    public boolean CloseOrder(boolean ClientAceptWork,int orderNumber,Date End){
        if(ClientAceptWork == true){
            if(constructionOrderList
                    .get(orderNumber)
                    .getStatus() == ConstructionOrder.WAITING_ACKNOWLEDGMENT_PAY){
                if(constructionOrderList
                    .get(orderNumber)
                    .getCurrentCoast() == 0){
                    constructionOrderList
                            .get(orderNumber)
                            .setStatus(ConstructionOrder.CLOSE);
                    return constructionOrderList
                            .get(orderNumber)
                            .CloseOrder(End);
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    @XmlTransient
    public List<ConstructionOrder> getConstructionOrderList() {
        return constructionOrderList;
    }

    public void setConstructionOrderList(List<ConstructionOrder> constructionOrderList) {
        this.constructionOrderList = constructionOrderList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Manager)) {
            return false;
        }
        Manager other = (Manager) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.Manager[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {      
        return Json
                .createBuilderFactory(null)
                .createObjectBuilder()
                .add("id", getId())
                .add("name", getName())
                .add("phone", getPhoneNumber())
                .add("address", getOfficeAddress());
    }

    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        String managerName;
        String phone;
        String address;
        if(obj.containsKey("name")){
            managerName = obj.get("name").toString();
            if(obj.containsKey("phone")){
                phone = obj.get("phone").toString();
                if(obj.containsKey("address")){
                    address = obj.get("address").toString();
                    if(managerName == null || managerName.trim().equals("")){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR, R.ErrMsg.NAME_ERROR).
                                build();
                    }
                    if(phone == null || phone.trim().equals("")){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR, R.ErrMsg.PHONE_ERROR)
                                .build();
                    }
                    if(address == null || address.trim().equals("")){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR, R.ErrMsg.ADDRESS_ERROR)
                                .build();
                    }
                    setName(managerName.substring(1, managerName.length()-1));
                    setPhoneNumber(phone.substring(1, phone.length()-1));
                    setOfficeAddress(address.substring(1, address.length()-1));
                    return factory.createObjectBuilder()
                            .add("info", "ok").build();
                }else{
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_1)
                            .build();
                }
            }else{
                return factory.createObjectBuilder()
                        .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_1)
                        .build();
            }
        }else{
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_1)
                    .build();
        }
    }
    
}
