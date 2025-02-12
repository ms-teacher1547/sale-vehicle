package com.tpinf4067.sale_vehicle.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpinf4067.sale_vehicle.patterns.auth.Role;
import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;
import com.tpinf4067.sale_vehicle.repository.CustomerRepository;
import com.tpinf4067.sale_vehicle.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    // ✅ Récupérer tous les clients
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // ✅ Récupérer un client par ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // ✅ Créer un client et l'associer à un utilisateur
    public Customer createCustomerForUser(Customer customer, User user) {
        // Vérifier si un client avec cet utilisateur existe déjà
        Optional<Customer> existingCustomer = customerRepository.findByEmail(user.getEmail());
        if (existingCustomer.isPresent()) {
            throw new IllegalStateException("Un client existe déjà pour cet utilisateur.");
        }
    
        // Associer l'utilisateur au client avec les bonnes données
        customer.setEmail(user.getEmail()); // ✅ Transférer l'email de l'utilisateur
        customer.setName(user.getFullname()); // ✅ Transférer le nom complet
        customer.setAddress(customer.getAddress()); // ✅ Conserver l'adresse fournie dans la requête
        customer.setType(customer.getType()); // ✅ Conserver le type fourni dans la requête
        customer.setUser(user); // ✅ Associer correctement le User au Customer
    
        // Enregistrer le client
        Customer savedCustomer = customerRepository.save(customer);
    
        // Associer le client à l'utilisateur
        user.setCustomer(savedCustomer);
        userRepository.save(user);
        return savedCustomer;
    }

    // ✅ Mettre à jour un client
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id).map(customer -> {
            customer.setName(updatedCustomer.getName());
            customer.setEmail(updatedCustomer.getEmail());
            customer.setAddress(updatedCustomer.getAddress());
            customer.setType(updatedCustomer.getType());
            customer.setSubsidiaries(updatedCustomer.getSubsidiaries());
            return customerRepository.save(customer);
        }).orElse(null);
    }

    // ✅ Supprimer un client
    @Transactional
    public boolean deleteCustomer(Long id, User currentUser) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
    
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
    
            // 🔹 Vérifier si c'est un ADMIN (peut tout supprimer)
            if (currentUser.getRole() == Role.ADMIN) {
                if (customer.getUser() != null) {
                    userRepository.delete(customer.getUser()); // ✅ Supprime d'abord l'utilisateur lié
                }
                customerRepository.delete(customer);
                return true;
            }
    
            // 🔹 Vérifier si c'est une COMPANY et si la filiale lui appartient
            if (currentUser.getCustomer() != null && currentUser.getCustomer().getType() == CustomerType.COMPANY) {
                boolean isSubsidiary = currentUser.getCustomer().getSubsidiaries().stream()
                        .anyMatch(sub -> sub.getId().equals(id));
                if (isSubsidiary) {
                    if (customer.getUser() != null) {
                        userRepository.delete(customer.getUser()); // ✅ Supprime d'abord l'utilisateur lié
                    }
                    customerRepository.delete(customer);
                    return true;
                }
            }
        }
        return false;
    }
    
    
    
    // ✅ **Récupérer un client par email**
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }

    public List<Customer> getSubsidiaries(Long companyId) {
        Customer company = customerRepository.findById(companyId).orElse(null);
        if (company == null || company.getType() != CustomerType.COMPANY) {
            return null;
        }
        return company.getSubsidiaries();
    }

    public Customer addSubsidiary(Long companyId, Customer subsidiary, User currentUser) {
        // 🔥 Vérifier que la COMPANY existe
        Customer company = customerRepository.findById(companyId).orElse(null);
        if (company == null || company.getType() != CustomerType.COMPANY) {
            throw new IllegalStateException("❌ Seuls les clients de type COMPANY peuvent avoir des filiales.");
        }
    
        // 🔥 Vérifier que l'utilisateur actuel est bien propriétaire de l'entreprise
        if (!company.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("🚫 Vous ne pouvez pas ajouter une filiale à une entreprise qui ne vous appartient pas !");
        }
    
        // ✅ Ajouter la filiale
        subsidiary.setType(CustomerType.FILIALE);
        company.getSubsidiaries().add(subsidiary);
        return customerRepository.save(company);
    }
    
}
