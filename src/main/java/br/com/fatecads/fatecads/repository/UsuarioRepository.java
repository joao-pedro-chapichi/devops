package br.com.fatecads.fatecads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.fatecads.fatecads.entity.Usuario;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    
    Optional<Usuario> findByLoginUsuario(String loginUsuario);
}