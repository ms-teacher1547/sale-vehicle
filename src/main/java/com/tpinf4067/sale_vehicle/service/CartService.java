package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.domain.*;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.repository.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final VehicleRepository vehicleRepository;
    private final OptionRepository optionRepository;
    private final IncompatibleOptionsRepository incompatibleOptionsRepository;

    public CartService(CartRepository cartRepository, VehicleRepository vehicleRepository,
                       OptionRepository optionRepository, IncompatibleOptionsRepository incompatibleOptionsRepository) {
        this.cartRepository = cartRepository;
        this.vehicleRepository = vehicleRepository;
        this.optionRepository = optionRepository;
        this.incompatibleOptionsRepository = incompatibleOptionsRepository;
    }

    public Cart getCartForCustomer(Long customerId) {
        return cartRepository.findByCustomerId(customerId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCustomer(new Customer(customerId));
            return cartRepository.save(newCart);
        });
    }

    public Cart addToCart(Long customerId, Long vehicleId, List<Long> optionIds) {
        Cart cart = getCartForCustomer(customerId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé avec ID: " + vehicleId));

        List<Option> options = optionRepository.findAllById(optionIds);

        // Vérification des incompatibilités
        for (int i = 0; i < options.size(); i++) {
            for (int j = i + 1; j < options.size(); j++) {
                if (incompatibleOptionsRepository.existsByOption1AndOption2(options.get(i), options.get(j))) {
                    throw new IllegalStateException("Les options " + options.get(i).getName() + " et " + options.get(j).getName() + " sont incompatibles.");
                }
            }
        }

        // Création de l'item du panier
        CartItem cartItem = new CartItem();
        cartItem.setVehicle(vehicle);
        cartItem.setOptions(options);
        cartItem.setCart(cart); // Lien bidirectionnel

        // 
        cart.getItems().add(cartItem);
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        Cart savedCart = cartRepository.save(cart);

        System.out.println("✅ Article ajouté au panier: " + cartItem);
        return savedCart;
    }

    public void removeFromCart(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Panier non trouvé avec ID: " + cartId));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new IllegalArgumentException("Article non trouvé dans le panier avec ID: " + itemId);
        }

        cartRepository.save(cart);
        System.out.println("🗑️ Article supprimé du panier.");
    }

    public void clearCart(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                                  .orElseThrow(() -> new IllegalArgumentException("Panier non trouvé"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    
}
