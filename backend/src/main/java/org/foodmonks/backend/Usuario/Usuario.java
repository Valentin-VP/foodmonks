package org.foodmonks.backend.Usuario;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
//@MappedSuperclass
@DiscriminatorColumn(name="rol")
public abstract class Usuario implements UserDetails {

	@Id
	private String correo;
    private String nombre;
    private String apellido;
    private String contrasenia;
    private LocalDate fechaRegistro;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.fechaRegistro = fechaRegistro;
    }

}
