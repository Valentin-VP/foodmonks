package org.foodmonks.backend.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService customService;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private AuthenticationEntry authenticationEntry;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(customService).passwordEncoder(passwordEncoder());

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling()
                .authenticationEntryPoint(authenticationEntry).and()
                .authorizeRequests((request) -> request.antMatchers("/api/v1/auth/login", "/v3/api-docs.yaml", "/v3/api-docs").permitAll()
                        .antMatchers("/api/v1/password/recuperacion/*").permitAll()
                        .antMatchers("/api/v1/cliente/altaCliente").permitAll()
                        .antMatchers("/api/v1/restaurante/crearSolicitudAltaRestaurante").permitAll()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .antMatchers("/api/v1/admin").hasRole("ADMIN")
                        .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
//                        .antMatchers("/api/v1/cliente").hasRole("CLIENTE")
                        .antMatchers("/api/v1/cliente/**").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/restaurante").hasRole("RESTAURANTE")
                        .antMatchers("/api/v1/restaurante/**").hasRole("RETAURANTE")
//                        .antMatchers("/api/v1/cliente/eliminarCuenta").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/paypal/order/**").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/cliente/agregarReclamo").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/admin/listarUsuarios").hasRole("ADMIN")
//                        .antMatchers("/api/v1/cliente/realizarPedido").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/cliente/calificarRestaurante").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/cliente/modificarCalificacionRestaurante").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/cliente/eliminarCalificacionRestaurante").hasRole("CLIENTE")
//                        .antMatchers("/api/v1/restaurante/calificarCliente").hasRole("RESTAURANTE")
//                        .antMatchers("/api/v1/restaurante/modificarCalificacionCliente").hasRole("RESTAURANTE")
//                        .antMatchers("/api/v1/restaurante/eliminarCalificacionCliente").hasRole("RESTAURANTE")
//                        .antMatchers("/api/v1/restaurante/realizarDevolucion").hasRole("RESTAURANTE")
                        .anyRequest().authenticated())
                .addFilterBefore(new AuthenticationFilter(customService, tokenHelper),
                        UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable();
        http.cors().and().headers().frameOptions().disable();

    }
}
