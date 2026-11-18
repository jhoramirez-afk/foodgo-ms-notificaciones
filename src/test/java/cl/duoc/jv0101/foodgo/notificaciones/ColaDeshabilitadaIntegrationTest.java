package cl.duoc.jv0101.foodgo.notificaciones;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ColaDeshabilitadaIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void elCrudLocalFuncionaAunqueLaColaNoEsteConfigurada() throws Exception {
        String body = mvc.perform(post("/api/notificaciones").contentType("application/json")
                .content("""
                    {"destinatario":"martin.araya@example.com","canal":"EMAIL","mensaje":"Martin, recibimos tu pedido."}
                    """))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(body).path("id").longValue();
        mvc.perform(post("/api/notificaciones/" + id + "/encolar"))
                .andExpect(status().isServiceUnavailable());
        mvc.perform(get("/api/notificaciones/" + id)).andExpect(status().isOk());
    }
}
