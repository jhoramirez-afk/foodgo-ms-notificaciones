package cl.duoc.jv0101.foodgo.notificaciones.controller;

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
import cl.duoc.jv0101.foodgo.notificaciones.model.IntentoEnvio;
import cl.duoc.jv0101.foodgo.notificaciones.service.IntentoEnvioService;

@RestController
@RequestMapping("/api")
public class IntentoEnvioController {

    private final IntentoEnvioService service;

    public IntentoEnvioController(IntentoEnvioService service) {
        this.service = service;
    }

    @GetMapping("/notificaciones/{notificacionId}/intentos")
    public ResponseEntity<List<IntentoEnvio>> listarPorNotificacion(@PathVariable Long notificacionId) {
        return ResponseEntity.ok(service.findByNotificacionId(notificacionId));
    }

    @PostMapping("/notificaciones/{notificacionId}/intentos")
    public ResponseEntity<IntentoEnvio> crear(@PathVariable Long notificacionId, @Valid @RequestBody IntentoEnvio recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(notificacionId, recurso));
    }

    @GetMapping("/intentos/{id}")
    public ResponseEntity<IntentoEnvio> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/intentos/{id}")
    public ResponseEntity<IntentoEnvio> actualizar(@PathVariable Long id, @Valid @RequestBody IntentoEnvio datos) {
        return ResponseEntity.ok(service.update(id, datos));
    }

    @DeleteMapping("/intentos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
