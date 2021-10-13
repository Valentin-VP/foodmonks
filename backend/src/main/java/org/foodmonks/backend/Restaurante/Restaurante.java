package org.foodmonks.backend.Restaurante;

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

@Entity
@DiscriminatorValue("restaurante")
public class Restaurante extends Usuario {

    private Float calificacion;
    private String nombreRestaurante;
    private Integer rut;
    @OneToOne(cascade=CascadeType.ALL)
    private Direccion direccion;
    private EstadoRestaurante estado;
    private Integer telefono;
    private String descripcion;
    private  String cuentaPaypal;
    private String imagen;
    @OneToMany(mappedBy="restaurante")//,cascade=CascadeType.ALL,orphanRemoval=true)
	private List<Pedido> pedidos = new ArrayList<>();
    //Cambios en cascada y el orphanRemoval, nos garantiza que el ciclo de vida de un Reclamo depende del ciclo de vida del Restaurante con el que está asociado. cascade a nivel de base de datos, la entidad se eleiminará con orphanRemoval en true si ya no tiene referencias de la clase primaria
  	@OneToMany(cascade=CascadeType.ALL,orphanRemoval=true)
  	private List<Reclamo>reclamos = new ArrayList<>();
    @OneToMany
    private List<Menu> menus = new ArrayList<>();

    private String roles = "ROLE_RESTAURANTE";


    public Restaurante() {
    }

    public Restaurante(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro, Float calificacion, String nombreRestaurante, Integer rut, Direccion direccion, EstadoRestaurante estado, Integer telefono, String descripcion, String cuentaPaypal, String imagen) {
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

    public Float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Float calificacion) {
        this.calificacion = calificacion;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }

    public Integer getRut() {
        return rut;
    }

    public void setRut(Integer rut) {
        this.rut = rut;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public EstadoRestaurante getEstado() {
        return estado;
    }

    public void setEstado(EstadoRestaurante estado) {
        this.estado = estado;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCuentaPaypal() {
        return cuentaPaypal;
    }

    public void setCuentaPaypal(String cuentaPaypal) {
        this.cuentaPaypal = cuentaPaypal;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}
	//Para dar soporte a la bidireccion
		public void agregarPedido(Pedido pedido) {
					pedidos.add(pedido);
					pedido.setRestaurante(this);
		}
		public void eliminarPedido(Pedido pedido) {
					pedidos.remove(pedido);
					pedido.setRestaurante(null);
		}

	public List<Reclamo> getReclamos() {
		return reclamos;
	}

	public void setReclamos(List<Reclamo> reclamos) {
		this.reclamos = reclamos;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

    public String getRoles() {
        return this.roles;
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
        return true;
    }
}
