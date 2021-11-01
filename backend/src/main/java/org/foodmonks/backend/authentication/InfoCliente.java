package org.foodmonks.backend.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.foodmonks.backend.Direccion.Direccion;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InfoCliente {

    private String firstName;
    private String lastName;
    private String mail;
    private List<Direccion> direcciones = new ArrayList<>();


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
