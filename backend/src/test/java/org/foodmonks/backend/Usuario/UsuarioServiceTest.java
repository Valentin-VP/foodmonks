package org.foodmonks.backend.Usuario;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteRepository;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    TemplateEngine templateEngine;
    @Mock
    EmailService emailService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    ClienteRepository clienteRepository;
    @Mock
    RestauranteRepository restauranteRepository;
    @Spy
    UsuarioConverter usuarioConverter;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, templateEngine, emailService, passwordEncoder, clienteRepository, restauranteRepository, usuarioConverter);
    }

    @Test
    void listarUsuarios() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);

        List<Usuario> expectedList = List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedList);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 7);
        expectedJsonObject.addProperty("totalPages", 2);
        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3));

        JsonObject result = usuarioService.listarUsuarios("","","","","",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_page() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);

        List<Usuario> expectedList = List.of(cliente2, cliente3);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedList);
        expectedJsonObject.addProperty("currentPage", 1);
        expectedJsonObject.addProperty("totalItems", 7);
        expectedJsonObject.addProperty("totalPages", 2);
        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3));

        JsonObject result = usuarioService.listarUsuarios("","","","","",false,"1");
        assertThat(result).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_correo() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);

        List<Usuario> expectedList = List.of(cliente1);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedList);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 1);
        expectedJsonObject.addProperty("totalPages", 1);
        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3));

        JsonObject result = usuarioService.listarUsuarios("cliente1@gmail.com","","","","",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_fechaInicio() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);

        List<Usuario> expectedList = List.of(restaurante1, restaurante3, cliente1, cliente3);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedList);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 4);
        expectedJsonObject.addProperty("totalPages", 1);
        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3));

        JsonObject result = usuarioService.listarUsuarios("","","2019-12-31","","",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_fechaInicio_fechaFin() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);

        List<Usuario> expectedList = List.of(restaurante2, restaurante4, cliente2);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedList);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 3);
        expectedJsonObject.addProperty("totalPages", 1);
        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3));

        JsonObject result = usuarioService.listarUsuarios("","","2000-01-01","2015-01-01","",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_tipoUser() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01));

        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, cliente1, cliente2, admin1));

        List<Usuario> expectedListR = List.of(restaurante1, restaurante2);
        List<Usuario> expectedListC = List.of(cliente1, cliente2);
        List<Usuario> expectedListA = List.of(admin1);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListR);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 2);
        expectedJsonObject.addProperty("totalPages", 1);


        JsonObject resultR = usuarioService.listarUsuarios("","restaurante","","","",false,"");
        assertThat(resultR).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListC);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 2);
        expectedJsonObject.addProperty("totalPages", 1);

        JsonObject resultC = usuarioService.listarUsuarios("","cliente","","","",false,"");
        assertThat(resultC).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListA);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 1);
        expectedJsonObject.addProperty("totalPages", 1);

        JsonObject resultA = usuarioService.listarUsuarios("","admin","","","",false,"");
        assertThat(resultA).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_estado() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01));

        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1));

        List<Usuario> expectedListAdmin = List.of();
        List<Usuario> expectedListDesbloqueado = List.of(restaurante1, restaurante2, cliente1);
        List<Usuario> expectedListBloqueado = List.of(restaurante3, cliente2);
        List<Usuario> expectedListEliminado = List.of(restaurante4, cliente3);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListAdmin);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 0);
        expectedJsonObject.addProperty("totalPages", 0);


        JsonObject result = usuarioService.listarUsuarios("","admin","","","BLOQUEADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);
        result = usuarioService.listarUsuarios("","admin","","","DESBLOQUEADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListDesbloqueado);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 3);
        expectedJsonObject.addProperty("totalPages", 1);
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1);
        result = usuarioService.listarUsuarios("","","","","DESBLOQUEADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListBloqueado);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 2);
        expectedJsonObject.addProperty("totalPages", 1);

        when(usuarioRepository.findByCorreo(anyString())).thenReturn(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1);
        result = usuarioService.listarUsuarios("","","","","BLOQUEADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListEliminado);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 2);
        expectedJsonObject.addProperty("totalPages", 1);

        when(usuarioRepository.findByCorreo(anyString())).thenReturn(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1);
        result = usuarioService.listarUsuarios("","","","","ELIMINADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_orden_tipoUser() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 01, 01));

        when(usuarioRepository.findAll()).thenReturn(
                List.of(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1));

        List<Usuario> expectedListOrdenAdmin = List.of(admin1);
        List<Usuario> expectedListOrdenRestaurante = List.of(restaurante3, restaurante4, restaurante1, restaurante2);
        List<Usuario> expectedListOrdenCliente = List.of(cliente2, cliente3, cliente1);
        List<Cliente> listOrdenCliente = List.of(cliente2, cliente3, cliente1);
        List<Restaurante> listOrdenRestaurante = List.of(restaurante3, restaurante4, restaurante1, restaurante2);
        JsonObject expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListOrdenAdmin);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 1);
        expectedJsonObject.addProperty("totalPages", 1);


        JsonObject result = usuarioService.listarUsuarios("","admin","","","",true,"");
        assertThat(result).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListOrdenCliente);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 3);
        expectedJsonObject.addProperty("totalPages", 1);
        when(clienteRepository.findAllByRolesOrderByCalificacionDesc(anyString())).thenReturn(listOrdenCliente);
        result = usuarioService.listarUsuarios("","cliente","","","",true,"");
        assertThat(result).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListOrdenRestaurante);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 4);
        expectedJsonObject.addProperty("totalPages", 1);
        when(restauranteRepository.findAllByRolesOrderByCalificacionDesc(anyString())).thenReturn(listOrdenRestaurante);
        result = usuarioService.listarUsuarios("","restaurante","","","",true,"");
        assertThat(result).isEqualTo(expectedJsonObject);
    }
}