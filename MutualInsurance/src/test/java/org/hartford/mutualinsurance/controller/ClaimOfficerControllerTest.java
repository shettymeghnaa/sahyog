package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.config.SecurityConfig;
import org.hartford.mutualinsurance.entity.ClaimOfficer;
import org.hartford.mutualinsurance.service.ClaimOfficerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClaimOfficerController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClaimOfficerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimOfficerService claimOfficerService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private ClaimOfficer officer;

    @BeforeEach
    void setUp() {
        officer = new ClaimOfficer();
        officer.setId(1L);
        officer.setFullName("John Doe");
        officer.setEmail("john.doe@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createClaimOfficer_shouldReturnCreatedOfficer() throws Exception {
        when(claimOfficerService.createClaimOfficer(anyLong(), any(ClaimOfficer.class))).thenReturn(officer);

        mockMvc.perform(post("/api/claim-officers/region/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(officer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllClaimOfficers_shouldReturnList() throws Exception {
        when(claimOfficerService.getAllClaimOfficers()).thenReturn(List.of(officer));

        mockMvc.perform(get("/api/claim-officers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deactivateClaimOfficer_shouldReturnOk() throws Exception {
        doNothing().when(claimOfficerService).deactivateClaimOfficer(1L);

        mockMvc.perform(put("/api/claim-officers/1/deactivate")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(claimOfficerService, times(1)).deactivateClaimOfficer(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reassignOfficerRegion_shouldReturnOk() throws Exception {
        doNothing().when(claimOfficerService).reassignOfficerRegion(1L, 2L);

        mockMvc.perform(put("/api/claim-officers/1/reassign-region/2")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(claimOfficerService, times(1)).reassignOfficerRegion(1L, 2L);
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getAllClaimOfficers_asMember_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/claim-officers"))
                .andExpect(status().isForbidden());
    }
}
