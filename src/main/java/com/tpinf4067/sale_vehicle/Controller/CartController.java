package com.tpinf4067.sale_vehicle.Controller;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

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
        return ResponseEntity.ok(cartService.addToCart(request.getCustomerId(), request.getVehicleId(), request.getOptions()));
    }

    @DeleteMapping("/{cartId}/remove/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        cartService.removeFromCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Classe interne pour structurer les données JSON envoyées
    public static class CartRequest {
        private Long customerId;
        private Long vehicleId;
        private List<Long> options;

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
    }
}
