package org.foodmonks.backend.Direccion;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Direccion.Exceptions.DireccionNumeroException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;

    @Autowired
    public DireccionService(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    public Direccion crearDireccion(JsonObject jsonDireccion) throws DireccionNumeroException {
        verificarDirecccion(jsonDireccion);
        return new Direccion(
                jsonDireccion.get("numero").getAsInt(),
                jsonDireccion.get("calle").getAsString(),
                jsonDireccion.get("esquina").getAsString(),
                jsonDireccion.get("detalles").getAsString(),
                jsonDireccion.get("latitud").getAsString(),
                jsonDireccion.get("longitud").getAsString()
        );
    }

    public void modificarDireccion(Direccion direccion, Direccion direccionNueva){
        direccion.setLatitud(direccionNueva.getLatitud());
        direccion.setLongitud(direccionNueva.getLongitud());
        direccion.setCalle(direccionNueva.getCalle());
        direccion.setNumero(direccionNueva.getNumero());
        direccion.setEsquina(direccionNueva.getEsquina());
        direccion.setDetalles(direccionNueva.getDetalles());
        direccionRepository.save(direccion);
    }

    public Direccion obtenerDireccion(Long id){
        return direccionRepository.findDireccionById(id);
    }

    public void verificarDirecccion(JsonObject jsonDireccion) throws DireccionNumeroException {
        if (!jsonDireccion.get("numero").getAsString().matches("[0-9]*") || jsonDireccion.get("numero").getAsString().isBlank()) {
            throw new DireccionNumeroException("El numero de puerta debe ser un numero real");
        }
    }

}
