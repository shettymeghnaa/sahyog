package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.service.RegionService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegionService regionService;

    @MockBean
    private org.hartford.mutualinsurance.security.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Region testRegion;

    @BeforeEach
    void setUp() {
        testRegion = new Region();
        testRegion.setId(1L);
        testRegion.setName("North");
        testRegion.setRiskLevel("MEDIUM");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRegion_shouldReturnCreatedRegion() throws Exception {
        when(regionService.createRegion(any(Region.class))).thenReturn(testRegion);

        mockMvc.perform(post("/api/regions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("North"));
    }

    @Test
    @WithMockUser
    void getAllRegions_shouldReturnList() throws Exception {
        when(regionService.getAllRegions()).thenReturn(List.of(testRegion));

        mockMvc.perform(get("/api/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("North"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRegionById_shouldReturnRegion() throws Exception {
        when(regionService.getRegionById(1L)).thenReturn(testRegion);

        mockMvc.perform(get("/api/regions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "GOVERNMENT")
    void updateRiskLevel_shouldReturnUpdatedRegion() throws Exception {
        testRegion.setRiskLevel("HIGH");
        when(regionService.updateRiskLevel(anyLong(), any(String.class))).thenReturn(testRegion);

        mockMvc.perform(put("/api/regions/1/risk")
                        .with(csrf())
                        .param("level", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("HIGH"));
    }
}
