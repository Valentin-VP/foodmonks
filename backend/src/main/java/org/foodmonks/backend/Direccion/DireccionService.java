package org.foodmonks.backend.Direccion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;

    @Autowired
    public DireccionService(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    public Direccion obtenerDireccion(String latitud, String longitud){
        return direccionRepository.findDireccionByLatitudAndLongitud(latitud,longitud);
    }

}
