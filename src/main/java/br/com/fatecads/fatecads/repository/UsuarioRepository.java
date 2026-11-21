package br.com.fatecads.fatecads.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLoginUsuario(String loginUsuario);

    Optional<Usuario> findByEmailUsuario(String emailUsuario);

    boolean existsByLoginUsuarioAndIdUsuarioNot(String loginUsuario, Integer idUsuario);

    boolean existsByEmailUsuarioAndIdUsuarioNot(String emailUsuario, Integer idUsuario);
}
