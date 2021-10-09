package org.foodmonks.backend.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomService implements UserDetailsService {

    //@Autowired
    //repo

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub

        Usuario usuario=repo.findByUserName(username);

        if(null==usuario) {
            throw new UsernameNotFoundException("User Not Found with userName "+username);
        }
        return usuario;
    }
}
