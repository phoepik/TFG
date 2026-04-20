package com.example.servidorcalendario.controller;

import com.example.servidorcalendario.model.Calendario;
import com.example.servidorcalendario.repository.CalendarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendarios")
public class CalendarioController {

    @Autowired
    private CalendarioRepository repo;

    // POST /api/calendarios
    @PostMapping
    public Calendario crear(@RequestBody Calendario c) {
        return repo.save(c);
    }

    // GET /api/calendarios/propietario/{id}
    @GetMapping("/propietario/{id}")
    public List<Calendario> porPropietario(@PathVariable Integer id) {
        return repo.findByIdPropietario(id);
    }

    // GET /api/calendarios/grupo/{id}
    @GetMapping("/grupo/{id}")
    public List<Calendario> porGrupo(@PathVariable Integer id) {
        return repo.findByIdGrupo(id);
    }

    // GET /api/calendarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Calendario> obtenerPorId(@PathVariable Integer id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/calendarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Integer id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
