// package com.tpinf4067.sale_vehicle.patterns.auth;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import java.io.IOException;

// @Component
// public class JwtFilter extends OncePerRequestFilter {

//     private final JwtUtil jwtUtil;
//     private final UserDetailsService userDetailsService;

//     public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
//         this.jwtUtil = jwtUtil;
//         this.userDetailsService = userDetailsService;
//     }

//     @SuppressWarnings("null")
//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//             throws ServletException, IOException {

//         String authorizationHeader = request.getHeader("Authorization");

//         if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//             String token = authorizationHeader.substring(7);
//             String username = null;

//             try {
//                 username = jwtUtil.extractUsername(token);
//                 System.out.println("🔍 Extraction du token réussie. Utilisateur : " + username);
//             } catch (Exception e) {
//                 System.out.println("❌ Erreur lors de l'extraction du token : " + e.getMessage());
//             }

//             if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                 UserDetails userDetails = userDetailsService.loadUserByUsername(username);

//                 if (jwtUtil.validateToken(token, username)) {
//                     UsernamePasswordAuthenticationToken authToken =
//                             new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

//                     SecurityContextHolder.getContext().setAuthentication(authToken);
//                     System.out.println("✅ Utilisateur authentifié : " + username + " - Rôles : " + userDetails.getAuthorities());
//                 } else {
//                     System.out.println("⚠️ Token JWT invalide ou expiré !");
//                 }
//             }
//         } else {
//             System.out.println("⚠️ Aucun token JWT trouvé !");
//         }

//         chain.doFilter(request, response);
//     }
// }
