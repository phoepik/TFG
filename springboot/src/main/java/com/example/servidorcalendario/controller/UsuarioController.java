package com.example.servidorcalendario.controller;

import com.example.servidorcalendario.model.Usuario;
import com.example.servidorcalendario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    // POST /api/usuarios/registro
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registro(@RequestBody Usuario u) {
        if (repo.findByEmail(u.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.save(u));
    }

    // POST /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario u) {
        return repo.findByEmail(u.getEmail())
                .filter(user -> user.getContrasena().equals(u.getContrasena()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/usuarios/{id}/notificaciones
    @PutMapping("/{id}/notificaciones")
    public ResponseEntity<Void> actualizarNotificaciones(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> body) {
        return repo.findById(id).map(u -> {
            u.setNotificacionesActivas(body.get("activas"));
            repo.save(u);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
