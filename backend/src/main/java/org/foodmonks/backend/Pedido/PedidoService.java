package org.foodmonks.backend.Pedido;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.CriterioQuery;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoConvertidor pedidoConvertidor;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, PedidoConvertidor pedidoConvertidor){
        this.pedidoRepository = pedidoRepository; this.pedidoConvertidor = pedidoConvertidor;
    }

    public List<JsonObject> listaPedidosPendientes(Restaurante restaurante){
        return pedidoConvertidor.listaJsonPedidoPendientes(pedidoRepository.findPedidosByRestauranteAndEstado(restaurante, EstadoPedido.PENDIENTE));
    }

    public List<JsonObject> listaPedidosHistorico(Restaurante restaurante, EstadoPedido estadoPedido, MedioPago medioPago, String orden, LocalDateTime[] fecha, Float[] total, Pageable pageable){

        List<Pedido> pedidos = pedidoRepository.findPedidosByRestaurante(restaurante, pageable).getContent();
        List<Pedido> result;

        PedidoSpecificationBuilder builder = new PedidoSpecificationBuilder();
        List<CriterioQuery> querys = new ArrayList<>();

        if (estadoPedido != null) {
            querys.add(new CriterioQuery("estado",":",estadoPedido, false));
//            result = pedidoRepository.findAll(estadoSpec);
            //result = pedidos.stream().filter(p -> p.getEstado().equals(estadoPedido)).collect(Collectors.toList());
        }else{
            querys.add(new CriterioQuery("estado",":",EstadoPedido.DEVUELTO, true));
            querys.add(new CriterioQuery("estado",":",EstadoPedido.FINALIZADO, true));
//            result = pedidoRepository.findAll(estadoSpecF.and(estadoSpecD));
            //result = pedidos.stream().filter(p -> (p.getEstado().equals(EstadoPedido.DEVUELTO) || p.getEstado().equals(EstadoPedido.FINALIZADO))).collect(Collectors.toList());
        }
//        pedidos = Optional.ofNullable(result).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
        if (medioPago != null) {
            querys.add(new CriterioQuery("medioPago",":",medioPago, false));
            //result = pedidos.stream().filter(p -> p.getMedioPago().equals(medioPago)).collect(Collectors.toList());
            //pedidos = Optional.ofNullable(result).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
        }

        if (total != null){
            querys.add(new CriterioQuery("total",">",total[0], false));
            querys.add(new CriterioQuery("total","<",total[1], false));
            //result = pedidos.stream().filter(p -> (p.getTotal() >= total[0] && p.getTotal() <= total[1])).collect(Collectors.toList());
            //pedidos = Optional.ofNullable(result).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
        }

        if (fecha != null){
            querys.add(new CriterioQuery("fechaHoraEntrega",">",fecha[0], false));
            querys.add(new CriterioQuery("fechaHoraEntrega","<",fecha[1].plusDays(1), false));
//            result = pedidos.stream().filter(p -> (
//                    p.getFechaHoraEntrega().isAfter(LocalDateTime.from(fecha[0]))
//                            && p.getFechaHoraEntrega().isBefore(LocalDateTime.from(fecha[1]))
//            )).collect(Collectors.toList());
//            pedidos = Optional.ofNullable(result).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
        }
        for(CriterioQuery c : querys){
            builder.with(c);
        }
        Specification<Pedido> p = builder.build();
        result = pedidoRepository.findAll(p, pageable).getContent();
        if (orden != null){
            if (orden.equals("asc"))
                result = pedidos.stream().sorted(Comparator.comparing(Pedido::getTotal)).collect(Collectors.toList());
            else if (orden.equals("desc"))
                result = pedidos.stream().sorted(Comparator.comparing(Pedido::getTotal).reversed()).collect(Collectors.toList());
        }

        //Cambiar o el convertidor o la funcion referenciada

        return pedidoConvertidor.listaJsonPedidoPendientes(result);
    }

    public boolean existePedidoRestaurante (Long idPedido, Restaurante restaurante) {
        return pedidoRepository.existsPedidoByIdAndRestaurante(idPedido,restaurante); }

    public void cambiarEstadoPedido(Long idPedido, EstadoPedido estadoPedido){
        Pedido pedido = pedidoRepository.findPedidoById(idPedido);
        pedido.setEstado(estadoPedido);
        pedidoRepository.save(pedido);
    }

    // Para cuando se confirma el pedido.
    public void cambiarFechasEntregaProcesado(Long idPedido, Integer minutosEntrega){
        Pedido pedido = pedidoRepository.findPedidoById(idPedido);
        pedido.setFechaHoraProcesado(LocalDateTime.now());
        pedido.setFechaHoraEntrega(pedido.getFechaHoraProcesado().plusMinutes(minutosEntrega));
        pedidoRepository.save(pedido);
    }
}
