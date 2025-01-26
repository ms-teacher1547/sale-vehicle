package com.tpinf4067.sale_vehicle.Controller;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    @ControllerAdvice
    public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCartForCustomer(customerId));
    }

    // ✅ Modifié pour accepter JSON au lieu de RequestParams
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request.getCustomerId(), request.getVehicleId(), request.getOptions(), request.getQuantity()));
    }

    @DeleteMapping("/{cartId}/remove/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        cartService.removeFromCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<String> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok("✅ Panier vidé avec succès !");
    }


    // ✅ Classe interne pour structurer les données JSON envoyées
    public static class CartRequest {
        private Long customerId;
        private Long vehicleId;
        private List<Long> options;
        private int quantity; // Ajout de la quantité

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public Long getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(Long vehicleId) {
            this.vehicleId = vehicleId;
        }

        public List<Long> getOptions() {
            return options;
        }

        public void setOptions(List<Long> options) {
            this.options = options;
        }

        public int getQuantity() { // Getter pour la quantité
            return quantity;
        }

        public void setQuantity(int quantity) { // Setter pour la quantité
            this.quantity = quantity;
        }
    }

}
