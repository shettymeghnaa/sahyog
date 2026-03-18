package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.ClaimOfficer;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.ClaimOfficerRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimOfficerServiceImplTest {

    @Mock
    private ClaimOfficerRepository claimOfficerRepository;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClaimOfficerServiceImpl claimOfficerService;

    private ClaimOfficer officer;
    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setName("North");

        officer = new ClaimOfficer();
        officer.setId(1L);
        officer.setFullName("John Doe");
        officer.setEmail("john.doe@example.com");
        officer.setPassword("password123");
        officer.setStatus("ACTIVE");
        officer.setRegion(region);
    }

    @Test
    void createClaimOfficer_whenValid_shouldReturnSavedOfficer() {
        when(claimOfficerRepository.existsByEmail(officer.getEmail())).thenReturn(false);
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByUsername(officer.getEmail())).thenReturn(Optional.empty());
        when(claimOfficerRepository.save(any(ClaimOfficer.class))).thenReturn(officer);

        ClaimOfficer result = claimOfficerService.createClaimOfficer(1L, officer);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("encodedPassword", officer.getPassword());
        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(claimOfficerRepository, times(1)).save(any(ClaimOfficer.class));
    }

    @Test
    void createClaimOfficer_whenEmailExists_shouldThrowException() {
        when(claimOfficerRepository.existsByEmail(officer.getEmail())).thenReturn(true);

        assertThrows(InvalidOperationException.class, () -> 
            claimOfficerService.createClaimOfficer(1L, officer));
    }

    @Test
    void createClaimOfficer_whenRegionNotFound_shouldThrowException() {
        when(claimOfficerRepository.existsByEmail(officer.getEmail())).thenReturn(false);
        when(regionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            claimOfficerService.createClaimOfficer(1L, officer));
    }

    @Test
    void getAllClaimOfficers_shouldReturnList() {
        when(claimOfficerRepository.findAll()).thenReturn(List.of(officer));

        List<ClaimOfficer> result = claimOfficerService.getAllClaimOfficers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deactivateClaimOfficer_whenValid_shouldUpdateStatusAndRemoveUser() {
        when(claimOfficerRepository.findById(1L)).thenReturn(Optional.of(officer));
        when(userRepository.findByUsername(officer.getEmail())).thenReturn(Optional.of(new AppUser()));

        claimOfficerService.deactivateClaimOfficer(1L);

        assertEquals("INACTIVE", officer.getStatus());
        verify(claimOfficerRepository, times(1)).save(officer);
        verify(userRepository, times(1)).delete(any(AppUser.class));
    }

    @Test
    void deactivateClaimOfficer_whenNotFound_shouldThrowException() {
        when(claimOfficerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            claimOfficerService.deactivateClaimOfficer(1L));
    }

    @Test
    void reassignOfficerRegion_whenValid_shouldUpdateRegion() {
        Region newRegion = new Region();
        newRegion.setId(2L);
        newRegion.setName("South");

        when(claimOfficerRepository.findById(1L)).thenReturn(Optional.of(officer));
        when(regionRepository.findById(2L)).thenReturn(Optional.of(newRegion));

        claimOfficerService.reassignOfficerRegion(1L, 2L);

        assertEquals(2L, officer.getRegion().getId());
        verify(claimOfficerRepository, times(1)).save(officer);
    }

    @Test
    void reassignOfficerRegion_whenOfficerNotFound_shouldThrowException() {
        when(claimOfficerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            claimOfficerService.reassignOfficerRegion(1L, 2L));
    }

    @Test
    void reassignOfficerRegion_whenRegionNotFound_shouldThrowException() {
        when(claimOfficerRepository.findById(1L)).thenReturn(Optional.of(officer));
        when(regionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            claimOfficerService.reassignOfficerRegion(1L, 2L));
    }
}
