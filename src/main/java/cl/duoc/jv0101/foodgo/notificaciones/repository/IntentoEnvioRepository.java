package cl.duoc.jv0101.foodgo.notificaciones.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.notificaciones.model.IntentoEnvio;

public interface IntentoEnvioRepository extends JpaRepository<IntentoEnvio, Long> {
    List<IntentoEnvio> findByNotificacion_Id(Long notificacionId);
}
