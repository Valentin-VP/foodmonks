package org.foodmonks.backend.authentication;

public class InfoRestaurante {

    private String nombre;
    private String descripcion;

    private Object roles;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String firstName) {
        this.nombre = firstName;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String lastName) {
        this.descripcion = lastName;
    }

    public Object getRoles() {
        return roles;
    }

    public void setRoles(Object roles) {
        this.roles = roles;
    }
}
