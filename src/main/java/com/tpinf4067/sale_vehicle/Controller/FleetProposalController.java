package com.tpinf4067.sale_vehicle.Controller;

import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.fleet.FleetProposal;
import com.tpinf4067.sale_vehicle.service.FleetProposalService;
import com.tpinf4067.sale_vehicle.service.VehicleService;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/fleet-proposals")
public class FleetProposalController {

    private static final Logger logger = LoggerFactory.getLogger(FleetProposalController.class);

    private final FleetProposalService fleetProposalService;
    private final VehicleService vehicleService;

    public FleetProposalController(FleetProposalService fleetProposalService, VehicleService vehicleService) {
        this.fleetProposalService = fleetProposalService;
        this.vehicleService = vehicleService;
    }

    // Créer une proposition de flotte pour une entreprise
    @PostMapping("/company/{companyId}")
    public ResponseEntity<?> createFleetProposal(
            @PathVariable Long companyId,
            @RequestBody FleetProposal proposal,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            logger.info("Réception d'une demande de création de proposition de flotte pour l'entreprise {}", companyId);
            FleetProposal createdProposal = fleetProposalService.createFleetProposal(companyId, proposal, currentUser);
            return ResponseEntity.ok(createdProposal);
        } catch (IllegalStateException e) {
            logger.error("Erreur lors de la création de la proposition : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création de la proposition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue");
        }
    }

    // Récupérer toutes les propositions de flotte pour une entreprise
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<FleetProposal>> getFleetProposalsForCompany(@PathVariable Long companyId) {
        try {
            logger.info("Réception d'une demande de récupération des propositions de flotte pour l'entreprise {}", companyId);
            List<FleetProposal> proposals = fleetProposalService.getFleetProposalsForCompany(companyId);
            return ResponseEntity.ok(proposals);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la récupération des propositions de flotte", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Récupérer une proposition de flotte par son ID avec les détails des véhicules
    @GetMapping("/{proposalId}")
    public ResponseEntity<Map<String, Object>> getFleetProposalById(@PathVariable Long proposalId) {
        try {
            Optional<FleetProposal> proposalOptional = fleetProposalService.getFleetProposalById(proposalId);
            if (proposalOptional.isPresent()) {
                FleetProposal proposal = proposalOptional.get();
                logger.info("Proposition trouvée : {}", proposal);
                logger.info("IDs de véhicules : {}", proposal.getVehicleIds());

                List<Vehicle> vehicleDetails = new ArrayList<>();
                if (proposal.getVehicleIds() != null && !proposal.getVehicleIds().isEmpty()) {
                    for (Long vehicleId : proposal.getVehicleIds()) {
                        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
                        if (vehicle != null) {
                            vehicleDetails.add(vehicle);
                        }
                    }
                }

                // Construction d'une réponse personnalisée
                Map<String, Object> response = new HashMap<>();
                response.put("proposal", proposal);
                response.put("vehicleDetails", vehicleDetails);

                logger.info("Détails de la proposition récupérés : {} véhicules", vehicleDetails.size());

                return ResponseEntity.ok(response);
            } else {
                logger.error("Proposition non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la proposition de flotte", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Mettre à jour le statut d'une proposition de flotte
    @PutMapping("/{proposalId}/status")
    public ResponseEntity<FleetProposal> updateProposalStatus(
            @PathVariable Long proposalId,
            @RequestParam String newStatus,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            logger.info("Réception d'une demande de mise à jour du statut de la proposition de flotte {}", proposalId);
            FleetProposal updatedProposal = fleetProposalService.updateProposalStatus(proposalId, newStatus, currentUser);
            return ResponseEntity.ok(updatedProposal);
        } catch (IllegalStateException e) {
            logger.error("Erreur lors de la mise à jour du statut de la proposition : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la mise à jour du statut de la proposition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Récupérer les propositions par statut
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FleetProposal>> getProposalsByStatus(@PathVariable String status) {
        try {
            logger.info("Réception d'une demande de récupération des propositions de flotte par statut {}", status);
            List<FleetProposal> proposals = fleetProposalService.getProposalsByStatus(status);
            return ResponseEntity.ok(proposals);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la récupération des propositions de flotte par statut", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
