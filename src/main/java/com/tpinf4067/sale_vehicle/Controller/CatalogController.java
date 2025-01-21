package com.tpinf4067.sale_vehicle.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpinf4067.sale_vehicle.domain.Car;
import com.tpinf4067.sale_vehicle.domain.Scooter;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.catalog.decorator.BasicVehicleDisplay;
import com.tpinf4067.sale_vehicle.patterns.catalog.decorator.IconDecorator;
import com.tpinf4067.sale_vehicle.patterns.catalog.decorator.PriceTagDecorator;
import com.tpinf4067.sale_vehicle.patterns.catalog.decorator.VehicleDisplay;
import com.tpinf4067.sale_vehicle.service.VehicleService;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final VehicleService vehicleService;

    public CatalogController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // ✅ Récupérer tous les véhicules avec affichage décoré
    @GetMapping("/vehicles")
    public List<String> getAllVehicles() {
        return vehicleService.getAllVehicles().stream()
                .map(vehicle -> {
                    VehicleDisplay display = new BasicVehicleDisplay(vehicle);
                    display = new IconDecorator(display);
                    display = new PriceTagDecorator(display);
                    return display.display();
                })
                .collect(Collectors.toList());
    }

    // ✅ Ajouter une voiture
    @PostMapping("/vehicles/car")
    public Vehicle addCar(@RequestBody Car car) {
        return vehicleService.saveVehicle(car);
    }

    // ✅ Ajouter un scooter
    @PostMapping("/vehicles/scooter")
    public Vehicle addScooter(@RequestBody Scooter scooter) {
        return vehicleService.saveVehicle(scooter);
    }

    // ✅ Supprimer un véhicule
    @DeleteMapping("/vehicles/{id}")
    public String deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicleById(id);
        return "Vehicle avec l'id " + id + " a été supprimé avec succès";
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
    public ResponseEntity<Vehicle> updateVehicleDetails(
        @PathVariable Long id,
        @RequestParam int stockQuantity,
        @RequestParam int yearOfManufacture,
        @RequestParam String fuelType,
        @RequestParam int mileage) {
    Vehicle updatedVehicle = vehicleService.updateVehicleDetails(id, stockQuantity, yearOfManufacture, fuelType, mileage);
    return updatedVehicle != null ? ResponseEntity.ok(updatedVehicle) : ResponseEntity.notFound().build();
}

}
