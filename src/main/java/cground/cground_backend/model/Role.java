package cground.cground_backend.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name="roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="role_id")
    private Integer roleId;

    private String authority;

    public Integer getRoleId() {

        return roleId;
    }

    public Role(){
        super();
    }

    public Role(String authority){

        this.authority = authority;
    }

    public Role(Integer roleId, String authority) {
        this.roleId = roleId;
        this.authority = authority;
    }

    public void setRoleId(Integer roleId) {

        this.roleId = roleId;
    }

    public void setAuthority(String authority) {

        this.authority = authority;
    }

    @Override
    public String getAuthority() {
       return this.authority;
    }
}
