package cl.duoc.jv0101.foodgo.notificaciones.service;

import cl.duoc.jv0101.foodgo.notificaciones.exception.ColaNoDisponibleException;
import cl.duoc.jv0101.foodgo.notificaciones.exception.ResourceNotFoundException;
import cl.duoc.jv0101.foodgo.notificaciones.repository.NotificacionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class NotificacionColaService {
    public record Encolada(long notificacionId, String eventoId, String mensajeId, String estado) {}

    private final NotificacionRepository repository;
    private final ObjectMapper mapper;
    private final SqsClient sqs;
    private final String queueUrl;

    public NotificacionColaService(NotificacionRepository repository, ObjectMapper mapper,
            ObjectProvider<SqsClient> clients, @Value("${foodgo.cola.url:}") String queueUrl) {
        this.repository = repository;
        this.mapper = mapper;
        this.sqs = clients.getIfAvailable();
        this.queueUrl = queueUrl;
    }

    public Encolada encolar(long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Notificacion no encontrado con id " + id);
        }
        if (sqs == null || queueUrl.isBlank()) throw new ColaNoDisponibleException();
        String eventoId = UUID.randomUUID().toString();
        try {
            String body = mapper.writeValueAsString(Map.of("version", 1,
                    "tipo", "NOTIFICACION_SOLICITADA", "eventoId", eventoId, "notificacionId", id));
            var result = sqs.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(body).build());
            if (result.messageId() == null || result.messageId().isBlank()) throw new ColaNoDisponibleException();
            return new Encolada(id, eventoId, result.messageId(), "ENCOLADO");
        } catch (SdkException | JsonProcessingException error) {
            // No exponer credenciales, endpoints internos ni detalles del proveedor en la respuesta HTTP.
            throw new ColaNoDisponibleException();
        }
    }
}
