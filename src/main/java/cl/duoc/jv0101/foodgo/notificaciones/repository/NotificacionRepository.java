package cl.duoc.jv0101.foodgo.notificaciones.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import cl.duoc.jv0101.foodgo.notificaciones.model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    @Override
    @EntityGraph(attributePaths = "intentos")
    List<Notificacion> findAll();

    @Override
    @EntityGraph(attributePaths = "intentos")
    Optional<Notificacion> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from Notificacion n where n.id = :id")
    Optional<Notificacion> findForUpdate(@Param("id") Long id);
}
