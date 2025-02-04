package com.tpinf4067.sale_vehicle.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.document.Document;
import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;
import com.tpinf4067.sale_vehicle.repository.DocumentRepository;
import com.tpinf4067.sale_vehicle.service.OrderService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final DocumentRepository documentRepository;

    public OrderController(OrderService orderService, DocumentRepository documentRepository) {
        this.orderService = orderService;
        this.documentRepository = documentRepository;
    }

    // ✅ Création d'une commande à partir du panier
    @PostMapping("/")
    public ResponseEntity<String> createOrder(
                                              @AuthenticationPrincipal User user) {
        if (user.getCustomer() == null) {
            return ResponseEntity.badRequest().body("❌ Aucun client associé à cet utilisateur.");
        }

        Order order = orderService.createOrderFromCart(user.getCustomer().getId());
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
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Order> orders = orderService.getAllOrders();
        List<Document> allDocuments = orders.stream()
            .flatMap(order -> order.getDocuments().stream())
            .toList();
    
        return ResponseEntity.ok(allDocuments);
    }
    
    // ✅ Récupérer les documents d'un client spécifique
    @GetMapping("/my-documents")
    public ResponseEntity<List<Document>> getMyDocuments(@AuthenticationPrincipal User user) {
        if (user.getCustomer() == null) {
            return ResponseEntity.badRequest().body(null);
        }
    
        List<Order> orders = orderService.getOrdersByCustomer(user);
        List<Document> myDocuments = orders.stream()
            .flatMap(order -> order.getDocuments().stream())
            .toList();
    
        return ResponseEntity.ok(myDocuments);
    }
    


    // ✅ Télécharger un document PDF par son ID
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            // 🔥 Récupérer le document par son ID
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document non trouvé !"));

            // 🔥 Vérifier si le fichier existe dans le dossier "documents"
            // Remplacement des caractères spéciaux avant de construire le chemin
            String safeFilename = document.getFilename().replace("’", "'"); // Remplace l’apostrophe spéciale
            safeFilename = safeFilename.replace(" ", "_"); // Remplace les espaces par des underscores

            Path filePath = Paths.get("documents").resolve(safeFilename).normalize();
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


    // ✅ Changer le statut d'une commande spécifique (ADMIN uniquement)
    @PutMapping("/{orderId}/{action}")
    public ResponseEntity<String> changeOrderStatus(@PathVariable Long orderId, @PathVariable String action) {
        boolean next = "next".equalsIgnoreCase(action);
        Order order = orderService.changeOrderStatus(orderId, next);
        return order != null ? ResponseEntity.ok("✅ Nouveau statut : " + order.getState()) :
                ResponseEntity.badRequest().body("❌ Commande non trouvée.");
    }

    // ✅ Changer l'état de la dernière commande du client connecté
    @PutMapping("/status")
    public ResponseEntity<String> changeLastOrderStatus(@AuthenticationPrincipal User user, @RequestParam boolean next) {
        if (user.getCustomer() == null) {
            return ResponseEntity.badRequest().body("❌ Aucun client associé à cet utilisateur.");
        }

        Order updatedOrder = orderService.changeLastOrderStatusForUser(user, next);
        return updatedOrder != null ? ResponseEntity.ok("✅ Nouveau statut : " + updatedOrder.getState()) :
                ResponseEntity.badRequest().body("❌ Aucune commande trouvée.");
    }

    // ✅ Rechercher des commandes par client et/ou état
    @GetMapping("/search")
    public List<Order> searchOrders(@RequestParam(required = false) Long customerId,
                                    @RequestParam(required = false) String state) {
        return orderService.searchOrders(customerId, state);
    }

    // ✅ Récupérer les commandes du client connecté
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal User user) {
        System.out.println("Utilisateur connecté : " + user.getUsername() + " - Rôles : " + user.getAuthorities());
    
        if (user.getCustomer() == null) {
            return ResponseEntity.badRequest().body(null);
        }
    
        List<Order> orders = orderService.getOrdersByCustomer(user);
        return ResponseEntity.ok(orders);
    }
    

}
