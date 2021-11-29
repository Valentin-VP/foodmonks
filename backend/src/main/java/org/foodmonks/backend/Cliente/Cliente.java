package org.foodmonks.backend.Cliente;


import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
@Getter
@Setter
@Entity
@DiscriminatorValue("cliente")
public class Cliente extends Usuario {

    private Float calificacion;
    private Integer cantidadCalificaciones;
    @ManyToMany(cascade=CascadeType.ALL)
    private List<Direccion> direcciones = new ArrayList<>();
    @Enumerated(value = EnumType.STRING)
    private EstadoCliente estado;
    private String mobileToken;
    @OneToMany(mappedBy="cliente")
	private List<Pedido> pedidos = new ArrayList<>();

    private String roles = "ROLE_CLIENTE";

    public Cliente() {
        super();
    }

    //CONSTRUCTOR CON UN LIST DE DIRECCIONS
    public Cliente(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro, Float calificacion, Integer cantidadCalificaciones, List<Direccion> direcciones, EstadoCliente estado, String mobileToken, List<Pedido> pedidos) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
        this.calificacion = calificacion;
        this.cantidadCalificaciones = cantidadCalificaciones;
        this.direcciones = direcciones;
        this.estado = estado;
        this.mobileToken = mobileToken;
        this.pedidos = pedidos;
    }
/*
    //CONSTRUCTOR CON UNA DIRECCION
    public Cliente(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro, Float calificacion, Direccion direccion, EstadoCliente estado, String mobileToken) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
        this.calificacion = calificacion;
        this.direcciones.add(direccion); //se agrega la direccion que se pasa ya que nunca agregamos mas de una direccion
        this.estado = estado;
        this.mobileToken = mobileToken;
    }
*/
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
        return !(getEstado() == EstadoCliente.BLOQUEADO || getEstado() == EstadoCliente.ELIMINADO);
    }

}
