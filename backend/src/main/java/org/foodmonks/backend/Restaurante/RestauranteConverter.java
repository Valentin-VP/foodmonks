package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Direccion.DireccionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class RestauranteConverter {

    private final DireccionConverter direccionConverter;

    @Autowired
    public RestauranteConverter ( DireccionConverter direccionConverter){
        this.direccionConverter = direccionConverter;
    }

    public List<JsonObject> listaRestaurantes (List<Restaurante> restaurantes){
        List<JsonObject> gsonRestaurantes = new ArrayList<>();
        for (Restaurante restaurante : restaurantes){
            gsonRestaurantes.add(jsonRestaurante(restaurante));
        }
        return gsonRestaurantes;
    }

    public JsonArray arrayJsonRestaurantes (List<Restaurante> restaurantes){
        JsonArray arrayJsonRestaurantes = new JsonArray();
        for (Restaurante restaurante : restaurantes){
            arrayJsonRestaurantes.add(jsonRestaurante(restaurante));
        }
        return arrayJsonRestaurantes;
    }

    public JsonObject jsonRestaurante(Restaurante restaurante) {
        JsonObject jsonRestaurante = new JsonObject();
        jsonRestaurante.addProperty("correo", restaurante.getCorreo());
        jsonRestaurante.addProperty("nombre", restaurante.getNombre());
        jsonRestaurante.addProperty("apellido", restaurante.getApellido());
        jsonRestaurante.addProperty("fechaRegistro", restaurante.getFechaRegistro().toString());
        jsonRestaurante.addProperty("calificacion", restaurante.getCantidadCalificaciones() >= 10 ? Math.round(restaurante.getCalificacion()*10)/10f : 5f);
        jsonRestaurante.addProperty("cantidadCalificaciones", restaurante.getCantidadCalificaciones());
        jsonRestaurante.addProperty("nombreRestaurante", restaurante.getNombreRestaurante());
        jsonRestaurante.addProperty("rut", restaurante.getRut());
        jsonRestaurante.addProperty("estado",restaurante.getEstado().toString());
        jsonRestaurante.addProperty("telefono", restaurante.getTelefono());
        jsonRestaurante.addProperty("descripcion", restaurante.getDescripcion());
        jsonRestaurante.addProperty("cuentaPaypal", restaurante.getCuentaPaypal());
        jsonRestaurante.addProperty("imagen", restaurante.getImagen());
        JsonArray jsonDirecciones = new JsonArray();
        jsonDirecciones.add(direccionConverter.jsonDireccion(restaurante.getDireccion()));
        jsonRestaurante.add("direcciones", jsonDirecciones);
        JsonArray jsonRoles = new JsonArray();
        JsonObject jsonRol = new JsonObject();
        jsonRol.addProperty("role", restaurante.getRoles());
        jsonRoles.add(jsonRol);
        jsonRestaurante.add("roles",jsonRoles);
        return jsonRestaurante;
    }

}
