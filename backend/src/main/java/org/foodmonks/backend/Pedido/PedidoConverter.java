package org.foodmonks.backend.Pedido;

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

    public JsonObject jsonPedido(Pedido pedido) {
        JsonObject jsonPedido= new JsonObject();
        jsonPedido.addProperty("id", pedido.getId());
        jsonPedido.addProperty("nombre", pedido.getNombre());
        jsonPedido.addProperty("estado", pedido.getEstado().name());
        jsonPedido.addProperty("calificacionCliente", (pedido.getCalificacionCliente()!=null ? pedido.getCalificacionCliente().getPuntaje().toString() : ""));
        jsonPedido.addProperty("comentarioCliente", (pedido.getCalificacionCliente()!=null ? pedido.getCalificacionCliente().getComentario() : ""));
        jsonPedido.addProperty("calificacionRestaurante", (pedido.getCalificacionRestaurante()!=null ? pedido.getCalificacionRestaurante().getPuntaje().toString() : ""));
        jsonPedido.addProperty("comentarioRestaurante", (pedido.getCalificacionRestaurante()!=null ? pedido.getCalificacionRestaurante().getComentario() : ""));
        jsonPedido.addProperty("fechaHoraProcesado", pedido.getFechaHoraProcesado().toString());
        jsonPedido.addProperty("total", pedido.getTotal());
        jsonPedido.addProperty("medioPago", pedido.getMedioPago().name());
        jsonPedido.addProperty("fechaHoraEntrega", pedido.getFechaHoraEntrega().toString());
        return jsonPedido;
    }
}
