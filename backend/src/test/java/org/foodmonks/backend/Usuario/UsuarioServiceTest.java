package org.foodmonks.backend.Usuario;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteRepository;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoBloqueadoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoDesbloqueadoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoEliminadoException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    @Mock
    RestauranteService restauranteService;
    @Spy
    UsuarioConverter usuarioConverter;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, templateEngine, emailService, passwordEncoder, clienteRepository, restauranteRepository, usuarioConverter, restauranteService);
    }

    @Test
    void listarUsuarios() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.5f,10,
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
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.5f,10,
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
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.5f,10,
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
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.5f,10,
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
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.5f,10,
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
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1));

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
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1));

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
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1);
        result = usuarioService.listarUsuarios("","","","","DESBLOQUEADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListBloqueado);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 2);
        expectedJsonObject.addProperty("totalPages", 1);

        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1);
        result = usuarioService.listarUsuarios("","","","","BLOQUEADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);

        expectedJsonObject = usuarioConverter.listaJsonUsuarioPaged(expectedListEliminado);
        expectedJsonObject.addProperty("currentPage", 0);
        expectedJsonObject.addProperty("totalItems", 2);
        expectedJsonObject.addProperty("totalPages", 1);

        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1, restaurante2, restaurante3, restaurante4, cliente1, cliente2, cliente3, admin1);
        result = usuarioService.listarUsuarios("","","","","ELIMINADO",false,"");
        assertThat(result).isEqualTo(expectedJsonObject);
    }

    @Test
    void listarUsuarios_orden_tipoUser() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                3.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante4 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante4@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2010, 1, 1),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Cliente cliente2 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente2@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1), 5.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        Cliente cliente3 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente3@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.5f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1));

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

    @Test
    void bloquearUsuario_Cliente() throws UsuarioNoEncontradoException, UsuarioNoBloqueadoException, EmailNoEnviadoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(cliente1);

        usuarioService.bloquearUsuario("dummy");
        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(usuarioRepository).save(clienteArgumentCaptor.capture());

        assertThat(clienteArgumentCaptor.getValue().getEstado()).isEqualTo(EstadoCliente.BLOQUEADO);
    }

    @Test
    void bloquearUsuario_Cliente_EstadoIncorrecto() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(cliente1);

        assertThatThrownBy(()->usuarioService.bloquearUsuario("dummy"))
                .isInstanceOf(UsuarioNoBloqueadoException.class)
                .hasMessageContaining("Usuario dummy no pudo ser bloqueado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void bloquearUsuario_Restaurante() throws UsuarioNoEncontradoException, UsuarioNoBloqueadoException, EmailNoEnviadoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);

        usuarioService.bloquearUsuario("dummy");
        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(usuarioRepository).save(restauranteArgumentCaptor.capture());

        assertThat(restauranteArgumentCaptor.getValue().getEstado()).isEqualTo(EstadoRestaurante.BLOQUEADO);
    }

    @Test
    void bloquearUsuario_Restaurante_EstadoIncorrecto() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ELIMINADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);

        assertThatThrownBy(()->usuarioService.bloquearUsuario("dummy"))
                .isInstanceOf(UsuarioNoBloqueadoException.class)
                .hasMessageContaining("Usuario dummy no pudo ser bloqueado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void bloquearUsuario_Inexistente() throws EmailNoEnviadoException {
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);

        assertThatThrownBy(()->usuarioService.bloquearUsuario("dummy"))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessageContaining("Usuario dummy no encontrado");
        verify(usuarioRepository, never()).save(any());
        verify(emailService, never()).enviarMail(anyString(), anyString(), anyString(), any());
    }

    @Test
    void desbloquearUsuario_Cliente() throws UsuarioNoDesbloqueadoException, UsuarioNoEncontradoException, EmailNoEnviadoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(cliente1);

        usuarioService.desbloquearUsuario("dummy");
        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(usuarioRepository).save(clienteArgumentCaptor.capture());

        assertThat(clienteArgumentCaptor.getValue().getEstado()).isEqualTo(EstadoCliente.ACTIVO);
    }

    @Test
    void desbloquearUsuario_Cliente_EstadoIncorrecto() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ELIMINADO, null, null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(cliente1);

        assertThatThrownBy(()->usuarioService.desbloquearUsuario("dummy"))
                .isInstanceOf(UsuarioNoDesbloqueadoException.class)
                .hasMessageContaining("Usuario dummy debe estar bloqueado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void desbloquearUsuario_Restaurante() throws UsuarioNoDesbloqueadoException, UsuarioNoEncontradoException, EmailNoEnviadoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);

        usuarioService.desbloquearUsuario("dummy");
        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(usuarioRepository).save(restauranteArgumentCaptor.capture());

        assertThat(restauranteArgumentCaptor.getValue().getEstado()).isEqualTo(EstadoRestaurante.CERRADO);
    }

    @Test
    void desbloquearUsuario_Restaurante_EstadoIncorrecto() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.PENDIENTE, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);

        assertThatThrownBy(()->usuarioService.desbloquearUsuario("dummy"))
                .isInstanceOf(UsuarioNoDesbloqueadoException.class)
                .hasMessageContaining("Usuario dummy debe estar bloqueado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void desbloquearUsuario_Inexistente() throws EmailNoEnviadoException {
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);

        assertThatThrownBy(()->usuarioService.desbloquearUsuario("dummy"))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessageContaining("Usuario dummy no encontrado");
        verify(usuarioRepository, never()).save(any());
        verify(emailService, never()).enviarMail(anyString(), anyString(), anyString(), any());
    }

    @Test
    void eliminarUsuario_Cliente() throws UsuarioNoEncontradoException, UsuarioNoEliminadoException, EmailNoEnviadoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.BLOQUEADO, null, null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(cliente1);

        usuarioService.eliminarUsuario("dummy");
        ArgumentCaptor<Cliente> clienteArgumentCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(usuarioRepository).save(clienteArgumentCaptor.capture());

        assertThat(clienteArgumentCaptor.getValue().getEstado()).isEqualTo(EstadoCliente.ELIMINADO);
    }

    @Test
    void eliminarUsuario_Cliente_EstadoIncorrecto() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 1, 1), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(cliente1);

        assertThatThrownBy(()->usuarioService.eliminarUsuario("dummy"))
                .isInstanceOf(UsuarioNoEliminadoException.class)
                .hasMessageContaining("Usuario dummy debe estar bloqueado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminarUsuario_Restaurante() throws UsuarioNoEncontradoException, EmailNoEnviadoException, UsuarioNoEliminadoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);

        usuarioService.eliminarUsuario("dummy");
        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(usuarioRepository).save(restauranteArgumentCaptor.capture());

        assertThat(restauranteArgumentCaptor.getValue().getEstado()).isEqualTo(EstadoRestaurante.ELIMINADO);
    }

    @Test
    void eliminarUsuario_Restaurante_EstadoIncorrecto() {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");

        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 1, 1),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.CERRADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(restaurante1);

        assertThatThrownBy(()->usuarioService.eliminarUsuario("dummy"))
                .isInstanceOf(UsuarioNoEliminadoException.class)
                .hasMessageContaining("Usuario dummy debe estar bloqueado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminarUsuario_Admin() throws UsuarioNoEncontradoException, EmailNoEnviadoException, UsuarioNoEliminadoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1));
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(admin1);

        usuarioService.eliminarUsuario("dummy");
        ArgumentCaptor<Admin> adminArgumentCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(usuarioRepository).delete(adminArgumentCaptor.capture());

        assertThat(adminArgumentCaptor.getValue().getCorreo()).isEqualTo("admin1@gmail.com");
    }

    @Test
    void eliminarUsuario_Inexistente() throws EmailNoEnviadoException {
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);

        assertThatThrownBy(()->usuarioService.eliminarUsuario("dummy"))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessageContaining("Usuario dummy no encontrado");
        verify(usuarioRepository, never()).save(any());
        verify(emailService, never()).enviarMail(anyString(), anyString(), anyString(), any());
    }

    @Test
    void cambiarPassword() throws UsuarioNoEncontradoException {
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("a");
        Admin admin1 =  new Admin("nombreDelAdmin",
                "apellidoDelAdmin",
                "admin1@gmail.com",
                passwordEncoder.encode("a"),
                LocalDate.of(2010, 1, 1));
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(admin1);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("b");

        usuarioService.cambiarPassword("dummy", "dummy");

        ArgumentCaptor<Admin> adminArgumentCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(usuarioRepository).save(adminArgumentCaptor.capture());

        assertThat(adminArgumentCaptor.getValue().getContrasenia()).isEqualTo("b");
    }

    @Test
    void cambiarPassword_Inexistente() {
        when(usuarioRepository.findByCorreoIgnoreCase(anyString())).thenReturn(null);

        assertThatThrownBy(()->usuarioService.cambiarPassword("dummy", "dummy"))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessageContaining("No existe el Usuario dummy");
        verify(passwordEncoder, never()).encode(any(CharSequence.class));
        verify(usuarioRepository, never()).save(any());
    }

}