package com.tpinf4067.sale_vehicle.service.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
    
        if (userOptional.isEmpty()) {
            return null; // Aucun utilisateur trouvé
        }
    
        User user = userOptional.get(); // Récupération correcte de l'utilisateur
    
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(username);
        }
        return null;
    }
    

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
