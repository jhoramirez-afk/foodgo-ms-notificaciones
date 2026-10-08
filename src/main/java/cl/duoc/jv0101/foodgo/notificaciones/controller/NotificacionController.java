package cl.duoc.jv0101.foodgo.notificaciones.controller;

import cl.duoc.jv0101.foodgo.notificaciones.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.jv0101.foodgo.notificaciones.model.Notificacion;
import cl.duoc.jv0101.foodgo.notificaciones.service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtener(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrado con id " + id));
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@Valid @RequestBody Notificacion recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(recurso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Long id,
            @Valid @RequestBody Notificacion datos) {
        return service.update(id, datos).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrado con id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.delete(id)) {
            throw new ResourceNotFoundException("Notificacion no encontrado con id " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
