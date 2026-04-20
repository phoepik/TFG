package com.example.servidorcalendario.repository;

import com.example.servidorcalendario.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findByIdCalendario(Integer idCalendario);
}
