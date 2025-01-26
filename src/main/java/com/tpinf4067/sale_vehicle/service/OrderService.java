package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.domain.CartItem;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.document.*;
import com.tpinf4067.sale_vehicle.patterns.order.factory.*;
import com.tpinf4067.sale_vehicle.patterns.order.state.*;
import com.tpinf4067.sale_vehicle.patterns.payment.PaymentType;
import com.tpinf4067.sale_vehicle.patterns.order.observer.EmailOrderNotifier;
import com.tpinf4067.sale_vehicle.patterns.order.observer.OrderNotifier;
import com.tpinf4067.sale_vehicle.repository.CustomerRepository;
import com.tpinf4067.sale_vehicle.repository.OrderRepository;
import com.tpinf4067.sale_vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final PDFDocumentAdapter pdfAdapter;
    private final OrderNotifier orderNotifier;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, VehicleRepository vehicleRepository,
                        CustomerRepository customerRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.pdfAdapter = new PDFDocumentAdapter();
        this.orderNotifier = new OrderNotifier();
        this.cartService = cartService;
    }

    // ✅ Création d'une commande depuis le panier avec Factory Method
    // ✅ Mise à jour pour ajouter la quantité de véhicules à la commande
    public Order createOrderFromCart(Long customerId, String paymentTypeStr) {
        Cart cart = cartService.getCartForCustomer(customerId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Le panier est vide, impossible de passer une commande !");
        }

        // Vérification du type de paiement
        PaymentType paymentType;
        try {
            paymentType = PaymentType.valueOf(paymentTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Type de paiement invalide : " + paymentTypeStr);
        } 

        // Factory Method pour créer la commande
        OrderFactory orderFactory = (paymentType == PaymentType.COMPTANT) ? 
                                    new ComptantOrderFactory() : 
                                    new CreditOrderFactory();

        Order order = orderFactory.createOrder(cart, paymentType);
        order.setCustomer(cart.getCustomer());
        order.setState(new PendingState());
        order.setDateDeCommande(new Date());

        for (CartItem item : cart.getItems()) {
            order.addVehicleWithOptions(item.getVehicle(), item.getOptions(), item.getQuantity());

            Vehicle vehicle = item.getVehicle();
            if (vehicle.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Le véhicule " + vehicle.getName() + " n'a pas assez de stock !");
            }
            vehicle.setStockQuantity(vehicle.getStockQuantity() - item.getQuantity());
            vehicleRepository.save(vehicle);
        }

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(customerId);
        generateOrderDocuments(savedOrder);
        orderNotifier.addObserver(new EmailOrderNotifier(cart.getCustomer().getEmail()));
        orderNotifier.notifyObservers("Votre commande a été créée avec succès !");
        return savedOrder;
    }

    // ✅ Génération des documents
    public void generateOrderDocuments(Order order) {
        OrderDocumentBuilder builder = new OrderDocumentBuilder();
        builder.constructOrderDocuments(order);
        DocumentLiasseSingleton.getInstance().getDocuments().forEach(pdfAdapter::export);
    }

    // ✅ Changer l'état d'une commande avec State Pattern
    public Order changeOrderStatus(Long orderId, boolean next) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
    
        if (next) {
            order.nextState();
        } else {
            order.previousState();
        }
    
        orderRepository.save(order); // 🔥 Enregistrer la mise à jour en base
    
        orderNotifier.notifyObservers("Votre commande est maintenant : " + order.getState().getStatus());
    
        return order;
    }
    


    // ✅ Récupérer toutes les commandes
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ Rechercher des commandes par client et/ou état
    public List<Order> searchOrders(Long customerId, String state) {
        List<Order> orders;
        
        if (customerId != null) {
            orders = orderRepository.findByCustomerId(customerId);
        } else {
            orders = orderRepository.findAll();
        }
    
        // 🔥 Filtrage manuel sur l'état car `state` est @Transient et non en base de données
        if (state != null) {
            orders = orders.stream()
                    .filter(o -> o.getState().getStatus().equalsIgnoreCase(state))
                    .toList();
        }
    
        return orders;
    }
    

}
