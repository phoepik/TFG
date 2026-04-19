package com.example.servidorcalendario.repository;

import com.example.servidorcalendario.model.Calendario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarioRepository extends JpaRepository<Calendario, Integer> {
    List<Calendario> findByIdPropietario(Integer idPropietario);
    List<Calendario> findByIdGrupo(Integer idGrupo);
}
