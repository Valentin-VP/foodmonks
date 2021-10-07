package org.foodmonks.backend.Restaurante;

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
        System.out.println(restaurante.getNombre());
    }

    public List<Restaurante> listarRestaurante(){
        return restauranteRepository.findAll();
    }

    public void buscarRestaurante(Long id) {
        Optional<Restaurante> aux = restauranteRepository.findById(id);
        if (aux.isEmpty()) {
            System.out.println("el auxiliar esta vacio");
        }else {
            System.out.println(aux.get().getNombre());
            restauranteRepository.delete(aux.get());
        }
    }

    public void eliminarRestaurante(Long id) {
        Optional<Restaurante> aux = restauranteRepository.findById(id);
        if (aux.isEmpty()) {
            System.out.println("el auxiliar esta vacio");
        }else {
            System.out.println(aux.get().getNombre());
            restauranteRepository.delete(aux.get());
        }
    }

    public void editarRestaurante(Restaurante restaurante) {
        restauranteRepository.save(restaurante);
    }
}
