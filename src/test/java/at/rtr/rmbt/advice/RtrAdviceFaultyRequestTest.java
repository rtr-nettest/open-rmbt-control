package at.rtr.rmbt.advice;

import at.rtr.rmbt.enums.TestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that a malformed/faulty request is mapped to HTTP 400 (Bad Request) rather than 500,
 * across the cases that previously slipped through: unparseable JSON, wrong field type, an invalid
 * enum value in the body, and a bad typed path variable. Uses a standalone MockMvc wired with the
 * real {@link RtrAdvice} plus Spring's default exception resolvers, so the asserted status reflects
 * the full resolver chain - not just the advice in isolation.
 */
class RtrAdviceFaultyRequestTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProbeController())
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    void malformedJsonBody_isBadRequest() throws Exception {
        mockMvc.perform(post("/probe").contentType(MediaType.APPLICATION_JSON).content("{ not valid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void wrongFieldType_isBadRequest() throws Exception {
        mockMvc.perform(post("/probe").contentType(MediaType.APPLICATION_JSON).content("{\"value\":\"notAnInt\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidEnumValue_isBadRequest() throws Exception {
        mockMvc.perform(post("/probe").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"NOT_A_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void badTypedPathVariable_isBadRequest() throws Exception {
        mockMvc.perform(get("/probe/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @RestController
    static class ProbeController {
        @PostMapping("/probe")
        public String body(@RequestBody ProbeBody body) {
            return "ok";
        }

        @GetMapping("/probe/{id}")
        public String path(@PathVariable UUID id) {
            return "ok";
        }

        static class ProbeBody {
            public int value;
            public TestStatus status;
        }
    }
}
