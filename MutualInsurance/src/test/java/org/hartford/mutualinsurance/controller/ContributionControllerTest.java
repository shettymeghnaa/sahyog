package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.entity.Contribution;
import org.hartford.mutualinsurance.service.ContributionService;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContributionController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContributionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContributionService contributionService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    private Contribution testContribution;

    @BeforeEach
    void setUp() {
        testContribution = new Contribution();
        testContribution.setId(1L);
        testContribution.setAmount(new BigDecimal("100"));
        testContribution.setContributionMonth(LocalDate.now().withDayOfMonth(1));
    }

    @Test
    @WithMockUser(roles = "MEMBER", username = "member@example.com")
    void payContribution_shouldReturnSavedContribution() throws Exception {
        when(contributionService.payContributionByEmail(anyString(), any(LocalDate.class)))
                .thenReturn(testContribution);

        mockMvc.perform(post("/api/contributions/pay")
                        .with(csrf())
                        .param("month", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getContributionsByMember_shouldReturnList() throws Exception {
        when(contributionService.getMemberContributions(1L)).thenReturn(List.of(testContribution));

        mockMvc.perform(get("/api/contributions/member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "MEMBER", username = "member@example.com")
    void getMyContributions_shouldReturnList() throws Exception {
        when(contributionService.getMemberContributionsByEmail("member@example.com"))
                .thenReturn(List.of(testContribution));

        mockMvc.perform(get("/api/contributions/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
