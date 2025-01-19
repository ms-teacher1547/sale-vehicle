package com.tpinf4067.sale_vehicle.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.tpinf4067.sale_vehicle.domain.Car;
import com.tpinf4067.sale_vehicle.domain.Scooter;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.service.catalog.VehicleService;
import com.tpinf4067.sale_vehicle.service.catalog.decorator.BasicVehicleDisplay;
import com.tpinf4067.sale_vehicle.service.catalog.decorator.IconDecorator;
import com.tpinf4067.sale_vehicle.service.catalog.decorator.PriceTagDecorator;
import com.tpinf4067.sale_vehicle.service.catalog.decorator.VehicleDisplay;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final VehicleService vehicleService;

    public CatalogController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/vehicles")
    public List<String> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return vehicles.stream()
                .map(vehicle -> {
                    VehicleDisplay display = new BasicVehicleDisplay(vehicle);
                    display = new IconDecorator(display);
                    display = new PriceTagDecorator(display);
                    return display.display();
                })
                .collect(Collectors.toList());
    }

    // Endpoint spécifique pour ajouter une voiture
    @PostMapping("/vehicles/car")
    public Vehicle addCar(@RequestBody Car car) {
        return vehicleService.saveVehicle(car);
    }

    // Endpoint spécifique pour ajouter un scooter
    @PostMapping("/vehicles/scooter")
    public Vehicle addScooter(@RequestBody Scooter scooter) {
        return vehicleService.saveVehicle(scooter);
    }

    // Endpoint spécifique pour supprimer un véhicule
    @DeleteMapping("/vehicles/{id}")
    public String deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicleById(id);
        return "Vehicle avec l'id " + id + " a été supprimé avec succès";
    }

    // Endpoint pour rechercher des véhicules
    @GetMapping("vehicles/search")
    public List<Vehicle> searchVehicles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax) {
        return vehicleService.searchVehicles(name, priceMin, priceMax);
    }   
}
