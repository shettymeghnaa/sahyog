package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService{
    private final RegionRepository regionRepository;
    private final MemberRepository memberRepository;
    private final PoolFundService poolFundService;
    public RegionServiceImpl(RegionRepository regionRepository,
                             MemberRepository memberRepository, PoolFundService poolFundService) {
        this.regionRepository = regionRepository;
        this.memberRepository = memberRepository;
        this.poolFundService = poolFundService;
    }

    @Override
    public Region createRegion(Region region) {
        if (regionRepository.findByName(region.getName()).isPresent()) {
            throw new InvalidOperationException("Region already exists");
        }

        Region savedRegion = regionRepository.save(region);

        poolFundService.createPoolForRegion(savedRegion);

        return savedRegion;
    }

    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Override
    public Region getRegionById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found"));
    }

    @Override
    public Region updateRegion(Long id, Region updatedRegion) {
        Region existing = getRegionById(id);

        existing.setName(updatedRegion.getName());
        existing.setState(updatedRegion.getState());
        existing.setCountry(updatedRegion.getCountry());
        existing.setRiskLevel(updatedRegion.getRiskLevel());
        existing.setStatus(updatedRegion.getStatus());

        return regionRepository.save(existing);
    }

    @Override
    public Region updateRiskLevel(Long id, String riskLevel) {
        Region existing = getRegionById(id);
        existing.setRiskLevel(riskLevel);
        return regionRepository.save(existing);
    }

    @Override
    public void deleteRegion(Long id) {
        Region region = getRegionById(id);

        if (!memberRepository.findByRegionId(id).isEmpty()) {
            throw new InvalidOperationException("Cannot delete region with existing members");
        }

        regionRepository.delete(region);
    }
    }

