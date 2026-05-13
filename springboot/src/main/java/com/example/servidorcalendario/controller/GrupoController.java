package com.example.servidorcalendario.controller;

import com.example.servidorcalendario.model.Grupo;
import com.example.servidorcalendario.model.UsuarioGrupo;
import com.example.servidorcalendario.repository.GrupoRepository;
import com.example.servidorcalendario.repository.UsuarioGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository repo;

    @Autowired
    private UsuarioGrupoRepository ugRepo;

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
    // Devuelve los grupos donde el usuario es admin O miembro
    @GetMapping("/usuario/{id}")
    public List<Grupo> porUsuario(@PathVariable Integer id) {
        LinkedHashMap<Integer, Grupo> mapa = new LinkedHashMap<>();
        // Grupos donde es admin
        repo.findByIdAdmin(id).forEach(g -> mapa.put(g.getIdGrupo(), g));
        // Grupos donde es miembro (via usuarios_grupos)
        List<UsuarioGrupo> membresías = ugRepo.findByIdUsuario(id);
        for (UsuarioGrupo ug : membresías) {
            if (!mapa.containsKey(ug.getIdGrupo())) {
                repo.findById(ug.getIdGrupo()).ifPresent(g -> mapa.put(g.getIdGrupo(), g));
            }
        }
        return new ArrayList<>(mapa.values());
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
