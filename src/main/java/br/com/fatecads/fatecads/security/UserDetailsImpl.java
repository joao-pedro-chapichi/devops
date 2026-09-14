package br.com.fatecads.fatecads.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.fatecads.fatecads.entity.Usuario;



public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;
 
    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }
   
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = usuario.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        } else if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }
        return List.of(new SimpleGrantedAuthority(role));
    }
 
    @Override
    public String getPassword() {
        return usuario.getSenhaUsuario();
    }
 
    @Override
    public String getUsername() {
        return usuario.getLoginUsuario();
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
