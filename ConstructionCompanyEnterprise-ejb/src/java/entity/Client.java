/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import Resources.R;
import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nik
 */
@Entity
@Table(name = "\"CLIENT\"",  schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c"),
    @NamedQuery(name = "Client.findById", query = "SELECT c FROM Client c WHERE c.id = :id"),
    @NamedQuery(name = "Client.findByName", query = "SELECT c FROM Client c WHERE c.name = \':name\'"),
    @NamedQuery(name = "Client.findByPhoneNumber", query = "SELECT c FROM Client c WHERE c.phoneNumber = \':phoneNumber\'"),
    @NamedQuery(name = "Client.findByType", query = "SELECT c FROM Client c WHERE c.type = :type"),
    @NamedQuery(name = "Client.findByAddres", query = "SELECT c FROM Client c WHERE c.addres = \':addres\'")})
public class Client implements Serializable, Converter {
    
    public final static int PHYSICAL = 0x1;
    public final static int LEGAL = 0x2;
    
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "CLIENT_ID", sequenceName = "public.\"CLIENT_ID_seq\"", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="CLIENT_ID")
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
    @Column(name = "\"TYPE\"", nullable = false)
    private int type;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"ADDRES\"", nullable = false, length = 255)
    private String addres;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientId", fetch = FetchType.LAZY)
    private List<ConstructionOrder> constructionOrderList;

    public Client() {
    }

    public Client(Integer id) {
        this.id = id;
    }

    public Client(Integer id, String name, String phoneNumber, int type, String addres) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.addres = addres;
    }

    //клиент принимает работу прораба
    public boolean TakeWork(int OrderNumber){
        if(OrderNumber > 0){
            return constructionOrderList.get(OrderNumber).ClientAcceptanceOrder();
        }else{
            return false;//у нас нет несуществующих заказов
        }
    }
    
    //процесс по оплате должен быть подтверждён менеджером
    public boolean PayEstimatePart(int OrderNumber, double pay){
        return constructionOrderList.get(OrderNumber)
                .ClientPay(pay);
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
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
        if (!(object instanceof Client)) {
            return false;
        }
        Client other = (Client) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.Client[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {     
        return Json
                .createBuilderFactory(null)
                .createObjectBuilder()
                .add("id", getId())
                .add("name", getName())
                .add("phone", getPhoneNumber())
                .add("address",getAddres())
                .add("type", R.Client.ClientTypeName(getType()));
    }
    
    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        String clientName;
        String phone;
        String address;
        int clientType;
        if(obj.containsKey("name")){
            clientName = obj.get("name").toString();
            if(obj.containsKey("phone")){
                phone = obj.get("phone").toString();
                if(obj.containsKey("address")){
                    address = obj.get("address").toString();
                    if(obj.containsKey("type")){
                        clientType = R.Client.ClientNameType(obj.getString("type"));
                        if(clientName == null || clientName.trim().equals("")){
                            return factory.createObjectBuilder()
                                    .add(R.ErrMsg.ERROR, R.ErrMsg.NAME_ERROR)
                                    .build();
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
                        if(!(clientType == LEGAL || clientType == PHYSICAL)){
                            return factory.createObjectBuilder()
                                    .add(R.ErrMsg.ERROR, R.ErrMsg.TYPE_ERROR)
                                    .build();
                        }
                        setType(clientType);
                        setName(clientName.substring(1, clientName.length()-1));//убираем кавычки
                        setPhoneNumber(phone.substring(1, phone.length()-1));   //убираем кавычки
                        setAddres(address.substring(1, address.length()-1));    //убираем кавычки
                        return factory.createObjectBuilder()
                                .add(R.InfoMsg.INFO, R.InfoMsg.SUCSESS)
                                .build();
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
        }else{
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_1)
                    .build();
        }
    }
    
}
