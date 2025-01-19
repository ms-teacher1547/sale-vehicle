package com.tpinf4067.sale_vehicle.service.customer;

import org.springframework.stereotype.Service;

import com.tpinf4067.sale_vehicle.service.customer.enums.CustomerType;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    // ✅ Ajout du repository
    private final CustomerRepository customerRepository;

    // ✅ Ajout du constructeur
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ✅ Ajout de la méthode de recherche
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // ✅ Ajout de la méthode de recherche par ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // ✅ Ajout de la méthode de création
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // ✅ Ajout de la méthode de mise à jour
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id).map(customer -> {
            customer.setName(updatedCustomer.getName());
            customer.setEmail(updatedCustomer.getEmail());
            customer.setAddress(updatedCustomer.getAddress());
            customer.setType(updatedCustomer.getType());
            customer.setSubsidiaries(updatedCustomer.getSubsidiaries()); // 📌 Mise à jour des filiales
            return customerRepository.save(customer);
        }).orElse(null);
    }

    // ✅ Ajout de la méthode de suppression
    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // ✅ Ajout de la méthode de recherche
    public List<Customer> searchCustomers(String name, CustomerType type) {
        if (name != null && type != null) {
            return customerRepository.findByNameContainingIgnoreCase(name)
                    .stream()
                    .filter(c -> c.getType().equals(type))
                    .toList();
        } else if (name != null) {
            return customerRepository.findByNameContainingIgnoreCase(name);
        } else if (type != null) {
            return customerRepository.findByType(type);
        } else {
            return customerRepository.findAll();
        }
    }
}
