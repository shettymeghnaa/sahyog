package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.dto.PolicyDTO;
import org.hartford.mutualinsurance.service.PolicyService;
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

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private PolicyDTO policyDTO;

    @BeforeEach
    void setUp() {
        policyDTO = new PolicyDTO();
        policyDTO.setMonthlyContribution(new BigDecimal("100"));
        policyDTO.setWaitingPeriodDays(30);
        policyDTO.setMaxPayoutPerClaim(new BigDecimal("5000"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPolicy_shouldReturnCreatedPolicy() throws Exception {
        when(policyService.createPolicy(anyLong(), any(PolicyDTO.class))).thenReturn(policyDTO);

        mockMvc.perform(post("/api/policies/region/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyContribution").value(100));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getPolicyByRegion_shouldReturnPolicy() throws Exception {
        when(policyService.getPolicyByRegionId(1L)).thenReturn(policyDTO);

        mockMvc.perform(get("/api/policies/region/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitingPeriodDays").value(30));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePolicy_shouldReturnUpdatedPolicy() throws Exception {
        when(policyService.updatePolicy(anyLong(), any(PolicyDTO.class))).thenReturn(policyDTO);

        mockMvc.perform(put("/api/policies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxPayoutPerClaim").value(5000));
    }
}
