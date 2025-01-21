package com.tpinf4067.sale_vehicle.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpinf4067.sale_vehicle.patterns.document.Document;
import com.tpinf4067.sale_vehicle.patterns.document.DocumentLiasseSingleton;
import com.tpinf4067.sale_vehicle.patterns.order.Order;
import com.tpinf4067.sale_vehicle.service.OrderService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ Création d'une commande à partir du panier
    @PostMapping("/")
    public ResponseEntity<String> createOrder(@RequestParam Long customerId) {
        Order order = orderService.createOrderFromCart(customerId);
        return order != null ? ResponseEntity.ok("✅ Commande créée avec succès.") :
                ResponseEntity.badRequest().body("❌ Impossible de créer la commande.");
    }

    // ✅ Récupération de toutes les commandes
    @GetMapping("/")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // ✅ Récupération des documents de commande
    @GetMapping("/documents")
    public List<Document> getAllDocuments() {
        return DocumentLiasseSingleton.getInstance().getDocuments();
    }

    // ✅ Télécharger un fichier PDF
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadPDF(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("documents").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ Changer le statut d'une commande
    @PutMapping("/{orderId}/next")
    public String nextStatus(@PathVariable Long orderId) {
        Order order = orderService.changeOrderStatus(orderId, true);
        return order != null ? "✅ Nouveau statut : " + order.getState() : "❌ Commande non trouvée.";
    }

    @PutMapping("/{orderId}/previous")
    public String previousStatus(@PathVariable Long orderId) {
        Order order = orderService.changeOrderStatus(orderId, false);
        return order != null ? "✅ Nouveau statut : " + order.getState() : "❌ Commande non trouvée.";
    }

    // ✅ Rechercher des commandes par client et/ou état
    @GetMapping("/search")
    public List<Order> searchOrders(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String state) {
        return orderService.searchOrders(customerId, state);
    }
}
