package com.tpinf4067.sale_vehicle.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.service.CustomerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ✅ Récupérer tous les clients
    @GetMapping("/")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // ✅ Récupérer un client par ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Ajouter un client en fonction de l'utilisateur connecté
    @PostMapping("/")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer, 
                                                   @AuthenticationPrincipal User user) {
        Customer savedCustomer = customerService.createCustomerForUser(customer, user);
        return ResponseEntity.ok(savedCustomer);
    }

    // ✅ Mettre à jour un client
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        Customer updated = customerService.updateCustomer(id, updatedCustomer);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // ✅ Supprimer un client
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ✅ Ajouter une filiale à une société
    @PostMapping("/{companyId}/subsidiaries")
    public ResponseEntity<Customer> addSubsidiary(
            @PathVariable Long companyId,
            @RequestBody Customer subsidiary,
            @AuthenticationPrincipal User currentUser) {
        
        Customer updatedCompany = customerService.addSubsidiary(companyId, subsidiary, currentUser);
        return updatedCompany != null ? ResponseEntity.ok(updatedCompany) : ResponseEntity.badRequest().build();
    }
    

    // ✅ Endpoint pour voir les filiales
    @GetMapping("/{companyId}/subsidiaries")
    public ResponseEntity<List<Customer>> getSubsidiaries(@PathVariable Long companyId) {
        List<Customer> subsidiaries = customerService.getSubsidiaries(companyId);
        return subsidiaries != null ? ResponseEntity.ok(subsidiaries) : ResponseEntity.notFound().build();
    }
}
