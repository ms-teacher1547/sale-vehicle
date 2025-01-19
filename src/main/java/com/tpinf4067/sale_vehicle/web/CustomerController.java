package com.tpinf4067.sale_vehicle.web;

import com.tpinf4067.sale_vehicle.service.customer.Customer;
import com.tpinf4067.sale_vehicle.service.customer.CustomerService;
import com.tpinf4067.sale_vehicle.service.customer.enums.CustomerType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        Customer updated = customerService.updateCustomer(id, updatedCustomer);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 📌 Ajouter des filiales à une société
    @PostMapping("/{companyId}/subsidiaries")
    public ResponseEntity<Customer> addSubsidiary(@PathVariable Long companyId, @RequestBody Customer subsidiary) {
        Optional<Customer> company = customerService.getCustomerById(companyId);
        if (company.isPresent() && company.get().getType() == CustomerType.COMPANY) {
            company.get().getSubsidiaries().add(subsidiary);
            return ResponseEntity.ok(customerService.updateCustomer(companyId, company.get()));
        }
        return ResponseEntity.badRequest().build();
    }
}
