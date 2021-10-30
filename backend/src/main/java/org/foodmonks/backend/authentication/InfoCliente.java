package org.foodmonks.backend.authentication;

import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Direccion.Direccion;

import java.util.List;

@Getter
@Setter
public class InfoCliente {

    private String firstName;
    private String lastName;
    private String mail;
    private List<Direccion> direcciones;


    private Object roles;

//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public Object getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Object roles) {
//        this.roles = roles;
//    }
}
