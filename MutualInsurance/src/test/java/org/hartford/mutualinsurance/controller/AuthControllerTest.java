package org.hartford.mutualinsurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.entity.MemberStatus;
import org.hartford.mutualinsurance.security.*;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
    }

    @Test
    void login_whenAdmin_shouldReturnTokenAndRole() throws Exception {
        Authentication auth = mock(Authentication.class);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(auth).getAuthorities();

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("test-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void login_whenMemberActive_shouldReturnTokenAndRole() throws Exception {
        Authentication auth = mock(Authentication.class);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER")))
                .when(auth).getAuthorities();

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        
        AppUser appUser = new AppUser();
        appUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));
        
        Member member = new Member();
        member.setStatus(MemberStatus.ACTIVE);
        when(memberRepository.findByUser(appUser)).thenReturn(Optional.of(member));
        
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("test-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.role").value("ROLE_MEMBER"));
    }

    @Test
    void login_whenMemberPending_shouldReturnError() throws Exception {
        Authentication auth = mock(Authentication.class);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER")))
                .when(auth).getAuthorities();

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        
        AppUser appUser = new AppUser();
        appUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));
        
        Member member = new Member();
        member.setStatus(MemberStatus.PENDING);
        when(memberRepository.findByUser(appUser)).thenReturn(Optional.of(member));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerClaimOfficer_whenValid_shouldReturnSuccess() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        mockMvc.perform(post("/api/auth/register/claim-officer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Claim officer account created successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerClaimOfficer_whenUserExists_shouldReturnConflict() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new AppUser()));

        mockMvc.perform(post("/api/auth/register/claim-officer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isConflict());
    }
    
}
