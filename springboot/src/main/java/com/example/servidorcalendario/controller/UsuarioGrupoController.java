package com.example.servidorcalendario.controller;

import com.example.servidorcalendario.model.UsuarioGrupo;
import com.example.servidorcalendario.repository.UsuarioGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios-grupos")
public class UsuarioGrupoController {

    @Autowired
    private UsuarioGrupoRepository repo;

    // POST /api/usuarios-grupos
    @PostMapping
    public UsuarioGrupo crear(@RequestBody UsuarioGrupo ug) {
        return repo.save(ug);
    }

    // GET /api/usuarios-grupos/grupo/{id}
    @GetMapping("/grupo/{id}")
    public List<UsuarioGrupo> miembrosDeGrupo(@PathVariable Integer id) {
        return repo.findByIdGrupo(id);
    }

    // GET /api/usuarios-grupos/usuario/{id}
    @GetMapping("/usuario/{id}")
    public List<UsuarioGrupo> gruposDeUsuario(@PathVariable Integer id) {
        return repo.findByIdUsuario(id);
    }

    // GET /api/usuarios-grupos/existe?idUsuario=1&idGrupo=2
    @GetMapping("/existe")
    public Map<String, Boolean> esMiembro(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idGrupo) {
        boolean existe = repo.existsByIdUsuarioAndIdGrupo(idUsuario, idGrupo);
        return Map.of("esMiembro", existe);
    }

    // DELETE /api/usuarios-grupos?idUsuario=1&idGrupo=2
    @DeleteMapping
    public ResponseEntity<Void> eliminar(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idGrupo) {
        UsuarioGrupo.UsuarioGrupoId id = new UsuarioGrupo.UsuarioGrupoId(idUsuario, idGrupo);
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
