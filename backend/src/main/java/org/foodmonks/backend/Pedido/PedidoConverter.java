package org.foodmonks.backend.Pedido;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
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
            jsonPedido.addProperty("fechaHoraProcesado", pedido.getFechaHoraProcesado().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }else{
            jsonPedido.addProperty("fechaHoraProcesado", "Sin Fecha");
        }
        jsonPedido.addProperty("total", pedido.getTotal());
        jsonPedido.addProperty("medioPago", pedido.getMedioPago().name());
        if (pedido.getFechaHoraEntrega() != null) {
            jsonPedido.addProperty("fechaHoraEntrega", pedido.getFechaHoraEntrega().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }else{
            jsonPedido.addProperty("fechaHoraEntrega", "Sin Fecha");
        }
        return jsonPedido;
    }

    // Para Listar pedidos sin confirmar
    public List<JsonObject> listaJsonPedidoPendientes(List<Pedido> pedidos){
        List<JsonObject> gsonPedidos = new ArrayList<>();
        for (Pedido pedido : pedidos){
            gsonPedidos.add(jsonPedidoPendientes(pedido));
        }
        return gsonPedidos;
    }

    // Datos de MenuCompra y Nombre y Apellido del cliente + datos estándar de pedido y nombre Restaurante
    public JsonObject jsonPedidoPendientes(Pedido pedido) {
        JsonObject jsonPedido= new JsonObject();
        jsonPedido.addProperty("id", pedido.getId());
        jsonPedido.addProperty("direccion", pedido.getDireccion()!=null ? (pedido.getDireccion().getCalle() + " "
                + pedido.getDireccion().getNumero().toString() + " esq. "
                + pedido.getDireccion().getEsquina()
                + (pedido.getDireccion().getDetalles()!=null ? " (" + pedido.getDireccion().getDetalles()+ ")" : "")) : "?");
        jsonPedido.addProperty("total", pedido.getTotal());
        jsonPedido.addProperty("medioPago", pedido.getMedioPago() == MedioPago.PAYPAL ? "PayPal" : "Efectivo");
        jsonPedido.addProperty("estadoPedido", pedido.getEstado() == EstadoPedido.DEVUELTO ? "Devuelto" :
                (pedido.getEstado() == EstadoPedido.FINALIZADO ? "Finalizado" :
                        (pedido.getEstado() == EstadoPedido.PENDIENTE ? "Pendiente" :
                                (pedido.getEstado() == EstadoPedido.CONFIRMADO ? "Confirmado" : "Rechazado"))));
        jsonPedido.addProperty("calificacionRestaurante", pedido.getCalificacionRestaurante() != null ? pedido.getCalificacionRestaurante().getPuntaje().toString() : "");
        jsonPedido.addProperty("calificacionRestauranteComentario", pedido.getCalificacionRestaurante() != null ? pedido.getCalificacionRestaurante().getComentario() : "");
        jsonPedido.addProperty("calificacionCliente", pedido.getCalificacionCliente() != null ? pedido.getCalificacionCliente().getPuntaje().toString() : "");
        jsonPedido.addProperty("calificacionClienteComentario", pedido.getCalificacionCliente() != null ? pedido.getCalificacionCliente().getComentario() : "");

        if(pedido.getFechaHoraEntrega() != null) {
            jsonPedido.addProperty("fechaHoraEntrega", pedido.getFechaHoraEntrega().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }else{
            jsonPedido.addProperty("fechaHoraEntrega", "Sin Fecha");
        }

        if(pedido.getFechaHoraProcesado() != null) {
            jsonPedido.addProperty("fechaHoraProcesado", pedido.getFechaHoraProcesado().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }else{
            jsonPedido.addProperty("fechaHoraProcesado", "Sin Fecha");
        }

        if (pedido.getCliente()!=null){
            jsonPedido.addProperty("nombreApellidoCliente", pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
        }

        if (pedido.getRestaurante()!=null){
            jsonPedido.addProperty("nombreRestaurante", pedido.getRestaurante().getNombreRestaurante());
            jsonPedido.addProperty("imagenRestaurante", pedido.getRestaurante().getImagen());
        }

        if (pedido.getReclamo()!=null){
            JsonObject reclamo = new JsonObject();
            reclamo.addProperty("razon", pedido.getReclamo().getRazon());
            reclamo.addProperty("comentario", pedido.getReclamo().getComentario());
            reclamo.addProperty("fecha", pedido.getReclamo().getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE)); //"yyyy-MM-dd HH:mm:ss"
            reclamo.addProperty("id", pedido.getReclamo().getId().toString());
            jsonPedido.add("reclamo", reclamo);
        }

        if (!pedido.getMenusCompra().isEmpty()){
            JsonArray jsonMenus = new JsonArray();
            for (MenuCompra m : pedido.getMenusCompra()) {
                JsonObject jsonMenuCompra= new JsonObject();
                jsonMenuCompra.addProperty("menu", m.getNombre());
                jsonMenuCompra.addProperty("imagen", m.getImagen());
                jsonMenuCompra.addProperty("precio", m.getPrice().toString());
                jsonMenuCompra.addProperty("multiplicadorPromocion", m.getMultiplicadorPromocion());
                jsonMenuCompra.addProperty("precioPorCantidad", Math.round(m.getPrice() * m.getCantidad()));
                jsonMenuCompra.addProperty("calculado", Math.round((m.getPrice() - (m.getPrice() * (m.getMultiplicadorPromocion() / 100))) * m.getCantidad()));
                jsonMenuCompra.addProperty("cantidad", m.getCantidad().toString());
                jsonMenus.add(jsonMenuCompra);
            }
            jsonPedido.add("menus", jsonMenus);
        }

        return jsonPedido;
    }

    public JsonObject listaJsonPedidoPaged(List<Pedido> pedidos){
        List<JsonObject> gsonPedidos = new ArrayList<>();
        JsonArray jsonArray = new JsonArray();
        JsonObject result = new JsonObject();
        for (Pedido pedido : pedidos){
            gsonPedidos.add(jsonPedidoPendientes(pedido));
        }

        for (JsonObject pedido : gsonPedidos){
            jsonArray.add(pedido);
        }
        result.add("pedidos", jsonArray);
        return result;
    }

}

