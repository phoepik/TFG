package com.example.servidorcalendario.controller;

import com.example.servidorcalendario.model.Grupo;
import com.example.servidorcalendario.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository repo;

    // POST /api/grupos
    @PostMapping
    public Grupo crear(@RequestBody Grupo g) {
        return repo.save(g);
    }

    // GET /api/grupos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Grupo> obtenerPorId(@PathVariable Integer id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/grupos/usuario/{id}
    @GetMapping("/usuario/{id}")
    public List<Grupo> porUsuario(@PathVariable Integer id) {
        return repo.findByIdAdmin(id);
    }

    // PUT /api/grupos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Grupo> actualizar(@PathVariable Integer id, @RequestBody Grupo datos) {
        return repo.findById(id).map(g -> {
            g.setNombre(datos.getNombre());
            g.setDescripcion(datos.getDescripcion());
            g.setIdAdmin(datos.getIdAdmin());
            return ResponseEntity.ok(repo.save(g));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/grupos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Integer id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
