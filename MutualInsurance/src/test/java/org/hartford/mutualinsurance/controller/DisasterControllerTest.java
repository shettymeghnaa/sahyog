package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.entity.DisasterEvent;
import org.hartford.mutualinsurance.service.DisasterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.hartford.mutualinsurance.security.JwtUtil;
import org.hartford.mutualinsurance.security.CustomUserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DisasterController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DisasterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DisasterService disasterService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private DisasterEvent disaster;

    @BeforeEach
    void setUp() {
        disaster = new DisasterEvent();
        disaster.setId(1L);
        disaster.setName("Wildfire");
        disaster.setStatus("ACTIVE");
    }

    @Test
    @WithMockUser(roles = "GOVERNMENT")
    void declareDisaster_shouldReturnCreatedDisaster() throws Exception {
        when(disasterService.declareDisaster(anyLong(), any(DisasterEvent.class))).thenReturn(disaster);

        mockMvc.perform(post("/api/disasters/region/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disaster)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDisasters_shouldReturnList() throws Exception {
        when(disasterService.getAllDisasters()).thenReturn(List.of(disaster));

        mockMvc.perform(get("/api/disasters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Wildfire"));
    }

    @Test
    @WithMockUser(roles = "GOVERNMENT")
    void closeDisaster_shouldReturnClosedDisaster() throws Exception {
        disaster.setStatus("CLOSED");
        when(disasterService.closeDisaster(anyLong())).thenReturn(disaster);

        mockMvc.perform(post("/api/disasters/close/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getActiveDisasters_shouldReturnList() throws Exception {
        when(disasterService.getActiveDisasters()).thenReturn(List.of(disaster));

        mockMvc.perform(get("/api/disasters/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getActiveDisastersByRegion_shouldReturnList() throws Exception {
        when(disasterService.getActiveDisastersByRegion(1L)).thenReturn(List.of(disaster));

        mockMvc.perform(get("/api/disasters/active/region/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
