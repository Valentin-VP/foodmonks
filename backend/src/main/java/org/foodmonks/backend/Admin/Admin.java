package org.foodmonks.backend.Admin;

import org.foodmonks.backend.Usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("admin")
public class Admin extends Usuario {

    private String rol = "ROLE_ADMIN";

    public Admin() {
    }

    public Admin(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] roles = new String[0];
        roles[0] = this.rol;
        Set<SimpleGrantedAuthority> rol = Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
        return rol;
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
