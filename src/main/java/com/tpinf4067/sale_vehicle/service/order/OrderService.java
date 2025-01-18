package com.tpinf4067.sale_vehicle.service.order;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.service.catalog.VehicleService;
import com.tpinf4067.sale_vehicle.service.document.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final VehicleService vehicleService;
    private final PDFDocumentAdapter pdfAdapter;

    public OrderService(OrderRepository orderRepository, VehicleService vehicleService) {
        this.orderRepository = orderRepository;
        this.vehicleService = vehicleService;
        this.pdfAdapter = new PDFDocumentAdapter();
    }

    public Order createOrder(Long vehicleId) {
        Vehicle vehicle = vehicleService.getAllVehicles().stream()
                .filter(v -> v.getId().equals(vehicleId))
                .findFirst()
                .orElse(null);

        if (vehicle == null) {
            return null;
        }

        Order order = new Order();
        order.setVehicle(vehicle);
      

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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

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
}
