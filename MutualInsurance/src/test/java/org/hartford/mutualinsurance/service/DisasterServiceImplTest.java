package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.DisasterEvent;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.DisasterRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisasterServiceImplTest {

    @Mock
    private DisasterRepository disasterRepository;
    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private DisasterServiceImpl disasterService;

    private Region region;
    private DisasterEvent disaster;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setName("North");

        disaster = new DisasterEvent();
        disaster.setId(1L);
        disaster.setName("Flood 2024");
        disaster.setStatus("ACTIVE");
        disaster.setRegion(region);
        disaster.setStartDate(LocalDate.now());
    }

    @Test
    void declareDisaster_whenValid_shouldReturnSavedDisaster() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(disasterRepository.existsByRegionIdAndStatus(1L, "ACTIVE")).thenReturn(false);
        when(disasterRepository.save(any(DisasterEvent.class))).thenReturn(disaster);

        DisasterEvent result = disasterService.declareDisaster(1L, disaster);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(disasterRepository, times(1)).save(disaster);
    }

    @Test
    void declareDisaster_whenRegionNotFound_shouldThrowException() {
        when(regionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            disasterService.declareDisaster(1L, disaster));
    }

    @Test
    void declareDisaster_whenActiveAlreadyExists_shouldThrowException() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(disasterRepository.existsByRegionIdAndStatus(1L, "ACTIVE")).thenReturn(true);

        assertThrows(InvalidOperationException.class, () -> 
            disasterService.declareDisaster(1L, disaster));
    }

    @Test
    void closeDisaster_whenActive_shouldUpdateStatusToClosed() {
        when(disasterRepository.findById(1L)).thenReturn(Optional.of(disaster));
        when(disasterRepository.save(any(DisasterEvent.class))).thenReturn(disaster);

        DisasterEvent result = disasterService.closeDisaster(1L);

        assertEquals("CLOSED", result.getStatus());
        verify(disasterRepository, times(1)).save(disaster);
    }

    @Test
    void closeDisaster_whenAlreadyClosed_shouldThrowException() {
        disaster.setStatus("CLOSED");
        when(disasterRepository.findById(1L)).thenReturn(Optional.of(disaster));

        assertThrows(InvalidOperationException.class, () -> 
            disasterService.closeDisaster(1L));
    }

    @Test
    void closeDisaster_whenNotFound_shouldThrowException() {
        when(disasterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            disasterService.closeDisaster(1L));
    }

    @Test
    void getDisastersByRegion_shouldReturnList() {
        when(disasterRepository.findByRegionId(1L)).thenReturn(List.of(disaster));

        List<DisasterEvent> result = disasterService.getDisastersByRegion(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void getAllDisasters_shouldReturnList() {
        when(disasterRepository.findAll()).thenReturn(List.of(disaster));

        List<DisasterEvent> result = disasterService.getAllDisasters();

        assertFalse(result.isEmpty());
    }

    @Test
    void getActiveDisasters_shouldReturnList() {
        when(disasterRepository.findByStatus("ACTIVE")).thenReturn(List.of(disaster));

        List<DisasterEvent> result = disasterService.getActiveDisasters();

        assertFalse(result.isEmpty());
    }

    @Test
    void getActiveDisastersByRegion_shouldReturnList() {
        when(disasterRepository.findByRegionIdAndStatus(1L, "ACTIVE")).thenReturn(List.of(disaster));

        List<DisasterEvent> result = disasterService.getActiveDisastersByRegion(1L);

        assertFalse(result.isEmpty());
    }
}
