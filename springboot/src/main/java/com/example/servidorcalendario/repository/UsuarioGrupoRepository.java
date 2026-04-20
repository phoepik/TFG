package com.example.servidorcalendario.repository;

import com.example.servidorcalendario.model.UsuarioGrupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo, UsuarioGrupo.UsuarioGrupoId> {
    List<UsuarioGrupo> findByIdGrupo(Integer idGrupo);
    List<UsuarioGrupo> findByIdUsuario(Integer idUsuario);
    boolean existsByIdUsuarioAndIdGrupo(Integer idUsuario, Integer idGrupo);
}
