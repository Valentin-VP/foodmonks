package org.foodmonks.backend.Usuario;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorColumn(name="rol")
public abstract class Usuario {

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


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
