package org.foodmonks.backend.Admin;

import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
@Entity
@DiscriminatorValue("admin")
public class Admin extends Usuario {

    private String roles = "ROLE_ADMIN";

    public Admin() {
        super();
    }

    public Admin(String nombre, String apellido, String correo, String contrasenia, LocalDate fechaRegistro) {
        super(nombre, apellido, correo, contrasenia, fechaRegistro);
    }

    public String getRoles() {
        return this.roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] autoridades = new String[1];
        autoridades[0] = this.roles;
        Set<SimpleGrantedAuthority> rol = Arrays.stream(autoridades)
                .map(autoridad -> new SimpleGrantedAuthority(autoridad))
                .collect(Collectors.toSet());
        return rol;
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String getPassword() {
        return getContrasenia();
    }

    @Override
    public String getUsername() {
        return getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
