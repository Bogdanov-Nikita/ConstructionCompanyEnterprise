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
import javax.json.JsonArrayBuilder;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nik
 */
@Entity
@Table(name = "\"ESTIMATE\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estimate.findAll", query = "SELECT e FROM Estimate e"),
    @NamedQuery(name = "Estimate.findById", query = "SELECT e FROM Estimate e WHERE e.id = :id"),
    @NamedQuery(name = "Estimate.findByType", query = "SELECT e FROM Estimate e WHERE e.type = :type"),
    @NamedQuery(name = "Estimate.findByCoast", query = "SELECT e FROM Estimate e WHERE e.coast = :coast"),
    @NamedQuery(name = "Estimate.findByPaid", query = "SELECT e FROM Estimate e WHERE e.paid = :paid")})
public class Estimate implements Serializable, Converter {

    public final static int MAIN = 0x1;
    public final static int ADDITIONAL = 0x2;
    
    public static final int PAID = 1;
    public static final int NOT_PAID = 0;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"ESTIMATE_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"TYPE\"", nullable = false)
    private int type;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"COAST\"", nullable = false)
    private double coast;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"PAID\"", nullable = false)
    private int paid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estimateId", fetch = FetchType.LAZY)
    private List<Work> workList;
    @JoinColumn(name = "\"ORDER_ID\"", referencedColumnName = "\"ID\"", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ConstructionOrder orderId;

    public Estimate() {
    }

    public Estimate(Integer id) {
        this.id = id;
    }

    public Estimate(Integer id, int type, int coast, int paid) {
        this.id = id;
        this.type = type;
        this.coast = coast;
        this.paid = paid;
    }
    
    /** 
     * @return Стоимость сметы
     */
    //преобразование в функциональный оператор приводит к ошибке при деплое
    public double CoastCalculation(){
        coast = 0;
        if(workList != null){
            if(!workList.isEmpty()){
                for(Work WorkListElem : workList){
                    coast = coast + WorkListElem.getWorkId().CoastCalculation();
                }
            }
        }
        return coast;
    }
    
    public boolean isFinish(){//проверка есть ли ещё невыполненная работа
        for(Work elem : workList){
            if(!elem.isFinish()){return false;}
        }
        return true;
    }
    
    public void add(Work e){
        if(e != null){
            workList.add(e);
        }
    }
    
    public void set(int i, Work e){
        if(workList != null){
            workList.set(i, e);
        }
    }
    
    public void delete(int i){
        if(workList != null){
            if(i < workList.size()){
                workList.remove(i);
            }
        }
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getCoast() {
        return coast;
    }

    public void setCoast(double coast) {
        this.coast = coast;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }
    
    public boolean isPaid(){
        return (paid == PAID);
    }

    @XmlTransient
    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
    }

    public ConstructionOrder getOrderId() {
        return orderId;
    }

    public void setOrderId(ConstructionOrder orderId) {
        this.orderId = orderId;
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
        if (!(object instanceof Estimate)) {
            return false;
        }
        Estimate other = (Estimate) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.Estimate[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder workArray = factory.createArrayBuilder();
        for(Work item : workList){
            workArray = workArray.add(item.toJSON());
        }
        return factory.createObjectBuilder()
                .add("estimateId", getId())
                .add("type",R.Estimate.TypeName(getType()))
                .add("status",R.Estimate.StatusName(isPaid(),isFinish()))
                .add("coastCalculation", CoastCalculation())
                .add(R.ArraysName.WORK,workArray);
    }

    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        double jCoast;
        int jPaid;
        int jOrderId;
        if(obj.containsKey("orderId")){
            jOrderId = obj.getInt("orderId",-1);
            if(obj.containsKey("paid")){
                jPaid = obj.getBoolean("paid",false)?PAID:NOT_PAID;
                if(obj.containsKey("coast")){
                    try{
                        jCoast = Double.parseDouble(obj.getString("coast"));
                    }catch(NumberFormatException e){
                        jCoast = -1;
                    }
                    if(jOrderId <= 0){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1)
                                .build();
                    }
                    if(jCoast < 0){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1)
                                .build();
                    }
                    try{
                        setOrderId(
                                (ConstructionOrder)em.createNamedQuery("ConstructionOrder.findById")
                                        .setParameter("id", jOrderId)
                                        .getSingleResult()
                        );
                        if(orderId.getEstimateList().size() > 1){
                            setType(ADDITIONAL);
                        }else{
                            setType(MAIN);
                        }
                        setCoast(jCoast);
                        setPaid(jPaid);
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
        return factory.createObjectBuilder()
                .add(R.ErrMsg.ERROR,R.ErrMsg.INPUT_DATA_ERROR_1)
                .build();
    }
    
}
