package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.dto.ClaimRequest;
import org.hartford.mutualinsurance.entity.Claim;
import org.hartford.mutualinsurance.entity.ClaimStatus;
import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.hartford.mutualinsurance.security.JwtUtil;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClaimController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimService claimService;

    @MockBean
    private org.hartford.mutualinsurance.security.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Claim testClaim;

    @BeforeEach
    void setUp() {
        testClaim = new Claim();
        testClaim.setId(1L);
        testClaim.setRequestedAmount(new BigDecimal("1000"));
        testClaim.setStatus(ClaimStatus.SUBMITTED);
        
        Member member = new Member();
        member.setEmail("member@example.com");
        testClaim.setMember(member);
    }

    @Test
    @WithMockUser(roles = "MEMBER", username = "member@example.com")
    void submitClaim_shouldReturnCreatedClaim() throws Exception {
        ClaimRequest request = new ClaimRequest();
        request.setDisasterId(1L);
        request.setRequestedAmount(new BigDecimal("1000"));
        
        when(claimService.submitClaim(anyString(), any(ClaimRequest.class))).thenReturn(testClaim);

        mockMvc.perform(post("/api/claims/submit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    @WithMockUser(roles = "MEMBER", username = "member@example.com")
    void getMyClaims_shouldReturnList() throws Exception {
        when(claimService.getClaimsByEmail("member@example.com")).thenReturn(List.of(testClaim));

        mockMvc.perform(get("/api/claims/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "CLAIM_OFFICER")
    void getPendingClaims_shouldReturnList() throws Exception {
        when(claimService.getPendingClaimsByOfficer(anyString())).thenReturn(List.of(testClaim));

        mockMvc.perform(get("/api/claims/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllClaims_shouldReturnList() throws Exception {
        when(claimService.getAllClaims()).thenReturn(List.of(testClaim));

        mockMvc.perform(get("/api/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
