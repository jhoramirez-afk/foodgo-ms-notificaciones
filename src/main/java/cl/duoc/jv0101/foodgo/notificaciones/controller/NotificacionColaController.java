package cl.duoc.jv0101.foodgo.notificaciones.controller;

import cl.duoc.jv0101.foodgo.notificaciones.service.NotificacionColaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionColaController {
    private final NotificacionColaService service;

    public NotificacionColaController(NotificacionColaService service) { this.service = service; }

    @PostMapping("/{id}/encolar")
    public ResponseEntity<NotificacionColaService.Encolada> encolar(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.encolar(id));
    }
}
