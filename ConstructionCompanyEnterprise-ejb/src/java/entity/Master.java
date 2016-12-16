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
@Table(name = "\"MASTER\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Master.findAll", query = "SELECT m FROM Master m"),
    @NamedQuery(name = "Master.findById", query = "SELECT m FROM Master m WHERE m.id = :id"),
    @NamedQuery(name = "Master.findByName", query = "SELECT m FROM Master m WHERE m.name = \':name\'"),
    @NamedQuery(name = "Master.findByPhoneNumber", query = "SELECT m FROM Master m WHERE m.phoneNumber = \':phoneNumber\'")})
public class Master implements Serializable , Converter {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "MASTER_ID", sequenceName = "public.\"MASTER_ID_seq\"", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="MASTER_ID")
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "masterId", fetch = FetchType.LAZY)
    private List<Work> workList;

    public Master() {
    }

    public Master(Integer id) {
        this.id = id;
    }

    public Master(Integer id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public boolean FinishWork(int WorkNumber){
        if(WorkNumber > 0){
            if(workList.get(WorkNumber).getEstimateId().getOrderId().getStatus() == ConstructionOrder.INPROGRESS){
                workList.get(WorkNumber).setFinish(Work.FINISH);
                if(workList.get(WorkNumber).getEstimateId().isFinish()){
                    if(workList.get(WorkNumber).getEstimateId().getOrderId().isFinish()){
                        workList.get(WorkNumber).getEstimateId().getOrderId()
                            .setStatus(ConstructionOrder.WAITING_ACKNOWLEDGMENT_TAKE);
                    }
                }
                return true;
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

    @XmlTransient
    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
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
        if (!(object instanceof Master)) {
            return false;
        }
        Master other = (Master) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.Master[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {      
        return Json
                .createBuilderFactory(null)
                .createObjectBuilder()
                .add("id", getId())
                .add("name", getName())
                .add("phone", getPhoneNumber());
    }

    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        String masterName;
        String phone;
        if(obj.containsKey("name")){
            masterName = obj.get("name").toString();
            if(obj.containsKey("phone")){
                phone = obj.get("phone").toString();
                if(masterName == null || masterName.trim().equals("")){
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR, R.ErrMsg.NAME_ERROR)
                            .build();
                }
                if(phone == null || phone.trim().equals("")){
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR, R.ErrMsg.PHONE_ERROR)
                            .build();
                }
                setName(masterName.substring(1, masterName.length()-1));
                setPhoneNumber(phone.substring(1, phone.length()-1));
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
    }
    
}
