package com.tpinf4067.sale_vehicle.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpinf4067.sale_vehicle.domain.Car;
import com.tpinf4067.sale_vehicle.domain.Scooter;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.repository.VehicleRepository;
import com.tpinf4067.sale_vehicle.service.VehicleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;

    public CatalogController(VehicleService vehicleService, VehicleRepository vehicleRepository) {
        this.vehicleService = vehicleService;
        this.vehicleRepository = vehicleRepository;
    }

    // ✅ Récupérer tous les véhicules avec affichage décoré
    // ✅ Maintenant, on retourne une vraie liste JSON d'objets Vehicle !
    @GetMapping("/vehicles")
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        
        // 🔥 Vérification pour éviter les null
        vehicles.forEach(vehicle -> {
            if (vehicle.getImageUrl() == null && vehicle.getAnimationUrl() != null) {
                vehicle.setImageUrl(vehicle.getAnimationUrl());
            }
        });
    
        return vehicles;
    }
    


    // ✅ Ajouter une voiture avec image
    @PostMapping(value = "/vehicles/car", consumes = {"multipart/form-data"})
    public ResponseEntity<Vehicle> addCar(
            @RequestPart("vehicle") String vehicleJson,
            @RequestPart("image") MultipartFile imageFile) {
        
        try {
            // 🔥 Convertir le JSON en objet `Car`
            ObjectMapper objectMapper = new ObjectMapper();
            Car car = objectMapper.readValue(vehicleJson, Car.class);

            // 🔥 Sauvegarde du véhicule
            Vehicle savedVehicle = vehicleService.saveVehicle(car);

            // 🔥 Sauvegarde de l'image si elle est fournie
            if (!imageFile.isEmpty()) {
                String imageUrl = vehicleService.saveVehicleImage(savedVehicle.getId(), imageFile);
                savedVehicle.setAnimationUrl(imageUrl);
                vehicleService.saveVehicle(savedVehicle);
            }

            return ResponseEntity.ok(savedVehicle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    // ✅ Ajouter un scooter avec image
    @PostMapping(value = "/vehicles/scooter", consumes = {"multipart/form-data"})
    public ResponseEntity<Vehicle> addScooter(
            @RequestPart("vehicle") String vehicleJson,
            @RequestPart("image") MultipartFile imageFile) {
        
        try {
            // 🔥 Convertir le JSON en objet `Scooter`
            ObjectMapper objectMapper = new ObjectMapper();
            Scooter scooter = objectMapper.readValue(vehicleJson, Scooter.class);
    
            // 🔥 Sauvegarde du véhicule
            Vehicle savedVehicle = vehicleService.saveVehicle(scooter);
    
            // 🔥 Sauvegarde de l'image si elle est fournie
            if (!imageFile.isEmpty()) {
                String imageUrl = vehicleService.saveVehicleImage(savedVehicle.getId(), imageFile);
                savedVehicle.setAnimationUrl(imageUrl);
                vehicleService.saveVehicle(savedVehicle);
            }
    
            return ResponseEntity.ok(savedVehicle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    

    // ✅ Supprimer un véhicule
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null) {
                return ResponseEntity.notFound().build();
            }
            
            vehicleService.deleteVehicleById(id);
            return ResponseEntity.ok("Vehicle avec l'id " + id + " a été supprimé avec succès");
        } catch (Exception e) {
            logger.error("Error deleting vehicle with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la suppression du véhicule: " + e.getMessage());
        }
    }

    // ✅ Recherche avancée dans le catalogue
    @GetMapping("/vehicles/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String keywords,
            @RequestParam(defaultValue = "OR") String operator) {

        if (keywords != null) {
            return ResponseEntity.ok(vehicleService.searchVehicles(keywords, operator));
        } else {
            return ResponseEntity.ok(vehicleService.searchVehicles(name, priceMin, priceMax));
        }
    }

    // ✅ Récupérer l'animation d'un véhicule
    @GetMapping("/vehicles/{id}/animation")
    public ResponseEntity<String> getVehicleAnimation(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return (vehicle != null && vehicle.getAnimationUrl() != null) ? 
                ResponseEntity.ok(vehicle.getAnimationUrl()) : 
                ResponseEntity.notFound().build();
    }

    // ✅ Mettre à jour l'animation d'un véhicule
    @PutMapping("/vehicles/{id}/animation")
    public ResponseEntity<Vehicle> updateVehicleAnimation(@PathVariable Long id, @RequestBody String animationUrl) {
        Vehicle updatedVehicle = vehicleService.updateVehicleAnimation(id, animationUrl);
        return updatedVehicle != null ? ResponseEntity.ok(updatedVehicle) : ResponseEntity.notFound().build();
    }

    // ✅ Présentation du catalogue (1 véhicule par ligne)
    @GetMapping("/vehicles/list")
    public ResponseEntity<List<List<Vehicle>>> getCatalogAsList() {
        return ResponseEntity.ok(vehicleService.getCatalogView(1));
    }

    // ✅ Présentation du catalogue (3 véhicules par ligne)
    @GetMapping("/vehicles/grid")
    public ResponseEntity<List<List<Vehicle>>> getCatalogAsGrid() {
        return ResponseEntity.ok(vehicleService.getCatalogView(3));
    }

    // 🔹 Appliquer une remise sur les véhicules en stock depuis longtemps
    @PutMapping("/vehicles/discount")
    public ResponseEntity<List<Vehicle>> applyDiscountForOldStock() {
        List<Vehicle> discountedVehicles = vehicleService.applyDiscountForOldStock();
        return ResponseEntity.ok(discountedVehicles);
    }

    // 🔹 Mettre à jour les détails d'un véhicule
    @PutMapping("/vehicles/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVehicleDetails(
            @PathVariable Long id,
            @RequestBody Vehicle updatedVehicle) {
        try {
            // Récupérer le véhicule existant
            Vehicle existingVehicle = vehicleService.getVehicleById(id);
            if (existingVehicle == null) {
                return ResponseEntity.notFound().build();
            }

            // Créer une nouvelle instance du bon type
            Vehicle vehicleToUpdate;
            if (existingVehicle instanceof Car) {
                Car car = new Car();
                car.setId(id);
                car.setName(updatedVehicle.getName());
                car.setPrice(updatedVehicle.getPrice());
                car.setStockQuantity(updatedVehicle.getStockQuantity());
                car.setYearOfManufacture(updatedVehicle.getYearOfManufacture());
                car.setFuelType(updatedVehicle.getFuelType());
                car.setMileage(updatedVehicle.getMileage());
                car.setAnimationUrl(updatedVehicle.getAnimationUrl());
                car.setImageUrl(updatedVehicle.getImageUrl());
                
                // Set Car-specific properties
                if (updatedVehicle instanceof Car) {
                    car.setNumberOfDoors(((Car) updatedVehicle).getNumberOfDoors());
                } else {
                    // If not explicitly set, keep the existing number of doors
                    car.setNumberOfDoors(((Car) existingVehicle).getNumberOfDoors());
                }
                vehicleToUpdate = car;
            } else if (existingVehicle instanceof Scooter) {
                // Similar handling for Scooter if needed
                return ResponseEntity.badRequest().body("Cannot update Scooter type");
            } else {
                return ResponseEntity.badRequest().body("Unknown vehicle type");
            }
            
            // Appeler le service pour mettre à jour le véhicule
            Vehicle updated = vehicleService.updateVehicle(vehicleToUpdate);
            
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du véhicule {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @Getter
    @AllArgsConstructor
    private static class ErrorResponse {
        private final String error;
        private final String message;
    }

    // ✅ Upload d'une image pour un véhicule
    @PostMapping("/vehicles/{id}/image")
    public ResponseEntity<Vehicle> uploadVehicleImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Vehicle updatedVehicle = vehicleService.uploadVehicleImage(id, file);
        return updatedVehicle != null ? ResponseEntity.ok(updatedVehicle) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // ✅ Récupérer un véhicule avec son image
    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> getVehicleDetails(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return vehicle != null ? ResponseEntity.ok(vehicle) : ResponseEntity.notFound().build();
    }

    public class VehicleUpdateRequest {
        private int stockQuantity;
        private int yearOfManufacture;
        private String fuelType;
        private int mileage;
    
        // Getters et setters
        public int getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        public int getYearOfManufacture() {
            return yearOfManufacture;
        }

        public void setYearOfManufacture(int yearOfManufacture) {
            this.yearOfManufacture = yearOfManufacture;
        }

        public String getFuelType() {
            return fuelType;
        }

        public void setFuelType(String fuelType) {
            this.fuelType = fuelType;
        }

        public int getMileage() {
            return mileage;
        }

        public void setMileage(int mileage) {
            this.mileage = mileage;
        }
    }
}