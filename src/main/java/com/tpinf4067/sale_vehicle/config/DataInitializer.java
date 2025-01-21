package com.tpinf4067.sale_vehicle.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tpinf4067.sale_vehicle.patterns.catalog.observer.EmailNotifier;
import com.tpinf4067.sale_vehicle.service.VehicleService;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(VehicleService vehicleService) {
        return args -> {
            // Ajout de l'observateur EmailNotifier
            vehicleService.addObserver(new EmailNotifier());
            System.out.println("✅ Observateur EmailNotifier enregistré !");
        };
    }
}
