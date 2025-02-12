package com.tpinf4067.sale_vehicle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.tpinf4067.sale_vehicle.patterns.auth.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowCredentials(true);
                config.addAllowedOrigin("http://localhost:3000"); // ✅ Autorise React
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");
                return config;
            }))
            .csrf(AbstractHttpConfigurer::disable) // ❌ Désactive CSRF car on utilise des sessions HTTP
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated() // ✅ Assure que seul un utilisateur connecté peut accéder à /me
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/catalog/vehicles/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/catalog/vehicles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/catalog/vehicles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/catalog/vehicles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/my-orders").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/orders/my-documents").hasAnyRole("ADMIN","USER")
                .requestMatchers(HttpMethod.GET, "/api/orders/download/{id}").hasAnyRole("ADMIN","USER")
                .requestMatchers(HttpMethod.GET, "/api/orders/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/customers/").hasRole("ADMIN") // ✅ Seul ADMIN peut voir tous les clients
                .requestMatchers(HttpMethod.GET, "/api/customers/{id}").hasAnyRole("ADMIN", "USER") // ✅ Un USER peut voir ses propres infos
                .requestMatchers(HttpMethod.PUT, "/api/customers/{id}").hasAnyRole("ADMIN", "USER") // ✅ Un USER peut modifier son propre compte
                .requestMatchers(HttpMethod.DELETE, "/api/customers/{id}").hasAnyRole("ADMIN", "USER") // ✅ Seul ADMIN peut supprimer un client
                .requestMatchers(HttpMethod.POST, "/api/customers/{companyId}/subsidiaries").hasAnyRole("ADMIN", "USER") // ✅ Un COMPANY peut ajouter une filiale
                .requestMatchers(HttpMethod.GET, "/api/customers/{companyId}/subsidiaries").hasAnyRole("ADMIN", "USER") // ✅ Un COMPANY peut voir ses filiales               
                .requestMatchers(HttpMethod.POST, "/api/fleet-proposals/company/{companyId}").hasAnyRole("ADMIN", "USER") // ✅ Créer une proposition de flotte
                .requestMatchers(HttpMethod.GET, "/api/fleet-proposals/company/{companyId}").hasAnyRole("ADMIN", "USER") // ✅ Voir les propositions de flotte d'une entreprise
                .requestMatchers(HttpMethod.GET, "/api/fleet-proposals/{proposalId}").hasAnyRole("ADMIN", "USER") // ✅ Voir une proposition de flotte spécifique
                .requestMatchers(HttpMethod.PUT, "/api/fleet-proposals/{proposalId}/status").hasAnyRole("ADMIN", "USER") // ✅ Mettre à jour le statut d'une proposition
                .requestMatchers(HttpMethod.GET, "/api/fleet-proposals/status/{status}").hasRole("ADMIN") // ✅ Voir les propositions par statut (admin uniquement)
                .requestMatchers(HttpMethod.GET, "/api/companies/with-subsidiaries").hasAnyRole("ADMIN", "USER") // ✅ Voir les entreprises avec filiales
                .requestMatchers(HttpMethod.POST, "/api/payments/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/payments/my-invoices").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/payments/invoice/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/payments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/payments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/options/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/options/incompatible").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/options/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/options/**").permitAll()
                .anyRequest().authenticated()
            )
                .formLogin(AbstractHttpConfigurer::disable) // 🚨 **Désactive la redirection automatique vers la page de login HTML**
                .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)) // ✅ Répondre 200 OK
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll() // ✅ Assure que tout le monde peut accéder à /logout
            )

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ✅ Maintient les sessions actives
                .maximumSessions(1)
                .expiredUrl("/api/auth/login?expired")
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> 
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
