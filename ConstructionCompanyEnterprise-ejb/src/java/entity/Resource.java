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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nik
 */
@Entity
@Table(name = "\"RESOURCE\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Resource.findAll", query = "SELECT r FROM Resource r"),
    @NamedQuery(name = "Resource.findById", query = "SELECT r FROM Resource r WHERE r.id = :id"),
    @NamedQuery(name = "Resource.findByAmount", query = "SELECT r FROM Resource r WHERE r.amount = :amount"),
    @NamedQuery(name = "Resource.findByCoast", query = "SELECT r FROM Resource r WHERE r.coast = :coast")})
public class Resource implements Serializable , Converter {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"RESOURCE_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"AMOUNT\"", nullable = false)
    private double amount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"COAST\"", nullable = false)
    private double coast;
    @ManyToMany(mappedBy = "resourceList", fetch = FetchType.LAZY)
    private List<Storage> storageList;
    @ManyToMany(mappedBy = "resourceList", fetch = FetchType.LAZY)
    private List<WorkItem> workItemList;
    @JoinColumn(name = "\"RESOURCEINFORMATION_ID\"", referencedColumnName = "\"ID\"", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ResourceItem resourceinformationId;

    public Resource() {
    }

    public Resource(Integer id) {
        this.id = id;
    }

    public Resource(Integer id, double amount, double coast) {
        this.id = id;
        if(amount < 0){
            this.amount = 0;
        }else{
            this.amount = amount;
        }
        if(coast < 0){
            this.coast = 0;
        }else{
            this.coast = coast;
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if(amount < 0){
            this.amount = 0;
        }else{
            this.amount = amount;
        }
    }

    public double getCoast() {
        return coast;
    }

    public void setCoast(double coast) {
        this.coast = coast;
    }

    @XmlTransient
    public List<Storage> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<Storage> storageList) {
        this.storageList = storageList;
    }

    @XmlTransient
    public List<WorkItem> getWorkItemList() {
        return workItemList;
    }

    public void setWorkItemList(List<WorkItem> workItemList) {
        this.workItemList = workItemList;
    }

    public ResourceItem getResourceinformationId() {
        return resourceinformationId;
    }

    public void setResourceinformationId(ResourceItem resourceinformationId) {
        this.resourceinformationId = resourceinformationId;
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
        if (!(object instanceof Resource)) {
            return false;
        }
        Resource other = (Resource) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.Resource[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {
        return resourceinformationId.toJSON()
                .add("resourceId", getId())
                .add("coast", getCoast())
                .add("amount", getAmount())
                .add("refer",
                        (((getWorkItemList() != null) && 
                                (getWorkItemList().size() > 0)) ? 
                        "работе" : 
                        "складу"));
    }
    
    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        double jAmount;
        double jCoast;
        double jId;
        if(obj.containsKey("coast")){
            jAmount = obj.getInt("coast", -1);
            if(obj.containsKey("amount")){
                jCoast = obj.getInt("amount", -1);
                if(obj.containsKey("resourceItemId")){
                    jId = obj.getInt("resourceItemId", -1);
                    if(jId <= 0 ){
                        return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                            .build();
                    }
                    if(jAmount <= 0){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_AMOUNT_ERROR_2)
                                .build();
                    }
                    if(jCoast <=0){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_RESOURCE_COAST_ERROR_2)
                                .build();
                    }
                    setAmount(jAmount);
                    setCoast(jCoast);
                    try{
                        setResourceinformationId(
                                (ResourceItem)em.createNamedQuery("ResourceItem.findById")
                                        .setParameter("id", jId)
                                        .getSingleResult()
                        );
                        return factory.createObjectBuilder()
                            .add(R.InfoMsg.INFO,R.InfoMsg.SUCSESS)
                            .build();
                    }catch(Exception e){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.SELECT_ERROR)
                                .build();
                    }
                }else{
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                            .build();
                }
            }else{
                return factory.createObjectBuilder()
                        .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_AMOUNT_ERROR_1)
                        .build();
            }
        }else{
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_RESOURCE_COAST_ERROR_1)
                    .build();
        }
    }

}
