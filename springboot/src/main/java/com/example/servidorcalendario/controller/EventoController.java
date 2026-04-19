package com.example.servidorcalendario.controller;

import com.example.servidorcalendario.model.Evento;
import com.example.servidorcalendario.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoRepository repo;

    // POST /api/eventos
    @PostMapping
    public Evento crear(@RequestBody Evento e) {
        return repo.save(e);
    }

    // GET /api/eventos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerPorId(@PathVariable Integer id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/eventos/calendario/{id}
    @GetMapping("/calendario/{id}")
    public List<Evento> porCalendario(@PathVariable Integer id) {
        return repo.findByIdCalendario(id);
    }

    // PUT /api/eventos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizar(@PathVariable Integer id, @RequestBody Evento datos) {
        return repo.findById(id).map(e -> {
            e.setTitulo(datos.getTitulo());
            e.setDescripcion(datos.getDescripcion());
            e.setFechaInicio(datos.getFechaInicio());
            e.setFechaFin(datos.getFechaFin());
            e.setUbicacion(datos.getUbicacion());
            e.setEstado(datos.getEstado());
            e.setIdCalendario(datos.getIdCalendario());
            return ResponseEntity.ok(repo.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/eventos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Integer id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
