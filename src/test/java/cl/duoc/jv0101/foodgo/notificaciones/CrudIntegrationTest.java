package cl.duoc.jv0101.foodgo.notificaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String unique(String json) { return json.replace("TEST20261009", UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()); }

    private long createParent() throws Exception {
        String result = mvc.perform(post("/api/notificaciones").contentType("application/json")
                .content(unique("""
{"destinatario":"camila.soto.TEST20261009@example.com","canal":"EMAIL","mensaje":"Camila, recibimos tu pedido de dos hamburguesas en La Cocina de Barrio. Total: $19.980 CLP."}
"""))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    private long createChild(String nested) throws Exception {
        String result = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"resultado":"ENVIADO","fechaHora":"2026-10-01T13:30:00","detalle":"Registro académico de entrega del aviso de confirmación."}
""")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        long id = createParent();
        String nested = "/api/notificaciones/" + id + "/intentos";
        long childId = createChild(nested);
        mvc.perform(get("/api/notificaciones/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.intentos[0].id").value(childId));
        mvc.perform(get("/api/notificaciones")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/intentos/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/notificaciones/" + id).contentType("application/json").content(unique("""
{"destinatario":"camila.soto.TEST20261009@example.com","canal":"EMAIL","mensaje":"Camila, Diego retiró tu pedido y va en camino a Los Aromos 1450."}
"""))).andExpect(status().isOk());
        mvc.perform(put("/api/intentos/" + childId).contentType("application/json").content("""
{"resultado":"ENVIADO","fechaHora":"2026-10-01T13:35:00","detalle":"Registro académico de entrega del aviso de seguimiento."}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/intentos/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/intentos/" + childId)).andExpect(status().isNotFound());
        long cascadeId = createChild(nested);
        mvc.perform(delete("/api/notificaciones/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/notificaciones/" + id)).andExpect(status().isNotFound());
        mvc.perform(get("/api/intentos/" + cascadeId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/notificaciones").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/notificaciones/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/notificaciones").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors").isNotEmpty());
        mvc.perform(get("/api/notificaciones/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/notificaciones/9223372036854775807/intentos").contentType("application/json")
                .content("""
{"resultado":"ENVIADO","fechaHora":"2026-10-01T13:30:00","detalle":"Registro académico de entrega del aviso de confirmación."}
""")).andExpect(status().isNotFound());
        var longBody = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree("""
{"destinatario":"camila.soto.TEST20261009@example.com","canal":"EMAIL","mensaje":"Camila, recibimos tu pedido de dos hamburguesas en La Cocina de Barrio. Total: $19.980 CLP."}
""");
        longBody.put("destinatario", "X".repeat(300));
        mvc.perform(post("/api/notificaciones").contentType("application/json").content(longBody.toString())).andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidInputs() {
        return Stream.of(
            Arguments.of("Correo inválido", "parent", """
{"destinatario":"camila-sin-correo","canal":"EMAIL","mensaje":"Camila, recibimos tu pedido de dos hamburguesas en La Cocina de Barrio. Total: $19.980 CLP."}
""", "destinatario"),
            Arguments.of("SMS sin teléfono internacional", "parent", """
{"destinatario":"123","canal":"SMS","mensaje":"Camila, recibimos tu pedido de dos hamburguesas en La Cocina de Barrio. Total: $19.980 CLP."}
""", "destinatario"),
            Arguments.of("Canal inválido", "parent", """
{"destinatario":"camila.soto.TEST20261009@example.com","canal":"RED_SOCIAL","mensaje":"Camila, recibimos tu pedido de dos hamburguesas en La Cocina de Barrio. Total: $19.980 CLP."}
""", "canal"),
            Arguments.of("Mensaje obligatorio", "parent", """
{"destinatario":"camila.soto.TEST20261009@example.com","canal":"EMAIL","mensaje":""}
""", "mensaje"),
            Arguments.of("Resultado inválido", "child", """
{"resultado":"MAGIA","fechaHora":"2026-10-01T13:30:00","detalle":"Registro académico de entrega del aviso de confirmación."}
""", "resultado")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidInputs")
    void businessValidationReturns400WithField(String name, String target, String body, String field) throws Exception {
        if ("parent".equals(target)) {
            mvc.perform(post("/api/notificaciones").contentType("application/json").content(unique(body)))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
        } else {
            long id = createParent();
            mvc.perform(post("/api/notificaciones/" + id + "/intentos").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
            mvc.perform(delete("/api/notificaciones/" + id)).andExpect(status().isNoContent());
        }
    }

    @Test
    void registraAvisoSmsConTelefonoInternacional() throws Exception {
        mvc.perform(post("/api/notificaciones").contentType("application/json")
                .content("""
{"destinatario":"+56912345678","canal":"SMS","mensaje":"Camila, recibimos tu pedido de dos hamburguesas en La Cocina de Barrio. Total: $19.980 CLP."}
"""))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.canal").value("SMS"));
    }

}
