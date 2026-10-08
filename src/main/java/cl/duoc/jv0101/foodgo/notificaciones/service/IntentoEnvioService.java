package cl.duoc.jv0101.foodgo.notificaciones.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.notificaciones.exception.ResourceNotFoundException;
import cl.duoc.jv0101.foodgo.notificaciones.model.IntentoEnvio;
import cl.duoc.jv0101.foodgo.notificaciones.model.Notificacion;
import cl.duoc.jv0101.foodgo.notificaciones.repository.IntentoEnvioRepository;
import cl.duoc.jv0101.foodgo.notificaciones.repository.NotificacionRepository;

@Service
@Transactional
public class IntentoEnvioService {

    private final IntentoEnvioRepository repository;
    private final NotificacionRepository notificacionRepository;

    public IntentoEnvioService(IntentoEnvioRepository repository, NotificacionRepository notificacionRepository) {
        this.repository = repository;
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional(readOnly = true)
    public List<IntentoEnvio> findByNotificacionId(Long notificacionId) {
        if (!notificacionRepository.existsById(notificacionId)) {
            throw new ResourceNotFoundException("Notificacion no encontrado con id " + notificacionId);
        }
        return repository.findByNotificacion_Id(notificacionId);
    }

    @Transactional(readOnly = true)
    public IntentoEnvio findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IntentoEnvio no encontrado con id " + id));
    }

    public IntentoEnvio create(Long notificacionId, IntentoEnvio recurso) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrado con id " + notificacionId));
        recurso.setId(null);
        recurso.setNotificacion(notificacion);
        return repository.save(recurso);
    }

    public IntentoEnvio update(Long id, IntentoEnvio datos) {
        IntentoEnvio existente = findById(id);
        existente.setResultado(datos.getResultado());
        existente.setFechaHora(datos.getFechaHora());
        existente.setDetalle(datos.getDetalle());
        return repository.save(existente);
    }

    public void delete(Long id) {
        IntentoEnvio existente = findById(id);
        repository.delete(existente);
    }
}
