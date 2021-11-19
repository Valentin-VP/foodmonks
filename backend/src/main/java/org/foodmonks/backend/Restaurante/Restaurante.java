package org.foodmonks.backend.Restaurante;

import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@DiscriminatorValue("restaurante")
public class Restaurante extends Usuario {

    private Float calificacion;
    private String nombreRestaurante;
    private Long rut;
    @OneToOne(cascade=CascadeType.ALL)
    private Direccion direccion;
    @Enumerated(value = EnumType.STRING)
    private EstadoRestaurante estado;
    private Integer telefono;
    private String descripcion;
    private String cuentaPaypal;
    private String imagen;
    @OneToMany(mappedBy="restaurante")//,cascade=CascadeType.ALL,orphanRemoval=true)
	private List<Pedido> pedidos = new ArrayList<>();
    //Cambios en cascada y el orphanRemoval, nos garantiza que el ciclo de vida de un Reclamo depende del ciclo de vida del Restaurante con el que está asociado. cascade a nivel de base de datos, la entidad se eleiminará con orphanRemoval en true si ya no tiene referencias de la clase primaria
  	@OneToMany(/*mappedBy="restaurante", */cascade=CascadeType.ALL,orphanRemoval=true)
  	private List<Reclamo>reclamos = new ArrayList<>();
    @OneToMany(mappedBy="restaurante", cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Menu> menus = new ArrayList<>();

    private String roles = "ROLE_RESTAURANTE";

    public Restaurante() {
        super();
    }

    public Restaurante(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro, Float calificacion, String nombreRestaurante, Long rut, Direccion direccion, EstadoRestaurante estado, Integer telefono, String descripcion, String cuentaPaypal, String imagen) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
        this.calificacion = calificacion;
        this.nombreRestaurante = nombreRestaurante;
        this.rut = rut;
        this.direccion = direccion;
        this.estado = estado;
        this.telefono = telefono;
        this.descripcion = descripcion;
        this.cuentaPaypal = cuentaPaypal;
        this.imagen = imagen;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] autoridades = new String[1];
        autoridades[0] = this.roles;
        Set<SimpleGrantedAuthority> rol = Arrays.stream(autoridades)
                .map(autoridad -> new SimpleGrantedAuthority(autoridad))
                .collect(Collectors.toSet());
        return rol;
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String getPassword() {
        return getContrasenia();
    }

    @Override
    public String getUsername() {
        return getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        EstadoRestaurante estado = getEstado();
        return !(estado == EstadoRestaurante.BLOQUEADO || estado == EstadoRestaurante.ELIMINADO
                || estado == EstadoRestaurante.RECHAZADO || estado == EstadoRestaurante.PENDIENTE);
    }
}
