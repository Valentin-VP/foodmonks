package org.foodmonks.backend.authentication;


import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    private TokenHelper tokenHelper;
    private UserDetailsService customService;
    private boolean testing = true;

    public AuthenticationFilter(UserDetailsService customService, TokenHelper tokenHelper) {
        this.tokenHelper = tokenHelper;
        this.customService = customService;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String authToken=tokenHelper.getToken(request);
        String refreshToken=tokenHelper.getRefreshToken(request);
        if(!testing) {
            System.out.println(authToken);
            this.testing = true;
        } else {
            this.testing = false;
        }

        if(authToken != null || refreshToken != null) {

            String userName=tokenHelper.getUsernameFromToken(authToken);
            String userNameRefresh = tokenHelper.getUsernameFromToken(refreshToken);

            if(userName != null || userNameRefresh != null) {
                UserDetails userDetails;
                try {
                    userDetails=customService.loadUserByUsername(userName);
                } catch(Exception e) {
                    userDetails=customService.loadUserByUsername(userNameRefresh);
                }

                if(tokenHelper.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } else {
                    //String refreshToken=tokenHelper.getRefreshToken(request);
                    if(tokenHelper.validateToken(refreshToken, userDetails)) {

                        String jwtToken=tokenHelper.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
                        String jwtRefreshToken=tokenHelper.generateRefreshToken(userDetails.getUsername(), userDetails.getAuthorities());
                        response.setHeader("Authorization", jwtToken);
                        response.setHeader("RefreshAuthentication", jwtRefreshToken);

                        UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }

            }

        }

        filterChain.doFilter(request, response);



    }
}
