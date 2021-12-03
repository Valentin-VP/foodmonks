package org.foodmonks.backend.Menu;

import lombok.Getter;
import lombok.Setter;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.persistencia.MenuID;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@IdClass(MenuID.class)
public class Menu {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
	@NotBlank(message = "El nombre no puede ser vacio")
	private String nombre;
	private Float price;
	private String descripcion;
	private Boolean visible;
	private Float multiplicadorPromocion;
	private String imagen;
	@Enumerated(value = EnumType.STRING)
	private CategoriaMenu categoria;
	@Id
	@ManyToOne
	@JoinColumn(
			insertable=false,
			updatable=false
	)
	private Restaurante restaurante;

	public Menu(String nombre, Float price, String descripcion, Boolean visible, Float multiplicadorPromocion, String imagen, CategoriaMenu categoria, Restaurante restaurante) {
		this.nombre = nombre;
		this.price = price;
		this.descripcion = descripcion;
		this.visible = visible;
		this.multiplicadorPromocion = multiplicadorPromocion;
		this.imagen = imagen;
		this.categoria = categoria;
		this.restaurante = restaurante;
	}

	public Menu() {

	}
}
