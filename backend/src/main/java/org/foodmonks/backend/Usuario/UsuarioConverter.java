package org.foodmonks.backend.Usuario;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Direccion.DireccionConverter;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsuarioConverter {

    @Autowired
    public UsuarioConverter (){

    }

    public JsonObject jsonUsuarios(Usuario usuario) {
            JsonObject item = new JsonObject();
            item.addProperty("correo", usuario.getCorreo());
            item.addProperty("fechaRegistro", usuario.getFechaRegistro().toString());
            if (usuario instanceof Cliente) {//si es cliente
                Cliente cliente = (Cliente) usuario;
                item.addProperty("rol", "CLIENTE");
                item.addProperty("estado", cliente.getEstado().toString());
                item.addProperty("nombre", cliente.getNombre());
                item.addProperty("apellido", cliente.getApellido());
                item.addProperty("calificacion", cliente.getCalificacion().toString());
            } else if(usuario instanceof Restaurante){//si es restaurante
                Restaurante restaurante = (Restaurante) usuario;
                item.addProperty("rol", "RESTAURANTE");
                item.addProperty("estado", restaurante.getEstado().toString());
                item.addProperty("RUT", restaurante.getRut().toString());
                item.addProperty("descripcion", restaurante.getDescripcion());
                item.addProperty("nombre", restaurante.getNombreRestaurante());
                item.addProperty("telefono", restaurante.getTelefono());
                item.addProperty("calificacion", restaurante.getCalificacion().toString());
            } else if(usuario instanceof Admin) {
                Admin admin = (Admin) usuario;
                item.addProperty("nombre", admin.getNombre());
                item.addProperty("rol", "ADMIN");
                item.addProperty("apellido", admin.getApellido());
            }
        return item;
    }

    public JsonObject listaJsonUsuarioPaged(List<Usuario> usuarios){
        List<JsonObject> gsonUsuarios = new ArrayList<>();
        JsonArray jsonArray = new JsonArray();
        JsonObject result = new JsonObject();
        for (Usuario usuario: usuarios){
            gsonUsuarios.add(jsonUsuarios(usuario));
        }
        for (JsonObject pedido : gsonUsuarios){
            jsonArray.add(pedido);
        }
        result.add("usuarios", jsonArray);
        return result;
    }
}
