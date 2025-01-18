package com.tpinf4067.sale_vehicle.config;

import com.tpinf4067.sale_vehicle.service.catalog.VehicleService;
import com.tpinf4067.sale_vehicle.service.catalog.observer.EmailNotifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
