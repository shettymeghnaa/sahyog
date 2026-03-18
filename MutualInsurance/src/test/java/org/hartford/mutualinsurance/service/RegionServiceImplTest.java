package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PoolFundService poolFundService;

    @InjectMocks
    private RegionServiceImpl regionService;

    private Region testRegion;

    @BeforeEach
    void setUp() {
        testRegion = new Region();
        testRegion.setId(1L);
        testRegion.setName("North");
        testRegion.setState("Delhi");
        testRegion.setCountry("India");
        testRegion.setRiskLevel("MEDIUM");
    }

    @Test
    void createRegion_whenNew_shouldReturnSavedRegion() {
        when(regionRepository.findByName("North")).thenReturn(Optional.empty());
        when(regionRepository.save(any(Region.class))).thenReturn(testRegion);

        Region result = regionService.createRegion(testRegion);

        assertNotNull(result);
        assertEquals("North", result.getName());
        verify(poolFundService, times(1)).createPoolForRegion(testRegion);
        verify(regionRepository, times(1)).save(testRegion);
    }

    @Test
    void createRegion_whenExists_shouldThrowException() {
        when(regionRepository.findByName("North")).thenReturn(Optional.of(testRegion));

        assertThrows(InvalidOperationException.class, () -> 
            regionService.createRegion(testRegion));
    }

    @Test
    void getAllRegions_shouldReturnList() {
        when(regionRepository.findAll()).thenReturn(List.of(testRegion));

        List<Region> result = regionService.getAllRegions();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getRegionById_whenExists_shouldReturnRegion() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));

        Region result = regionService.getRegionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getRegionById_whenNotExists_shouldThrowException() {
        when(regionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            regionService.getRegionById(99L));
    }

    @Test
    void deleteRegion_whenNoMembers_shouldDeleteSuccessfully() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));
        when(memberRepository.findByRegionId(1L)).thenReturn(Collections.emptyList());

        regionService.deleteRegion(1L);

        verify(regionRepository, times(1)).delete(testRegion);
    }

    @Test
    void deleteRegion_whenMembersExist_shouldThrowException() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));
        when(memberRepository.findByRegionId(1L)).thenReturn(List.of(new org.hartford.mutualinsurance.entity.Member()));

        assertThrows(InvalidOperationException.class, () -> 
            regionService.deleteRegion(1L));
    }
}
