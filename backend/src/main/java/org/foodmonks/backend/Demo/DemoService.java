package org.foodmonks.backend.Demo;

import lombok.extern.slf4j.Slf4j;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoRepository;
import org.foodmonks.backend.Reclamo.ReclamoRepository;
import org.foodmonks.backend.Usuario.Usuario;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Long intervalo = ChronoUnit.DAYS.between(fFin, fInit);
        List<Usuario> usuarioList = usuarioRepository.findAll();

        Random generador = new Random(1);

        Integer cantDias = (int)Math.floor((generador.nextDouble() * (intervalo - 1)) + 1);
        // Ciclo por usuarios para no obtener pedidos que aleatoriamente queden con fechas anteriores a su registro.
        for (Usuario u : usuarioList){
            u.setFechaRegistro(u.getFechaRegistro().minusDays(cantDias));
            usuarioRepository.save(u);
            if (u instanceof Cliente){
                if (!((Cliente) u).getPedidos().isEmpty()){
                    for (Pedido p : ((Cliente) u).getPedidos()){
                        p.setFechaHoraEntrega(p.getFechaHoraEntrega().minusDays(cantDias));
                        p.setFechaHoraProcesado(p.getFechaHoraProcesado().minusDays(cantDias));
                        if (p.getReclamo()!=null){
                            p.getReclamo().setFecha(p.getReclamo().getFecha().minusDays(cantDias));
                            // reclamoRepository.save(p.getReclamo());
                        }
                        pedidoRepository.save(p);
                    }
                }
            }
            cantDias = (int)Math.floor((generador.nextDouble() * (intervalo - 1)) + 1);
        }
    }
}
