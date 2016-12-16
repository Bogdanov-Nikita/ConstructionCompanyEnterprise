/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import Resources.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "\"STORAGE\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Storage.findAll", query = "SELECT s FROM Storage s"),
    @NamedQuery(name = "Storage.findById", query = "SELECT s FROM Storage s WHERE s.id = :id"),
    @NamedQuery(name = "Storage.findByLocation", query = "SELECT s FROM Storage s WHERE s.location = \':location\'")})
public class Storage implements Serializable , Converter {

    //TakeResources
    public final static int TAKE_RESORSE_SUCCESS = 0x21;
    public final static int INSUFFICIENTLY_RESORSE = 0x22;
    public final static int RESORSE_EMPTY = 0x23;
    public final static int RESORSE_NOT_FOUND = 0x24;
    public final static int STORAGE_EMPTY = 0x25;
    //SendResources
    public final static int SEND_RESORSE_SUCCESS = 0x26;
    public final static int STORAGE_RESOURSE_FAIL = 0x27;
    public final static int ADD_RESOURSE_FAIL = 0x28;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"STORAGE_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"LOCATION\"", nullable = false, length = 255)
    private String location;
    @JoinTable(name = "\"STORAGELIST\"", joinColumns = {
        @JoinColumn(name = "\"ID\"", referencedColumnName = "\"ID\"", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "\"RESOURCE_ID\"", referencedColumnName = "\"ID\"", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Resource> resourceList;

    public Storage() {
    }

    public Storage(Integer id) {
        this.id = id;
    }

    public Storage(Integer id, String location) {
        this.id = id;
        this.location = location;
    }
    
    public int findResoursePositionByProductCode(int productCode){
        for(int i = 0; i < resourceList.size(); i++){
            if(resourceList
                    .get(i)
                    .getResourceinformationId()
                    .getProductCode() == productCode){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Устарел
     * взять ресурс со склада
     * @param i - номер ресурса на складе
     * @param Amount - количество ресурса
     * @return успешно или тип ошибки.
     */
    public int TakeResources(int i,double Amount){
        if(resourceList != null){
            if(i < resourceList.size()){
                if(resourceList.get(i).getAmount() > 0 ){
                    double tempAmount = resourceList.get(i).getAmount() - Amount;
                    if(tempAmount >= 0){
                        resourceList.get(i).setAmount(tempAmount);
                        return TAKE_RESORSE_SUCCESS;
                    }else{
                        resourceList.get(i).setAmount(0);
                        return INSUFFICIENTLY_RESORSE;
                    }
                }else{
                    return RESORSE_EMPTY;
                }
            }else{
                return RESORSE_NOT_FOUND;
            }
        }else{
            return STORAGE_EMPTY;
        }
    }

    /**
     * получить ресурс
     * @param productCode - тип ресурса который мы отправляем на склад
     * @param amount - количество ресурса котрое хотим добавить
     * @return успех или тип ошибки. 
     */
    public int SendResources(int productCode,double amount){
        if(resourceList != null){
            int index = findResoursePositionByProductCode(productCode);
            if(index >= 0){
                if(amount > 0){
                    resourceList.get(index).setAmount(resourceList.get(index).getAmount() + amount);
                    return SEND_RESORSE_SUCCESS;
                }else{
                    return ADD_RESOURSE_FAIL;
                }
            }else{
                return RESORSE_NOT_FOUND;
            }
        }else{
            return STORAGE_RESOURSE_FAIL;
        }
    }
    
    public boolean isEmpty(){
        return((resourceList == null) ? true : resourceList.isEmpty());
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @XmlTransient
    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
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
        if (!(object instanceof Storage)) {
            return false;
        }
        Storage other = (Storage) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.Storage[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder resourceArray = factory.createArrayBuilder();
        for(Resource item : resourceList){
            resourceArray = resourceArray.add(item.toJSON());
        }
        return factory
                .createObjectBuilder()
                .add("storageId", getId())
                .add("location", getLocation())
                .add(R.ArraysName.RESOURCE,resourceArray);
    }

    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        String storageLocation;       
        if(obj.containsKey("location")){
            storageLocation = obj.getString("location");
            if(storageLocation.trim().equals("")){
                return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_STORAGE_ERROR)
                    .build();
            }
                setLocation(storageLocation);
                if(obj.containsKey(R.ArraysName.RESOURCE)){
                   JsonArray array = obj.getJsonArray(R.ArraysName.RESOURCE);
                   if(resourceList == null){resourceList = new ArrayList<>();}
                   for(int i = 0; i < array.size(); i++){
                       JsonObject item = array.getJsonObject(i);
                       Resource res = new Resource();
                       JsonObject ans = res.fromJSON(item, em);
                       if (ans.containsKey(R.ErrMsg.ERROR)){
                           return ans;
                       }
                       resourceList.add(res);
                   }
                }
                return factory.createObjectBuilder()
                        .add(R.InfoMsg.INFO, R.InfoMsg.SUCSESS)
                        .build();
        }else{
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_1)
                    .build();
        }
    }

    
}
