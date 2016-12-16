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
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
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
@Table(name = "\"WORKINFORMATION\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkItem.findAll", query = "SELECT w FROM WorkItem w"),
    @NamedQuery(name = "WorkItem.findById", query = "SELECT w FROM WorkItem w WHERE w.id = :id"),
    @NamedQuery(name = "WorkItem.findByDescription", query = "SELECT w FROM WorkItem w WHERE w.description = \':description\'"),
    @NamedQuery(name = "WorkItem.findByServiceCoast", query = "SELECT w FROM WorkItem w WHERE w.serviceCoast = :serviceCoast")})
public class WorkItem implements Serializable, Converter {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"WORKINFORMATION_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"DESCRIPTION\"", nullable = false, length = 255)
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"SERVICE_COAST\"", nullable = false)
    private double serviceCoast;
    @JoinTable(name = "\"WORKSANDRESOURCE\"", joinColumns = {
        @JoinColumn(name = "\"WORKINFORMATION_ID\"", referencedColumnName = "\"ID\"", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "\"RESOURCE_ID\"", referencedColumnName = "\"ID\"", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Resource> resourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workId", fetch = FetchType.LAZY)
    private List<Work> workList;

    public WorkItem() {
    }

    public WorkItem(Integer id) {
        this.id = id;
    }

    public WorkItem(Integer id, String description, double serviceCoast) {
        this.id = id;
        this.description = description;
        this.serviceCoast = serviceCoast;
    }

    /**
     * @return - рассчёт стоимости услуги и ресурсов.
     */
    public double CoastCalculation(){
        double Coast = serviceCoast;
        if(resourceList != null){
            for (Resource Resource : resourceList) {
                Coast = Coast + (Resource.getAmount() * Resource.getCoast());
            }
        }
        return Coast;
    }
    
    public void add(Resource res){
        if(res != null){
            resourceList.add(res);
        }
    }
    
    public void set(int i,Resource e){
        if(resourceList != null){
            resourceList.set(i, e);
        }
    }
    
    public void delete(int i){
        if(resourceList != null){
            if(i < resourceList.size()){
                resourceList.remove(i);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getServiceCoast() {
        return serviceCoast;
    }

    public void setServiceCoast(double serviceCoast) {
        this.serviceCoast = serviceCoast;
    }

    @XmlTransient
    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
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
        if (!(object instanceof WorkItem)) {
            return false;
        }
        WorkItem other = (WorkItem) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.WorkItem[ id=" + id + " ]";
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
                .add("workItemId", getId())
                .add("description", getDescription())
                .add("serviceCoast", getServiceCoast())
                .add("coastCalculation", CoastCalculation())
                .add(R.ArraysName.RESOURCE,resourceArray);
    }

    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        String workDescription;
        int jServiceCoast;        
        if(obj.containsKey("description")){
            workDescription = obj.getString("description");
            if(obj.containsKey("serviceCoast")){
                jServiceCoast = obj.getInt("serviceCoast",-1);
                if(workDescription == null || workDescription.trim().equals("")){
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_WORK_DESCRIPTION_ERROR)
                            .build();
                }
                if(jServiceCoast <= 0){
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_WORK_COAST_ERROR_2)
                            .build();
                }
                setDescription(workDescription);
                setServiceCoast(jServiceCoast);
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
                        .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_WORK_COAST_ERROR_1)
                        .build();
            }
        }else{
            return factory.createObjectBuilder()
                    .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_DATA_ERROR_1)
                    .build();
        }
    }
    
}
