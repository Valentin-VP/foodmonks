package org.foodmonks.backend.Menu;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.persistencia.MenuID;

import javax.persistence.*;

@Entity
@IdClass(MenuID.class)
public class Menu {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
	private String nombre;
	private Float precio;
	private String descripcion;
	private Boolean visible;
	private Float multiplicadorPromocion;
	private String imagen;
	@ManyToOne
	private Restaurante restaurante;
	
	public Menu() {
		// TODO Auto-generated constructor stub
	}

	public Menu(String nombre, Float precio, String descripcion, Boolean visible, Float multiplicadorPromocion,
			String imagen) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.descripcion = descripcion;
		this.visible = visible;
		this.multiplicadorPromocion = multiplicadorPromocion;
		this.imagen = imagen;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Float getPrecio() {
		return precio;
	}

	public void setPrecio(Float precio) {
		this.precio = precio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Float getMultiplicadorPromocion() {
		return multiplicadorPromocion;
	}

	public void setMultiplicadorPromocion(Float multiplicadorPromocion) {
		this.multiplicadorPromocion = multiplicadorPromocion;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	
	

}
