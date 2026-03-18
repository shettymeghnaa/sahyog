package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.dto.PoolFinancialSummary;
import org.hartford.mutualinsurance.entity.PoolFund;
import org.hartford.mutualinsurance.entity.Region;

import java.math.BigDecimal;

public interface PoolFundService {

    PoolFund createPoolForRegion(Region region);

    PoolFund getPoolByRegionId(Long regionId);

    void updateBalance(PoolFund pool, BigDecimal amount);
    void save(PoolFund pool);
    PoolFinancialSummary getFinancialSummary(Long regionId);
    PoolFinancialSummary getGlobalFinancialSummary();
}
