package com.tpinf4067.sale_vehicle.service.order;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.service.catalog.VehicleService;
import com.tpinf4067.sale_vehicle.service.customer.Customer;
import com.tpinf4067.sale_vehicle.service.customer.CustomerRepository;
import com.tpinf4067.sale_vehicle.service.document.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    // 🔥 Injection de dépendances
    private final OrderRepository orderRepository;
    private final VehicleService vehicleService;
    private final CustomerRepository customerRepository;
    private final PDFDocumentAdapter pdfAdapter;

    // 🔥 Constructeur
    public OrderService(OrderRepository orderRepository, VehicleService vehicleService, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.vehicleService = vehicleService;
        this.customerRepository = customerRepository;
        this.pdfAdapter = new PDFDocumentAdapter();
    }

    // 🔥 Création d'une commande
    public Order createOrder(Long vehicleId, Long customerId) {
        Vehicle vehicle = vehicleService.getAllVehicles().stream()
                .filter(v -> v.getId().equals(vehicleId))
                .findFirst()
                .orElse(null);

        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (vehicle == null || customer == null) {
            return null;
        }

        Order order = new Order();
        order.setVehicle(vehicle);
        order.setCustomer(customer);
        order.setState("EN_COURS");

        Order savedOrder = orderRepository.save(order);

        // 🔥 Génération du bon de commande
        OrderDocumentBuilder builder = new OrderDocumentBuilder();
        builder.constructOrderDocument(savedOrder);
        Document orderDocument = builder.getDocument();

        // 🔥 Ajout du document à la liasse unique
        DocumentLiasseSingleton liasse = DocumentLiasseSingleton.getInstance();
        liasse.addDocument(orderDocument);

        // 🔥 Export en PDF
        System.out.println("🔍 Appel de PDFDocumentAdapter.export() pour " + orderDocument.getTitle());
        pdfAdapter.export(orderDocument);

        System.out.println("📄 Bon de commande généré et exporté en PDF");

        return savedOrder;
    }

    // 🔥 Récupérer toutes les commandes
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 🔥 Changer l'état d'une commande
    public Order changeOrderStatus(Long orderId, boolean next) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null) {
            if (next) {
                order.nextState();
            } else {
                order.previousState();
            }
            orderRepository.save(order); // ✅ Sauvegarder l'état mis à jour
        }

        return order;
    }

    // 🔥 Rechercher des commandes
    public List<Order> searchOrders(Long customerId, String state) {
        if (customerId != null && state != null) {
            return orderRepository.findByCustomerId(customerId)
                    .stream()
                    .filter(o -> o.getState().equalsIgnoreCase(state)) // ✅ Correction de la comparaison
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
