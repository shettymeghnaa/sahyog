package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.entity.MemberStatus;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private Region region;
    private AppUser user;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setName("North");

        user = new AppUser();
        user.setId(1L);
        user.setUsername("member@example.com");

        member = new Member();
        member.setId(1L);
        member.setEmail("member@example.com");
        member.setPassword("password123");
        member.setAcceptedTerms(true);
        member.setRegion(region);
        member.setUser(user);
    }

    @Test
    void registerMember_whenValid_shouldReturnSavedMember() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.registerMember(1L, member);

        assertNotNull(result);
        assertEquals(MemberStatus.PENDING, result.getStatus());
        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void registerMember_whenTermsNotAccepted_shouldThrowException() {
        member.setAcceptedTerms(false);
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));

        assertThrows(InvalidOperationException.class, () -> 
            memberService.registerMember(1L, member));
    }

    @Test
    void registerMember_whenEmailAlreadyExists_shouldThrowException() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        assertThrows(InvalidOperationException.class, () -> 
            memberService.registerMember(1L, member));
    }

    @Test
    void getMemberById_whenExists_shouldReturnMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.getMemberById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getMemberById_whenNotExists_shouldThrowException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            memberService.getMemberById(1L));
    }

    @Test
    void activateMember_shouldUpdateStatus() {
        member.setStatus(MemberStatus.PENDING);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.activateMember(1L);

        assertEquals(MemberStatus.ACTIVE, result.getStatus());
    }

    @Test
    void suspendMember_shouldUpdateStatus() {
        member.setStatus(MemberStatus.ACTIVE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.suspendMember(1L);

        assertEquals(MemberStatus.SUSPENDED, result.getStatus());
    }

    @Test
    void getMemberByEmail_whenExists_shouldReturnMember() {
        when(userRepository.findByUsername("member@example.com")).thenReturn(Optional.of(user));
        when(memberRepository.findByUser(user)).thenReturn(Optional.of(member));

        Member result = memberService.getMemberByEmail("member@example.com");

        assertNotNull(result);
        assertEquals("member@example.com", result.getEmail());
    }
}
