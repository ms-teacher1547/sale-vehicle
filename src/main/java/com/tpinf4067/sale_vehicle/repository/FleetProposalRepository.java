package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.patterns.fleet.FleetProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FleetProposalRepository extends JpaRepository<FleetProposal, Long> {
    List<FleetProposal> findByCompanyId(Long companyId);
    List<FleetProposal> findByProposalStatus(String status);
}
