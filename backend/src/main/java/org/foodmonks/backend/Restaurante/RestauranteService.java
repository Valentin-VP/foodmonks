package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Autowired
    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public void createRestaurante(Restaurante restaurante) {
        restauranteRepository.save(restaurante);
    }

    public List<Restaurante> listarRestaurante(){
        return restauranteRepository.findAll();
    }

    public Restaurante buscarRestaurante(String correo) {
        Restaurante aux = restauranteRepository.findByCorreo(correo);
        if (aux == null) {
            return null;
        } else {
            return aux;
        }
    }

    public void editarRestaurante(Restaurante restaurante) {
        restauranteRepository.save(restaurante);
    }

    public void modificarEstado(String correo,EstadoRestaurante estado) {
        restauranteRepository.findByCorreo(correo).setEstado(estado);
    }

    public void createSolicitudAltaRestaurante(String nombre, String apellido, String correo, String password,
                                               LocalDate now, float v, String nombreRestaurante, String rut,
                                               Direccion direccion, EstadoRestaurante pendiente, String telefono,
                                               String descripcion, String cuentaPaypal, String url, ArrayList<JsonObject> jsonMenus) {
    }
}
