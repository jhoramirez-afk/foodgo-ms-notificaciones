package cl.duoc.jv0101.foodgo.notificaciones.service;

import java.util.List;
import cl.duoc.jv0101.foodgo.notificaciones.exception.BusinessRuleException;
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
        recurso.getIntentos().forEach(item -> item.setId(null));
        validarDestinatario(recurso);
        return repository.save(recurso);
    }

    public Optional<Notificacion> update(Long id, Notificacion datos) {
        return repository.findById(id).map(existente -> {
            validarDestinatario(datos);
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
    private void validarDestinatario(Notificacion datos) {
        boolean valido = "EMAIL".equals(datos.getCanal())
                ? datos.getDestinatario().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                : datos.getDestinatario().matches("\\+[1-9]\\d{7,14}");
        if (!valido) {
            throw new BusinessRuleException("destinatario",
                    "EMAIL requiere un correo válido; SMS requiere teléfono internacional, por ejemplo +56912345678");
        }
    }
}
