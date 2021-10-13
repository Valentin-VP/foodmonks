package org.foodmonks.backend;

import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteRepository;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@SpringBootApplication
public class BackendApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ClienteRepository clienteRepository;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Cliente cliente = new Cliente(
			"manuel",
			"montxito",
			"manuel@gmail.com",
			passwordEncoder.encode("contrasenia"),
			LocalDate.now().minusYears(20),
			1.0f,
			null,
			EstadoCliente.ACTIVO,
			null
		);

		clienteRepository.save(cliente);

	}

}
