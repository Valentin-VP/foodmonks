package org.foodmonks.backend.Cliente;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteConverter {

    private final DireccionConverter direccionConverter;

    @Autowired
    public ClienteConverter ( DireccionConverter direccionConverter){
        this.direccionConverter = direccionConverter;
    }

    public List<JsonObject> listaClientes (List<Cliente> clientes){
        List<JsonObject> gsonClientes = new ArrayList<>();
        for (Cliente cliente : clientes){
            gsonClientes.add(jsonCliente(cliente));
        }
        return gsonClientes;
    }

    public JsonArray arrayJsonCliente (List<Cliente> clientes){
        JsonArray arrayJsonClientes = new JsonArray();
        for (Cliente cliente : clientes){
            arrayJsonClientes.add(jsonCliente(cliente));
        }
        return arrayJsonClientes;
    }

    public JsonObject jsonCliente(Cliente cliente) {
        JsonObject jsonCliente = new JsonObject();
        jsonCliente.addProperty("correo", cliente.getCorreo());
        jsonCliente.addProperty("nombre", cliente.getNombre());
        jsonCliente.addProperty("apellido", cliente.getApellido());
        jsonCliente.addProperty("calificacion", cliente.getCantidadCalificaciones() >= 10 ? Math.round(cliente.getCalificacion()*10)/10f : 5f);
        jsonCliente.addProperty("cantidadCalificaciones", cliente.getCantidadCalificaciones());
        if (cliente.getFechaRegistro() != null) {
            jsonCliente.addProperty("fechaRegistro", cliente.getFechaRegistro().toString());
        } else {
            jsonCliente.addProperty("fechaRegistro","");
        }
        jsonCliente.addProperty("estado", cliente.getEstado().toString());
       // jsonCliente.addProperty("mobileToken", cliente.getMobileToken());
        JsonArray jsonDirecciones = new JsonArray();
        for (Direccion direccion : cliente.getDirecciones())  {
            jsonDirecciones.add(direccionConverter.jsonDireccion(direccion));
        }
        jsonCliente.add("direcciones", jsonDirecciones);
        JsonArray jsonRoles = new JsonArray();
        JsonObject jsonRol = new JsonObject();
        jsonRol.addProperty("role", cliente.getRoles());
        jsonRoles.add(jsonRol);
        jsonCliente.add("roles",jsonRoles);
        return jsonCliente;
    }



}
