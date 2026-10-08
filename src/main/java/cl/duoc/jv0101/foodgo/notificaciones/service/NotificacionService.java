package cl.duoc.jv0101.foodgo.notificaciones.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.notificaciones.model.Notificacion;
import cl.duoc.jv0101.foodgo.notificaciones.repository.NotificacionRepository;

@Service
@Transactional
public class NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionService(NotificacionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Notificacion> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Notificacion> findById(Long id) {
        return repository.findById(id);
    }

    public Notificacion create(Notificacion recurso) {
        recurso.setId(null);
        return repository.save(recurso);
    }

    public Optional<Notificacion> update(Long id, Notificacion datos) {
        return repository.findById(id).map(existente -> {
            existente.setDestinatario(datos.getDestinatario());
            existente.setCanal(datos.getCanal());
            existente.setMensaje(datos.getMensaje());
            return repository.save(existente);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            return true;
        }).orElse(false);
    }
}
