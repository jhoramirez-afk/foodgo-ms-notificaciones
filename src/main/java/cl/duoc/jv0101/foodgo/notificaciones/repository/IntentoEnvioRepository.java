package cl.duoc.jv0101.foodgo.notificaciones.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.notificaciones.model.IntentoEnvio;

public interface IntentoEnvioRepository extends JpaRepository<IntentoEnvio, Long> {
    List<IntentoEnvio> findByNotificacion_Id(Long notificacionId);
    Optional<IntentoEnvio> findByNotificacion_IdAndEventoId(Long notificacionId, String eventoId);
}
