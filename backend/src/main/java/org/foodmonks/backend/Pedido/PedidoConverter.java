package org.foodmonks.backend.Pedido;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoConverter {

    public List<JsonObject> listaJsonPedido(List<Pedido> pedidos){
        List<JsonObject> gsonPedidos = new ArrayList<>();
        for (Pedido pedido : pedidos){
            gsonPedidos.add(jsonPedido(pedido));
        }
        return gsonPedidos;
    }

    public JsonArray arrayJsonPedido (List<Pedido> pedidos){
        JsonArray arrayJsonPedido = new JsonArray();
        for (Pedido pedido : pedidos){
            arrayJsonPedido.add(jsonPedido(pedido));
        }
        return arrayJsonPedido;
    }

    public JsonObject jsonPedido(Pedido pedido) {
        JsonObject jsonPedido= new JsonObject();
        jsonPedido.addProperty("id", pedido.getId());
        jsonPedido.addProperty("estado", pedido.getEstado().name());
        if (pedido.getCalificacionCliente() != null){
            jsonPedido.addProperty("calificacionCliente", pedido.getCalificacionCliente().getPuntaje());
            jsonPedido.addProperty("comentarioCliente", pedido.getCalificacionCliente().getComentario());
        }
        if (pedido.getCalificacionRestaurante() != null){
            jsonPedido.addProperty("calificacionRestaurante", pedido.getCalificacionRestaurante().getPuntaje());
            jsonPedido.addProperty("comentarioRestaurante", pedido.getCalificacionRestaurante().getComentario());
        }
        if (pedido.getFechaHoraProcesado() != null) {
            jsonPedido.addProperty("fechaHoraProcesado", pedido.getFechaHoraProcesado().toString());
        }
        jsonPedido.addProperty("total", pedido.getTotal());
        jsonPedido.addProperty("medioPago", pedido.getMedioPago().name());
        if (pedido.getFechaHoraEntrega() != null) {
            jsonPedido.addProperty("fechaHoraEntrega", pedido.getFechaHoraEntrega().toString());
        }
        return jsonPedido;
    }
}
