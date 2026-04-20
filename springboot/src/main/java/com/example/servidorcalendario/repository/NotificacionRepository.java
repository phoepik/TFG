package com.example.servidorcalendario.repository;

import com.example.servidorcalendario.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByIdUsuarioOrderByFechaCreacionDesc(Integer idUsuario);
    List<Notificacion> findByIdUsuarioAndLeidaFalseOrderByFechaCreacionDesc(Integer idUsuario);
    long countByIdUsuarioAndLeidaFalse(Integer idUsuario);
}
