package cl.duoc.jv0101.foodgo.notificaciones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@SpringBootTest(properties = "foodgo.cola.url=https://sqs.us-east-1.amazonaws.com/000000000000/pruebas")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ColaIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean SqsClient sqs;

    private long crearNotificacion() throws Exception {
        String body = mvc.perform(post("/api/notificaciones").contentType("application/json")
                .content("""
                    {"destinatario":"camila.soto@example.com","canal":"EMAIL","mensaje":"Camila, tu pedido esta en camino."}
                    """))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).path("id").longValue();
    }

    @Test void publicaEnSqsYDevuelve202SinExponerDatosPersonales() throws Exception {
        long id = crearNotificacion();
        when(sqs.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(SendMessageResponse.builder().messageId("mensaje-confirmado").build());
        String response = mvc.perform(post("/api/notificaciones/" + id + "/encolar"))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.estado").value("ENCOLADO"))
                .andExpect(jsonPath("$.mensajeId").value("mensaje-confirmado"))
                .andReturn().getResponse().getContentAsString();
        var captured = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqs).sendMessage(captured.capture());
        var event = mapper.readTree(captured.getValue().messageBody());
        assertEquals(id, event.path("notificacionId").longValue());
        assertEquals(1, event.path("version").intValue());
        assertEquals(mapper.readTree(response).path("eventoId").asText(), event.path("eventoId").asText());
        assertFalse(event.has("destinatario"));
        assertFalse(event.has("mensaje"));
    }

    @Test void unErrorDeSqsDevuelve503YNoFingeUnaEntrega() throws Exception {
        long id = crearNotificacion();
        when(sqs.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(SdkClientException.create("Detalle confidencial del proveedor"));
        mvc.perform(post("/api/notificaciones/" + id + "/encolar"))
                .andExpect(status().isServiceUnavailable()).andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").value("La cola de notificaciones no esta disponible. Intente nuevamente."));
        mvc.perform(get("/api/notificaciones/" + id + "/intentos"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void noPublicaUnaNotificacionInexistente() throws Exception {
        mvc.perform(post("/api/notificaciones/9223372036854775807/encolar"))
                .andExpect(status().isNotFound());
        verifyNoInteractions(sqs);
    }

    @Test void laReentregaDelMismoEventoNoDuplicaIntentosEnLaBase() throws Exception {
        long id = crearNotificacion();
        String eventId = UUID.randomUUID().toString();
        String body = """
                {"eventoId":"%s","resultado":"ENVIADO","fechaHora":"2026-10-01T13:30:00","detalle":"Simulacion academica desde Lambda."}
                """.formatted(eventId);
        String path = "/api/notificaciones/" + id + "/intentos";
        String first = mvc.perform(post(path).contentType("application/json").content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String second = mvc.perform(post(path).contentType("application/json").content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        assertEquals(mapper.readTree(first).path("id").longValue(), mapper.readTree(second).path("id").longValue());
        mvc.perform(get(path)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        verifyNoInteractions(sqs);
    }

    @Test void rechazaUnIdentificadorDeEventoMalformado() throws Exception {
        long id = crearNotificacion();
        mvc.perform(post("/api/notificaciones/" + id + "/intentos").contentType("application/json")
                .content("""
                    {"eventoId":"incorrecto","resultado":"ENVIADO","fechaHora":"2026-10-01T13:30:00","detalle":"Simulacion academica."}
                    """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors.eventoId").exists());
    }

    @Test void dosConsumidoresConcurrentesCreanUnSoloIntento() throws Exception {
        long id = crearNotificacion();
        String body = """
                {"eventoId":"%s","resultado":"ENVIADO","fechaHora":"2026-10-01T13:30:00","detalle":"Simulacion academica concurrente."}
                """.formatted(UUID.randomUUID());
        String path = "/api/notificaciones/" + id + "/intentos";
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<Long> request = () -> {
                ready.countDown();
                if (!start.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("No iniciaron ambos consumidores");
                String response = mvc.perform(post(path).contentType("application/json").content(body))
                        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
                return mapper.readTree(response).path("id").longValue();
            };
            var first = executor.submit(request);
            var second = executor.submit(request);
            try {
                assertTrue(ready.await(5, TimeUnit.SECONDS));
            } finally {
                start.countDown();
            }
            assertEquals(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS));
        }
        mvc.perform(get(path)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
}
