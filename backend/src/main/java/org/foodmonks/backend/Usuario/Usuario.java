package org.foodmonks.backend.Usuario;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@Entity
//@MappedSuperclass
@DiscriminatorColumn(name="rol")
public abstract class Usuario implements UserDetails {

	@Id
    @NotBlank(message = "El correo no puede ser vacio")
	private String correo;
    @NotBlank(message = "El nombre no puede ser vacio")
    private String nombre;
    @NotBlank(message = "El apellido no puede ser vacio")
    private String apellido;
    @NotBlank(message = "La pass no puede ser vacia")
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
