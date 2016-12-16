/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import Resources.R;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nik
 */
@Entity
@Table(name = "\"ORDER\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConstructionOrder.findAll", query = "SELECT c FROM ConstructionOrder c"),
    @NamedQuery(name = "ConstructionOrder.findById", query = "SELECT c FROM ConstructionOrder c WHERE c.id = :id"),
    @NamedQuery(name = "ConstructionOrder.findByNumber", query = "SELECT c FROM ConstructionOrder c WHERE c.number = :number"),
    @NamedQuery(name = "ConstructionOrder.findByStatus", query = "SELECT c FROM ConstructionOrder c WHERE c.status = :status"),
    @NamedQuery(name = "ConstructionOrder.findByCurrentCoast", query = "SELECT c FROM ConstructionOrder c WHERE c.currentCoast = :currentCoast"),
    @NamedQuery(name = "ConstructionOrder.findByCreateDate", query = "SELECT c FROM ConstructionOrder c WHERE c.createDate = :createDate"),
    @NamedQuery(name = "ConstructionOrder.findByUpdateDate", query = "SELECT c FROM ConstructionOrder c WHERE c.updateDate = :updateDate"),
    @NamedQuery(name = "ConstructionOrder.findByEndDate", query = "SELECT c FROM ConstructionOrder c WHERE c.endDate = :endDate")})
public class ConstructionOrder implements Serializable, Converter {
        
    public final static int OPEN = 0x1;//содаём заказ.
    public final static int INPROGRESS = 0x2;// отправляем заказ прорабу.
    public final static int WAITING_ACKNOWLEDGMENT_TAKE = 0x3;// запрос на приём заказа.
    public final static int WAITING_PAY = 0x4;// ожидание оплаты от клиента
    public final static int WAITING_ACKNOWLEDGMENT_PAY = 0x5;// запрос на приём оплаты менеджером.
    public final static int CLOSE = 0x6;//закрытие заказы.
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"ORDER_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"NUMBER\"", nullable = false)
    private int number;//номер заказа
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"STATUS\"",nullable = false)
    private int status;//Статус заказа.
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"CURRENT_COAST\"", nullable = false)
    private double currentCoast;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"CREATE_DATE\"", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"UPDATE_DATE\"", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    @Column(name = "\"END_DATE\"")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @JoinColumn(name = "\"CLIENT_ID\"", referencedColumnName = "\"ID\"", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client clientId;//определяет к какому Клиенту принадлежит заказ.
    @JoinColumn(name = "\"MANAGER_ID\"", referencedColumnName = "\"ID\"", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Manager managerId;//определяет к какому Менеджеру принадлежит заказ.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId", fetch = FetchType.LAZY)
    private List<Estimate> estimateList;

    public ConstructionOrder() {
    }

    public ConstructionOrder(Integer id) {
        this.id = id;
    }

    public ConstructionOrder(Integer id, int number, int status, double currentCoast, Date createDate, Date updateDate) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.currentCoast = currentCoast;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    /** 
     * @return Стоимость всего заказа
     */
    public double CoastCalculation(){
        double totalCoast = 0;
        if(estimateList != null){
            if(!estimateList.isEmpty()){
                for(Estimate epartElem: estimateList){
                    totalCoast = totalCoast + epartElem.CoastCalculation();
                }
            }
        }
        return totalCoast;
    }
    
    /**
     * пополнение счёта клиентом
     * @param pay - количество денежных единиц внесённых клиентом.
     * @return true в случе успеха иначе false
     */
    public boolean ClientPay(double pay){
        if(pay > 0){
            double tempCoast = currentCoast - pay;
            if(tempCoast  == 0){
                currentCoast = 0;
                status = WAITING_ACKNOWLEDGMENT_PAY;
                return true;
            }else{
                if(tempCoast > 0){
                    currentCoast = tempCoast;
                    return true;
                }
            }
        }
        return false;
    }
    
    //закрытие заказа не означает уничтожение документа!
    public boolean CloseOrder(Date End) {
        if(End != null){//  защита от дурака на всякий случай!
            this.endDate = End;
            return true;
        }else{
            return false;
        }
        //Старый вариант
        /*if(End != null){//  защита от дурака на всякий случай! 
        //в иделаьном случае все эти проверки осуществляет менеджер.
            if(CurrentCoast == 0){
                Status = CLOSE;//так же повторная запись
                this.End = End;
                return true;
            }
        }
        return false;*/        
    }
    
    public boolean ClientAcceptanceOrder(){
        if(isFinish()){
            if(status == WAITING_ACKNOWLEDGMENT_TAKE){
                status = ConstructionOrder.WAITING_PAY;
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
    public boolean isFinish(){
        for(Estimate elem : estimateList){
            if(!elem.isFinish()){return false;}
        }
        return true;
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getCurrentCoast() {
        return currentCoast;
    }

    public void setCurrentCoast(double currentCoast) {
        this.currentCoast = currentCoast;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Client getClientId() {
        return clientId;
    }

    public void setClientId(Client clientId) {
        this.clientId = clientId;
    }

    public Manager getManagerId() {
        return managerId;
    }

    public void setManagerId(Manager managerId) {
        this.managerId = managerId;
    }

    @XmlTransient
    public List<Estimate> getEstimateList() {
        return estimateList;
    }

    public void setEstimateList(List<Estimate> estimateList) {
        this.estimateList = estimateList;
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
        if (!(object instanceof ConstructionOrder)) {
            return false;
        }
        ConstructionOrder other = (ConstructionOrder) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.ConstructionOrder[ id=" + id + " ]";
    }
    
    public String dataFormat(Date d){
        return (d != null) ? new SimpleDateFormat (R.DataFormat).format(d) : "--/--";
    }
    
    @Override
    public JsonObjectBuilder toJSON() {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder estimateArray = factory.createArrayBuilder();
        for(Estimate item : estimateList){
            estimateArray = estimateArray.add(item.toJSON());
        }
        return factory.createObjectBuilder()
            .add("orderId", getId())
            .add("number", getNumber())
            .add("status", R.Order.StatusName(getStatus()))
            .add("coast", getCurrentCoast())
            .add("create",dataFormat(getCreateDate()))
            .add("update", dataFormat(getUpdateDate()))
            .add("end", dataFormat(getEndDate()))
            .add("managername", getManagerId().getName())
            .add("managerphone", getManagerId().getPhoneNumber())
            .add("manageraddress", getManagerId().getOfficeAddress())
            .add("clientname", getClientId().getName())
            .add("clientphone", getClientId().getPhoneNumber())
            .add("clientaddress", getClientId().getAddres())
            .add(R.ArraysName.ESTIMATE, estimateArray);
    }

    @Override
    public JsonObject fromJSON(JsonObject obj,EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        int jManagerId;
        int jClientId;
        int jNumber;
        if(obj.containsKey("managerId")){
            jManagerId = obj.getInt("managerId",-1);
            if(obj.containsKey("clientId")){
                jClientId = obj.getInt("clientId",-1);
                if(obj.containsKey("number")){
                    jNumber = obj.getInt("number",-1);
                    
                    if(jManagerId <= 0 || jClientId <= 0){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_ID)
                                .build();
                    }
                    if(jNumber <= 0){
                        return factory.createObjectBuilder()
                                .add(R.ErrMsg.ERROR, R.ErrMsg.NUMBER_ERROR)
                                .build();
                    }

                    try{
                        setManagerId(
                                (Manager)em.createNamedQuery("Manager.findById")
                                        .setParameter("id", jManagerId)
                                        .getSingleResult()
                        );
                        setClientId(
                                (Client)em.createNamedQuery("Client.findById")
                                        .setParameter("id", jClientId)
                                        .getSingleResult()
                        );
                        setNumber(jNumber);
                        setStatus(OPEN);
                        setCurrentCoast(0);
                        Date date = new Date();
                        setCreateDate(date);
                        setUpdateDate((Date)date.clone());
                        setEndDate(null);
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
                .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_ID)
                .build();
    }
    
}
