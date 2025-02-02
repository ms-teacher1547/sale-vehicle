package com.tpinf4067.sale_vehicle.Controller;

import com.tpinf4067.sale_vehicle.patterns.auth.Role;
import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;
import com.tpinf4067.sale_vehicle.repository.UserRepository;
import com.tpinf4067.sale_vehicle.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, CustomerService customerService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerService = customerService;
    }

    // ✅ **INSCRIPTION : Création automatique du client si USER**
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsernameWithCustomer(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("⚠️ Ce nom d'utilisateur est déjà pris.");
        }
    
        // 🔥 Hacher le mot de passe
        String hashedPassword = passwordEncoder.encode(request.getPassword());
    
        // 🔥 Vérifier le rôle (par défaut, `USER`)
        Role role = request.getRole() != null ? request.getRole() : Role.USER;
    
        // 🔥 Créer l'utilisateur
        User user = new User(request.getUsername(), hashedPassword, role, request.getFullName(), request.getEmail());
        userRepository.save(user);
    
        // 🔥 Si c'est un USER, créer aussi un Customer
        if (role == Role.USER) {
            Customer customer = new Customer();
            customer.setName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setAddress(request.getAddress());
            customer.setType(request.getType() != null ? request.getType() : CustomerType.INDIVIDUAL);
    
            // 🔥 Associer le client à l'utilisateur
            customerService.createCustomerForUser(customer, user);
        }
    
        return ResponseEntity.ok("✅ Inscription réussie !");
    }

    // ✅ **CONNEXION : Création de la session HTTP**
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            return ResponseEntity.ok("✅ Connexion réussie !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("⚠️ Identifiants incorrects");
        }
    }

    // ✅ **RÉCUPÉRER LE PROFIL UTILISATEUR + INFORMATIONS DU CLIENT**
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("⚠️ Aucun utilisateur connecté");
        }
    
        // Utiliser la méthode avec JOIN FETCH
        Optional<User> user = userRepository.findByUsernameWithCustomer(principal.getName());
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserProfileResponse(user.get(), user.get().getCustomer()));
        }
    
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    

    // ✅ **DÉCONNEXION : Suppression de la session HTTP**
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getSession().invalidate(); // ✅ Supprime la session serveur
            SecurityContextHolder.clearContext();
            response.setHeader("Set-Cookie", "JSESSIONID=; HttpOnly; Path=/; Max-Age=0;"); // ✅ Force suppression du cookie
            return ResponseEntity.ok("✅ Déconnexion réussie !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Erreur lors de la déconnexion");
        }
    }
    

    // ✅ **Classe pour gérer la requête d'inscription avec getters et setters**
    private static class RegisterRequest {
        private String username;
        private String password;
        private String fullName;
        private String email;
        private String address;
        private CustomerType type;
        private Role role;

        public String getUsername() { return username; }
        @SuppressWarnings("unused")
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        @SuppressWarnings("unused")
        public void setPassword(String password) { this.password = password; }

        public String getFullName() { return fullName; }
        @SuppressWarnings("unused")
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        @SuppressWarnings("unused")
        public void setEmail(String email) { this.email = email; }

        public String getAddress() { return address; }
        @SuppressWarnings("unused")
        public void setAddress(String address) { this.address = address; }

        public CustomerType getType() { return type; }
        @SuppressWarnings("unused")
        public void setType(CustomerType type) { this.type = type; }

        public Role getRole() { return role; }
        @SuppressWarnings("unused")
        public void setRole(Role role) { this.role = role; }
    }

    // ✅ **Classe pour renvoyer l'utilisateur et ses informations de client**
    private static class UserProfileResponse {
        public String username;
        public String role;
        public Customer customer;

        public UserProfileResponse(User user, Customer customer) {
            this.username = user.getUsername();
            this.role = user.getRole().name();
            this.customer = customer;
        }

        @SuppressWarnings("unused")
        public String getUsername() { return username; }

        @SuppressWarnings("unused")
        public String getRole() { return role; }

        @SuppressWarnings("unused")
        public Customer getCustomer() {return customer; }
    }
}
