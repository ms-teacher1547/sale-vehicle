package com.tpinf4067.sale_vehicle.web;

import com.tpinf4067.sale_vehicle.service.document.DocumentLiasseSingleton;
import com.tpinf4067.sale_vehicle.service.order.Order;
import com.tpinf4067.sale_vehicle.service.order.OrderService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // 🔥 Injection de dépendance du service de commande
    private final OrderService orderService;

    // 🔥 Constructeur
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 🔥 Création d'une commande avec un client associé
    @PostMapping("/")
    public ResponseEntity<String> createOrder(@RequestParam Long vehicleId, @RequestParam Long customerId) {
        Order order = orderService.createOrder(vehicleId, customerId);
        if (order == null) {
            return ResponseEntity.badRequest().body("❌ Véhicule ou Client non trouvé.");
        }
        return ResponseEntity.ok("✅ Commande créée pour " + order.getVehicle().getName() + " par " + order.getCustomer().getName());
    }

    // 🔥 Récupération de toutes les commandes
    @GetMapping("/")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 🔥 Récupération des commandes par client
    @GetMapping("/documents")
    public String getAllDocuments() {
        DocumentLiasseSingleton.getInstance().showAllDocuments();
        return "📂 Liasse affichée dans la console.";
    }

    // 🔥 Endpoint pour télécharger un fichier PDF
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadPDF(@PathVariable String filename) {
        try {
            // Définir le chemin du fichier
            Path filePath = Paths.get("documents").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Vérifier si le fichier existe et est lisible
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Renvoyer le fichier en tant que pièce jointe
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 🔥 Endpoint pour changer le statut d'une commande
    @PutMapping("/{orderId}/next")
    public String nextStatus(@PathVariable Long orderId) {
        Order order = orderService.changeOrderStatus(orderId, true);
        return order != null ? "✅ Nouveau statut : " + order.getStatus() : "❌ Commande non trouvée.";
    }

    @PutMapping("/{orderId}/previous")
    public String previousStatus(@PathVariable Long orderId) {
        Order order = orderService.changeOrderStatus(orderId, false);
        return order != null ? "✅ Nouveau statut : " + order.getStatus() : "❌ Commande non trouvée.";
    }

    // 🔥 Rechercher des commandes par client et/ou état
    @GetMapping("/search")
    public List<Order> searchOrders(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String state) {
        return orderService.searchOrders(customerId, state);
    }
}
