package org.foodmonks.backend.Admin;

import org.foodmonks.backend.Usuario.Usuario;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("admin")
public class Admin extends Usuario {

    public Admin() {
    }

    public Admin(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
    }
}
