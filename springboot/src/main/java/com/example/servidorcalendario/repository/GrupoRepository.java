package com.example.servidorcalendario.repository;

import com.example.servidorcalendario.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    List<Grupo> findByIdAdmin(Integer idAdmin);
}
