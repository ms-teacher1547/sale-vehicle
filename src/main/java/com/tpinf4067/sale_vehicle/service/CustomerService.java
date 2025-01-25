package com.tpinf4067.sale_vehicle.service;

import org.springframework.stereotype.Service;

import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;
import com.tpinf4067.sale_vehicle.repository.CustomerRepository;

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

    // // ✅ Ajout de la méthode de création
    // public Customer createCustomer(Customer customer) {
    //     return customerRepository.save(customer);
    // }

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

    // ✅ Ajout de la méthode pour ajouter des filiales
    public Customer addSubsidiary(Long companyId, Long subsidiaryId) {
        Customer company = customerRepository.findById(companyId).orElse(null);
        Customer subsidiary = customerRepository.findById(subsidiaryId).orElse(null);
    
        if (company == null || subsidiary == null) {
            return null; // L'un des deux clients n'existe pas
        }
    
        if (company.getType() != CustomerType.COMPANY) {
            throw new IllegalStateException("Seuls les clients de type COMPANY peuvent avoir des filiales.");
        }
    
        if (subsidiary.getType() != CustomerType.COMPANY) {
            throw new IllegalStateException("Seuls les clients de type COMPANY peuvent être des filiales.");
        }
    
        company.getSubsidiaries().add(subsidiary);
        return customerRepository.save(company);
    }

    // ✅ Ajout de la méthode pour récupérer les filiales
    public List<Customer> getSubsidiaries(Long companyId) {
        Customer company = customerRepository.findById(companyId).orElse(null);
        if (company == null || company.getType() != CustomerType.COMPANY) {
            return null; // Le client n'existe pas ou n'est pas une entreprise
        }
        return company.getSubsidiaries();
    }
    
    public Customer createCustomer(Customer customer) {
        if (customer.getType() == CustomerType.INDIVIDUAL && 
            customer.getSubsidiaries() != null && !customer.getSubsidiaries().isEmpty()) {
            throw new IllegalStateException("Un client INDIVIDUAL ne peut pas avoir de filiales.");
        }
        if (customer.getType() == CustomerType.COMPANY && 
            (customer.getName() == null || customer.getAddress() == null)) {
            throw new IllegalStateException("Une société doit avoir un nom et une adresse.");
        }
        return customerRepository.save(customer);
    }
}
