/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nik
 */
@Entity
@Table(name = "\"USERS\"", schema = "\"public\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByLogin", query = "SELECT u FROM Users u WHERE u.login = :login"),
    @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
    @NamedQuery(name = "Users.findByRoleId", query = "SELECT u FROM Users u WHERE u.roleId = :roleId")})
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"LOGIN\"", nullable = false, length = 255)
    private String login;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "\"PASSWORD\"", nullable = false, length = 255)
    private String password;
    @Basic(optional = false)
    @NotNull
    @Column(name = "\"ROLE_ID\"", nullable = false)
    private int roleId;

    public Users() {
    }

    public Users(String login) {
        this.login = login;
    }

    public Users(String login, String password, int roleId) {
        this.login = login;
        this.password = password;
        this.roleId = roleId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (login != null ? login.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }else{
            Users other = (Users) object;        
            return (other.login != null) && 
                    (this.login != null) && 
                    (this.login.equals(other.login));
        }
    }

    @Override
    public String toString() {
        return "entity.Users[ login=" + login + " ]";
    }
    
}
