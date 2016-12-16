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
@Table(name = "\"RESOURCEINFORMATION\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ResourceItem.findAll", query = "SELECT r FROM ResourceItem r"),
    @NamedQuery(name = "ResourceItem.findById", query = "SELECT r FROM ResourceItem r WHERE r.id = :id"),
    @NamedQuery(name = "ResourceItem.findByProductCode", query = "SELECT r FROM ResourceItem r WHERE r.productCode = :productCode"),
    @NamedQuery(name = "ResourceItem.findByName", query = "SELECT r FROM ResourceItem r WHERE r.name = \':name\'")})
public class ResourceItem implements Serializable , Converter {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="\"RESOURCEINFORMATION_ID_seq\"")
    @Basic(optional = false)
    @Column(name = "\"ID\"", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"PRODUCT_CODE\"", nullable = false)
    private int productCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"NAME\"", nullable = false, length = 255)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resourceinformationId", fetch = FetchType.LAZY)
    private List<Resource> resourceList;

    public ResourceItem() {
    }

    public ResourceItem(Integer id) {
        this.id = id;
    }

    public ResourceItem(Integer id, int productCode, String name) {
        this.id = id;
        this.productCode = productCode;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof ResourceItem)) {
            return false;
        }
        ResourceItem other = (ResourceItem) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.ResourceItem[ id=" + id + " ]";
    }

    @Override
    public JsonObjectBuilder toJSON() {
        return Json
                .createBuilderFactory(null)
                .createObjectBuilder()
                .add("resourceItemId", getId())
                .add("name", getName())
                .add("productCode", getProductCode());
    }

    @Override
    public JsonObject fromJSON(JsonObject obj, EntityManager em) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        String productName;
        int jProductCode;        
        if(obj.containsKey("name")){
            productName = obj.getString("name");
            if(obj.containsKey("productCode")){
                jProductCode = obj.getInt("produCtcode",-1);
                if(productName == null || productName.trim().equals("")){
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR, R.ErrMsg.INPUT_RESOURCE_NAME_ERROR)
                            .build();
                }
                if(jProductCode <= 0){
                    return factory.createObjectBuilder()
                            .add(R.ErrMsg.ERROR, R.ErrMsg.TYPE_ERROR)
                            .build();
                }
                setName(productName);
                setProductCode(jProductCode);
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
    }
}
