package com.example.servidorcalendario.repository;

import com.example.servidorcalendario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByEmailContainingIgnoreCase(String fragmento);
    List<Usuario> findByNombreContainingIgnoreCase(String fragmento);
}
