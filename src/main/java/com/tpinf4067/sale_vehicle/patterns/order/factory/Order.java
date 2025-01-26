package com.tpinf4067.sale_vehicle.patterns.order.factory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tpinf4067.sale_vehicle.domain.Option;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.order.state.DeliveredState;
import com.tpinf4067.sale_vehicle.patterns.order.state.OrderState;
import com.tpinf4067.sale_vehicle.patterns.order.state.PendingState;
import com.tpinf4067.sale_vehicle.patterns.order.state.ValidatedState;
import com.tpinf4067.sale_vehicle.patterns.payment.PaymentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonManagedReference
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date dateDeCommande;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderVehicle> orderVehicles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "order_options",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<Option> options = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Transient
    private OrderState state;

    @Column(nullable = false)
    private String stateName;

    @PrePersist
    protected void onCreate() {
        this.dateDeCommande = new Date();
        this.state = new PendingState();
        this.stateName = state.getStatus();
    }

    @PostLoad
    private void initState() {
        switch (this.stateName) {
            case "VALIDEE":
                this.state = new ValidatedState();
                break;
            case "LIVREE":
                this.state = new DeliveredState();
                break;
            default:
                this.state = new PendingState();
        }
    }

    public void nextState() {
        state.next(this);
        this.stateName = state.getStatus();
    }

    public void previousState() {
        state.previous(this);
        this.stateName = state.getStatus();
    }

    public String getStatus() {
        return state.getStatus();
    }

    public void addVehicleWithOptions(Vehicle vehicle, List<Option> vehicleOptions, int quantity) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.addAll(vehicleOptions);

        OrderVehicle orderVehicle = new OrderVehicle();
        orderVehicle.setOrder(this);
        orderVehicle.setVehicle(vehicle);
        orderVehicle.setQuantity(quantity);

        this.orderVehicles.add(orderVehicle);
    }

    public double getTotalPrice() {
        double vehiclesPrice = orderVehicles.stream()
            .mapToDouble(orderVehicle -> orderVehicle.getVehicle().getPrice() * orderVehicle.getQuantity())
            .sum();

        double optionsPrice = options.stream().mapToDouble(Option::getPrice).sum();

        double totalPrice = vehiclesPrice + optionsPrice;

        // 🔥 Formater pour éviter l'affichage en notation scientifique
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(totalPrice));   
    }

    public String getStateString() {
        return stateName;
    }
}
