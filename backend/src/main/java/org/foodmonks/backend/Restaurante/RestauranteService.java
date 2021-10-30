package org.foodmonks.backend.Restaurante;

import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Restaurante restauranteAux = RestauranteRepository.findByCorreo(correo);
        restauranteAux.setEstado(estado);
        RestauranteRepository.save(restauranteAux);
    }

    public EstadoRestaurante restauranteEstado (String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restaurante.getEstado();
    }
}
