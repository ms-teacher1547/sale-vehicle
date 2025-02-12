package com.tpinf4067.sale_vehicle.patterns.fleet;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
public class FleetProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Customer company;

    @ElementCollection
    private List<Long> vehicleIds;

    @Transient  // Indique que ce champ ne doit pas être persisté en base de données
    private List<Vehicle> vehicleDetails = new ArrayList<>();

    private int numberOfVehicles;
    private double totalPrice;
    private String proposalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeurs
    public FleetProposal() {
        this.createdAt = LocalDateTime.now();
        this.proposalStatus = "EN_ATTENTE";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCompany() { return company; }
    public void setCompany(Customer company) { this.company = company; }

    public List<Long> getVehicleIds() { return vehicleIds; }
    public void setVehicleIds(List<Long> vehicleIds) { this.vehicleIds = vehicleIds; }

    public int getNumberOfVehicles() { return numberOfVehicles; }
    public void setNumberOfVehicles(int numberOfVehicles) { this.numberOfVehicles = numberOfVehicles; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getProposalStatus() { return proposalStatus; }
    public void setProposalStatus(String proposalStatus) { this.proposalStatus = proposalStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Nouveau getter et setter pour vehicleDetails
    public List<Vehicle> getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(List<Vehicle> vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
        // Mettre à jour le nombre de véhicules et le prix total
        this.numberOfVehicles = vehicleDetails.size();
        this.totalPrice = vehicleDetails.stream()
            .mapToDouble(Vehicle::getPrice)
            .sum();
    }
}
