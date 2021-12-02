package org.foodmonks.backend.Demo;

import lombok.extern.slf4j.Slf4j;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoRepository;
import org.foodmonks.backend.Reclamo.ReclamoRepository;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class DemoService {
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final ReclamoRepository reclamoRepository;

    @Autowired
    public DemoService(UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository, ReclamoRepository reclamoRepository){
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.reclamoRepository = reclamoRepository;
    }

    public void cambiarFechas(String fechaInicio, String fechaFin) throws IllegalArgumentException{
        LocalDate fInit;
        LocalDate fFin;
        try{
            fInit = LocalDate.parse(fechaInicio);
            fFin = LocalDate.parse(fechaFin);
        }catch(DateTimeParseException e){
            throw new IllegalArgumentException("Fechas incorrectas.");
        }
        if (fFin.isBefore(fInit))
            throw new IllegalArgumentException("Fechas incorrectas.");
        Long intervalo = ChronoUnit.DAYS.between(fInit, fFin);
        List<Usuario> usuarioList = usuarioRepository.findAll();

        // Aca se podria cambiar el seed para que de siempre lo mismo (con algun numero en el constructor)
        Random generador = new Random();

        Integer cantDias = (int)Math.floor((generador.nextDouble() * (intervalo - 1)) + 1);

        List<Pedido> pedidoList = pedidoRepository.findAll();
        for (Pedido p : pedidoList){
            LocalDateTime fechaARestar = LocalDateTime.of(fFin, p.getFechaHoraProcesado().toLocalTime());
            p.setFechaHoraEntrega(fechaARestar.minusDays(cantDias));
            p.setFechaHoraProcesado(fechaARestar.minusDays(cantDias));

            if (p.getReclamo()!=null){
                LocalDateTime fechaARestarReclamo = LocalDateTime.of(fFin, p.getReclamo().getFecha().toLocalTime());
                p.getReclamo().setFecha(fechaARestarReclamo.minusDays(cantDias));
                // reclamoRepository.save(p.getReclamo());
            }
            pedidoRepository.save(p);
            cantDias = (int)Math.floor((generador.nextDouble() * (intervalo - 1)) + 1);
        }

        // Ciclo por usuarios para no obtener pedidos que aleatoriamente queden con fechas anteriores a su registro.
//        for (Usuario u : usuarioList){
//            u.setFechaRegistro(u.getFechaRegistro().minusDays(cantDias));
//            usuarioRepository.save(u);
//            if (u instanceof Cliente){
//                if (!((Cliente) u).getPedidos().isEmpty()){
//                    for (Pedido p : ((Cliente) u).getPedidos()){
//                        p.setFechaHoraEntrega(p.getFechaHoraEntrega().minusDays(cantDias));
//                        p.setFechaHoraProcesado(p.getFechaHoraProcesado().minusDays(cantDias));
//
//                        if (p.getReclamo()!=null){
//                            p.getReclamo().setFecha(p.getReclamo().getFecha().minusDays(cantDias));
//                            // reclamoRepository.save(p.getReclamo());
//                        }
//                        pedidoRepository.save(p);
//                    }
//                }
//            }
//
//            cantDias = (int)Math.floor((generador.nextDouble() * (intervalo - 1)) + 1);
//        }
        // Para que no quede la fecha de ningun usuario después de ningún pedido suyo
        for (Usuario u : usuarioList){
            if(u instanceof Restaurante){
                if (!((Restaurante) u).getPedidos().isEmpty()){
                    LocalDate minFechaRestaurante;
                    minFechaRestaurante = ((Restaurante) u).getPedidos().get(0).getFechaHoraProcesado().toLocalDate();
                    for (Pedido p : ((Restaurante) u).getPedidos()){
                        if (p.getFechaHoraProcesado().toLocalDate().isBefore(minFechaRestaurante)){
                            minFechaRestaurante = p.getFechaHoraProcesado().toLocalDate();
                        }
                    }
                    u.setFechaRegistro(minFechaRestaurante);
                    usuarioRepository.save(u);
                }
            }else if (u instanceof Cliente){
                if (!((Cliente) u).getPedidos().isEmpty()){
                    LocalDate minFechaCliente;
                    minFechaCliente = ((Cliente) u).getPedidos().get(0).getFechaHoraProcesado().toLocalDate();
                    for (Pedido p : ((Cliente) u).getPedidos()){
                        if (p.getFechaHoraProcesado().toLocalDate().isBefore(minFechaCliente)){
                            minFechaCliente = p.getFechaHoraProcesado().toLocalDate();
                        }
                    }
                    u.setFechaRegistro(minFechaCliente);
                    usuarioRepository.save(u);
                }
            }else{
                u.setFechaRegistro(fFin.minusDays(intervalo+1));
                usuarioRepository.save(u);
            }
        }
    }
}
