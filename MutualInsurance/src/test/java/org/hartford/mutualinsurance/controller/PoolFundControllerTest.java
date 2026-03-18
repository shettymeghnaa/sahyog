package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.dto.PoolFinancialSummary;
import org.hartford.mutualinsurance.entity.PoolFund;
import org.hartford.mutualinsurance.service.PoolFundService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PoolFundController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PoolFundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PoolFundService poolFundService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    private PoolFund poolFund;
    private PoolFinancialSummary summary;

    @BeforeEach
    void setUp() {
        poolFund = new PoolFund();
        poolFund.setId(1L);
        poolFund.setTotalBalance(new BigDecimal("10000"));

        summary = new PoolFinancialSummary();
        summary.setTotalBalance(new BigDecimal("10000"));
        summary.setAvailableBalance(new BigDecimal("7000"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPoolByRegion_shouldReturnPool() throws Exception {
        when(poolFundService.getPoolByRegionId(1L)).thenReturn(poolFund);

        mockMvc.perform(get("/api/pools/region/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFinancialSummary_shouldReturnSummary() throws Exception {
        when(poolFundService.getFinancialSummary(1L)).thenReturn(summary);

        mockMvc.perform(get("/api/pools/region/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(7000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGlobalFinancialSummary_shouldReturnSummary() throws Exception {
        when(poolFundService.getGlobalFinancialSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/pools/summary/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").value(10000));
    }
}
