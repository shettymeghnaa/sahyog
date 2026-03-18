package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.entity.Claim;
import org.hartford.mutualinsurance.entity.Contribution;
import org.hartford.mutualinsurance.config.SecurityConfig;
import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.entity.MemberStatus;
import org.hartford.mutualinsurance.service.ClaimService;
import org.hartford.mutualinsurance.service.ContributionService;
import org.hartford.mutualinsurance.service.MemberService;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;
    @MockBean
    private ClaimService claimService;
    @MockBean
    private ContributionService contributionService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setFullName("Test Member");
        member.setEmail("member@example.com");
        member.setStatus(MemberStatus.ACTIVE);
    }

    @Test
    void registerMember_shouldReturnRegisteredMember() throws Exception {
        when(memberService.registerMember(anyLong(), any(Member.class))).thenReturn(member);

        mockMvc.perform(post("/api/members/region/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "MEMBER", username = "member@example.com")
    void getMyProfile_shouldReturnProfile() throws Exception {
        when(memberService.getMemberByEmail("member@example.com")).thenReturn(member);

        mockMvc.perform(get("/api/members/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("member@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMemberById_shouldReturnMember() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(member);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void suspendMember_shouldReturnSuspendedMember() throws Exception {
        member.setStatus(MemberStatus.SUSPENDED);
        when(memberService.suspendMember(1L)).thenReturn(member);

        mockMvc.perform(post("/api/members/suspend/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMemberClaims_shouldReturnList() throws Exception {
        Claim claim = new Claim();
        claim.setId(1L);
        when(claimService.getClaimsByMember(1L)).thenReturn(List.of(claim));

        mockMvc.perform(get("/api/members/1/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getMemberContributions_shouldReturnList() throws Exception {
        Contribution contribution = new Contribution();
        contribution.setId(1L);
        when(contributionService.getMemberContributions(1L)).thenReturn(List.of(contribution));

        mockMvc.perform(get("/api/members/1/contributions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
