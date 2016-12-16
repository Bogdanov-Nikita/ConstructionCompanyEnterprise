/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import Resources.R;
import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nik
 */
@Entity
@Table(name = "\"WORK\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Work.findAll", query = "SELECT w FROM Work w"),
    @NamedQuery(name = "Work.findById", query = "SELECT w FROM Work w WHERE w.id = :id"),
    @NamedQuery(name = "Work.findByFinish", query = "SELECT w FROM Work w WHERE w.finish = :finish")})
public class Work implements Serializable , Converter {

    public static final int FINISH = 1;
    public static final int NOT_FINISH = 0;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"WORK_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"FINISH\"", nullable = false)
    private int finish;
    @JoinColumn(name = "\"ESTIMATE_ID\"", referencedColumnName = "\"ID\"", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Estimate estimateId;
    @JoinColumn(name = "\"MASTER_ID\"", referencedColumnName = "\"ID\"", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Master masterId;
    @JoinColumn(name = "\"WORK_ID\"", referencedColumnName = "\"ID\"", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private WorkItem workId;

    public Work() {
    }

    public Work(Integer id) {
        this.id = id;
    }

    public Work(Integer id, int finish) {
        this.id = id;
        this.finish = finish;
    }

    public boolean isFinish(){//проверка выпонена работа или нет
        return (finish == FINISH);
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public Estimate getEstimateId() {
        return estimateId;
    }

    public void setEstimateId(Estimate estimateId) {
        this.estimateId = estimateId;
    }

    public Master getMasterId() {
        return masterId;
    }

    public void setMasterId(Master masterId) {
        this.masterId = masterId;
    }

    public WorkItem getWorkId() {
        return workId;
    }

    public void setWorkId(WorkItem workId) {
        this.workId = workId;
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
        if (!(object instanceof Work)) {
            return false;
        }
        Work other = (Work) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.Work[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {
        return workId
                .toJSON()
                .add("workId", getId())
                .add("orderNumber", getEstimateId().getOrderId().getNumber())
                .add("managerName", getEstimateId().getOrderId().getManagerId().getName())
                .add("managerPhone", getEstimateId().getOrderId().getManagerId().getPhoneNumber())
                .add("clientName", getEstimateId().getOrderId().getClientId().getName())
                .add("clientPhone", getEstimateId().getOrderId().getClientId().getPhoneNumber())
                .add("clientAddress", getEstimateId().getOrderId().getClientId().getAddres())
                .add("status", R.Work.StatusName(isFinish()));
    }

    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        int jFinish;
        int jEstimateId;
        int jMasterId;
        int jWorkId;
        if(obj.containsKey("workId")){
            jWorkId = obj.getInt("workId",-1);
            if(obj.containsKey("estimateId")){
                jEstimateId = obj.getInt("estimateId",-1);
                if(obj.containsKey("masterId")){
                    jMasterId = obj.getInt("masterId",-1);
                    if(obj.containsKey("finish")){
                        if(jWorkId <= 0 || jEstimateId <= 0 || jMasterId <= 0){
                            return factory.createObjectBuilder()
                                    .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_ID)
                                    .build();
                        }
                        jFinish = obj.getBoolean("finish",false)?FINISH:NOT_FINISH;
                        setFinish(jFinish);
                        try{
                            setWorkId(
                                    (WorkItem)em.createNamedQuery("WorkItem.findById")
                                            .setParameter("id", jWorkId)
                                            .getSingleResult()
                            );
                            setEstimateId(
                                    (Estimate)em.createNamedQuery("Estimate.findById")
                                            .setParameter("id", jEstimateId)
                                            .getSingleResult()
                            );
                            setMasterId(
                                    (Master)em.createNamedQuery("Master.findById")
                                            .setParameter("id", jMasterId)
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
                    }
                }
            }
        }
        return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1)
                .build();
    }
    
}
