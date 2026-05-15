package com.example.servidorcalendario.controller;

import com.example.servidorcalendario.model.Notificacion;
import com.example.servidorcalendario.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository repo;

    // POST /api/notificaciones
    @PostMapping
    public Notificacion crear(@RequestBody Notificacion n) {
        n.setIdNotificacion(null); // Forzar INSERT, no UPDATE
        if (n.getFechaCreacion() == null) {
            n.setFechaCreacion(System.currentTimeMillis());
        }
        if (n.getLeida() == null) {
            n.setLeida(false);
        }
        return repo.save(n);
    }

    // GET /api/notificaciones/usuario/{id}
    @GetMapping("/usuario/{id}")
    public List<Notificacion> obtenerPorUsuario(@PathVariable Integer id) {
        return repo.findByIdUsuarioOrderByFechaCreacionDesc(id);
    }

    // GET /api/notificaciones/usuario/{id}/no-leidas
    @GetMapping("/usuario/{id}/no-leidas")
    public List<Notificacion> obtenerNoLeidas(@PathVariable Integer id) {
        return repo.findByIdUsuarioAndLeidaFalseOrderByFechaCreacionDesc(id);
    }

    // GET /api/notificaciones/usuario/{id}/contador
    @GetMapping("/usuario/{id}/contador")
    public Map<String, Long> contarNoLeidas(@PathVariable Integer id) {
        long count = repo.countByIdUsuarioAndLeidaFalse(id);
        return Map.of("noLeidas", count);
    }

    // PUT /api/notificaciones/{id}/leida
    @PutMapping("/{id}/leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable Integer id) {
        return repo.findById(id).map(n -> {
            n.setLeida(true);
            repo.save(n);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/notificaciones/usuario/{id}/leer-todas
    @PutMapping("/usuario/{id}/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas(@PathVariable Integer id) {
        List<Notificacion> noLeidas = repo.findByIdUsuarioAndLeidaFalseOrderByFechaCreacionDesc(id);
        noLeidas.forEach(n -> n.setLeida(true));
        repo.saveAll(noLeidas);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private com.example.servidorcalendario.repository.UsuarioGrupoRepository ugRepo;

    // PUT /api/notificaciones/{id}/invitacion
    @PutMapping("/{id}/invitacion")
    public ResponseEntity<Void> actualizarInvitacion(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        return repo.findById(id).map(n -> {
            String estado = body.get("estado");
            n.setEstadoInvitacion(estado);
            n.setLeida(true);
            repo.save(n);

            // Si acepta, añadir al usuario al grupo
            if ("ACEPTADA".equals(estado) && n.getIdGrupoInvitacion() != null && n.getIdUsuario() != null) {
                com.example.servidorcalendario.model.UsuarioGrupo ug = new com.example.servidorcalendario.model.UsuarioGrupo();
                ug.setIdUsuario(n.getIdUsuario());
                ug.setIdGrupo(n.getIdGrupoInvitacion());
                ugRepo.save(ug);
            }

            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/notificaciones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Integer id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/notificaciones/usuario/{id}
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> borrarTodasDeUsuario(@PathVariable Integer id) {
        List<Notificacion> todas = repo.findByIdUsuarioOrderByFechaCreacionDesc(id);
        repo.deleteAll(todas);
        return ResponseEntity.ok().build();
    }
}