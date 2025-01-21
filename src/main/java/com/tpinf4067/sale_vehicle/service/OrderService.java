package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.domain.CartItem;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.document.*;
import com.tpinf4067.sale_vehicle.patterns.order.Order;
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

    // ✅ Création d'une commande depuis le panier
    public Order createOrderFromCart(Long customerId) {
        Cart cart = cartService.getCartForCustomer(customerId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Le panier est vide, impossible de passer une commande !");
        }

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setState("EN_COURS");
        order.setDateDeCommande(new Date());

        // Ajout des véhicules et options de chaque élément du panier
        for (CartItem item : cart.getItems()) {
            order.addVehicleWithOptions(item.getVehicle(), item.getOptions());

            // 🔥 Vérifier le stock et le mettre à jour
            Vehicle vehicle = item.getVehicle();
            if (vehicle.getStockQuantity() <= 0) {
                throw new IllegalStateException("Le véhicule " + vehicle.getName() + " est en rupture de stock !");
            }
            vehicle.setStockQuantity(vehicle.getStockQuantity() - 1);
            vehicleRepository.save(vehicle);
        }

        Order savedOrder = orderRepository.save(order);

        // ✅ Vider le panier après la commande
        cartService.clearCart(customerId);

        // ✅ Générer les documents
        generateOrderDocuments(savedOrder);

        // ✅ Notifier le client
        orderNotifier.addObserver(new EmailOrderNotifier(cart.getCustomer().getEmail()));
        orderNotifier.notifyObservers("Votre commande a été créée avec succès !");

        return savedOrder;
    }

    // ✅ Génération des documents
    public void generateOrderDocuments(Order order) {
        OrderDocumentBuilder builder = new OrderDocumentBuilder();
        builder.constructOrderDocuments(order);
        
        // ✅ Exporter chaque document en PDF
        DocumentLiasseSingleton.getInstance().getDocuments().forEach(pdfAdapter::export);
    }

    // ✅ Récupérer toutes les commandes
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ Changer l'état d'une commande avec notification
    public Order changeOrderStatus(Long orderId, boolean next) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        if (next) {
            order.nextState();
        } else {
            order.previousState();
        }

        orderRepository.save(order);

        // 🔥 Notification du client
        orderNotifier.notifyObservers("Votre commande est maintenant : " + order.getState());

        return order;
    }

    public List<Order> searchOrders(Long customerId, String state) {
        if (customerId != null && state != null) {
            return orderRepository.findByCustomerId(customerId)
                    .stream()
                    .filter(o -> o.getState().equalsIgnoreCase(state))
                    .toList();
        } else if (customerId != null) {
            return orderRepository.findByCustomerId(customerId);
        } else if (state != null) {
            return orderRepository.findByStateIgnoreCase(state);
        } else {
            return orderRepository.findAll();
        }
    }
    
}
