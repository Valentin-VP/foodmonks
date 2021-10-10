package org.foodmonks.backend.Cliente;


import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.EstadoCliente;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("cliente")
public class Cliente extends Usuario {

    private Float calificacion;
    @ManyToMany(cascade=CascadeType.ALL)
    private List<Direccion> direcciones = new ArrayList<>();
    private EstadoCliente estado;
    private String mobileToken;
    @OneToMany(mappedBy="cliente",cascade=CascadeType.ALL,orphanRemoval=true)
	private List<Pedido> pedidos = new ArrayList<>();

    public Cliente() {
    }

    //CONSTRUCTOR CON UN LIST DE DIRECCIONS
    public Cliente(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro, Float calificacion, List<Direccion> direcciones, EstadoCliente estado, String mobileToken, List<Pedido> pedidos) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
        this.calificacion = calificacion;
        this.direcciones = direcciones;
        this.estado = estado;
        this.mobileToken = mobileToken;
        this.pedidos = pedidos;
    }

    //CONSTRUCTOR CON UNA DIRECCION
    public Cliente(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro, Float calificacion, Direccion direccion, EstadoCliente estado, String mobileToken) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
        this.calificacion = calificacion;
        this.direcciones.add(direccion); //se agrega la direccion que se pasa ya que nunca agregamos mas de una direccion
        this.estado = estado;
        this.mobileToken = mobileToken;
    }

    public Float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Float calificacion) {
        this.calificacion = calificacion;
    }

    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direccion> direcciones) {
        this.direcciones = direcciones;
    }

    public void addDireccion(Direccion direccion){
        this.direcciones.add(direccion);
    }

    public EstadoCliente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCliente estado) {
        this.estado = estado;
    }

    public String getMobileToken() {
        return mobileToken;
    }

    public void setMobileToken(String mobileToken) {
        this.mobileToken = mobileToken;
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
				pedido.setCliente(this);
	}
	public void eliminarPedido(Pedido pedido) {
				pedidos.remove(pedido);
				pedido.setCliente(null);
	}
}
