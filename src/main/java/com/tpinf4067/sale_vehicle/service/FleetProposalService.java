package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;
import com.tpinf4067.sale_vehicle.patterns.fleet.FleetProposal;
import com.tpinf4067.sale_vehicle.repository.CustomerRepository;
import com.tpinf4067.sale_vehicle.repository.FleetProposalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FleetProposalService {

    private static final Logger logger = LoggerFactory.getLogger(FleetProposalService.class);

    private final FleetProposalRepository fleetProposalRepository;
    private final CustomerRepository customerRepository;

    public FleetProposalService(FleetProposalRepository fleetProposalRepository, 
                                CustomerRepository customerRepository) {
        this.fleetProposalRepository = fleetProposalRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public FleetProposal createFleetProposal(Long companyId, FleetProposal proposal, User currentUser) {
        logger.info("Tentative de création de proposition de flotte pour l'entreprise {}", companyId);
        logger.info("Détails de la proposition : vehicleIds={}, numberOfVehicles={}, totalPrice={}, status={}",
            proposal.getVehicleIds(), proposal.getNumberOfVehicles(), proposal.getTotalPrice(), proposal.getProposalStatus());
        logger.info("Utilisateur courant : {}", currentUser.getId());

        // Vérifier que seul l'admin peut créer des propositions de flotte
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            logger.error("Seul un administrateur peut créer des propositions de flotte");
            throw new IllegalStateException("Vous n'êtes pas autorisé à créer une proposition de flotte");
        }

        // Vérifier que le client est une entreprise
        Customer company = customerRepository.findById(companyId)
            .orElseThrow(() -> {
                logger.error("Entreprise non trouvée : {}", companyId);
                return new IllegalStateException("Entreprise non trouvée");
            });

        logger.info("Entreprise trouvée : type={}", company.getType());

        if (company.getType() != CustomerType.COMPANY) {
            logger.error("L'entité {} n'est pas une entreprise", companyId);
            throw new IllegalStateException("Seules les entreprises peuvent recevoir des propositions de flotte");
        }

        proposal.setCompany(company);
        proposal.setCreatedAt(LocalDateTime.now());
        proposal.setProposalStatus("EN_ATTENTE");

        FleetProposal savedProposal = fleetProposalRepository.save(proposal);
        logger.info("Proposition de flotte créée avec succès : {}", savedProposal.getId());
        return savedProposal;
    }

    public List<FleetProposal> getFleetProposalsForCompany(Long companyId) {
        return fleetProposalRepository.findByCompanyId(companyId);
    }

    public Optional<FleetProposal> getFleetProposalById(Long proposalId) {
        return fleetProposalRepository.findById(proposalId);
    }

    @Transactional
    public FleetProposal updateProposalStatus(Long proposalId, String newStatus, User currentUser) {
        FleetProposal proposal = fleetProposalRepository.findById(proposalId)
            .orElseThrow(() -> new IllegalStateException("Proposition non trouvée"));

        // Vérifier que l'utilisateur a le droit de mettre à jour la proposition
        if (!proposal.getCompany().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Vous n'êtes pas autorisé à modifier cette proposition");
        }

        proposal.setProposalStatus(newStatus);
        proposal.setUpdatedAt(LocalDateTime.now());

        return fleetProposalRepository.save(proposal);
    }

    public List<FleetProposal> getProposalsByStatus(String status) {
        return fleetProposalRepository.findByProposalStatus(status);
    }
}
