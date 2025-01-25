package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.catalog.observer.EmailNotifier;
import com.tpinf4067.sale_vehicle.patterns.catalog.observer.Observer;
import com.tpinf4067.sale_vehicle.patterns.catalog.observer.VehicleNotifier;
import com.tpinf4067.sale_vehicle.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    // 🔥 Injection du notificateur
    private final VehicleRepository vehicleRepository;
    private final VehicleNotifier vehicleNotifier; // Ajout du notificateur

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleNotifier = new VehicleNotifier(); // Initialisation
    }

    // ✅ Ajout de la méthode de récupération
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // ✅ Ajout de la méthode de récupération par ID
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }
    
    // ✅ Ajout de la méthode d'ajout
    public Vehicle saveVehicle(Vehicle vehicle) {
        if (vehicle.getPrice() <= 0) {
            throw new IllegalArgumentException("Le prix doit être positif.");
        }
        if (vehicle.getName() == null || vehicle.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du véhicule ne peut pas être vide.");
        }
    
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        vehicleNotifier.notifyObservers("🚗 Nouveau véhicule ajouté : " + vehicle.getName() + " au prix de " + vehicle.getPrice());
        return savedVehicle;
    }

    // ✅ Ajout de la méthode de suppression
    public void deleteVehicleById(Long id) {
        vehicleRepository.deleteById(id);
    }

    // Permet d'ajouter un observateur
    public void addObserver(EmailNotifier observer) {
        vehicleNotifier.addObserver(observer);
    }

    // Permet de retirer un observateur
    public void removeObserver(Observer observer) {
        vehicleNotifier.removeObserver(observer);
    }
    
    // ✅ Ajout de la méthode de recherche
    public List<Vehicle> searchVehicles(String name, Double priceMin, Double priceMax) {
        if (name != null && priceMin != null && priceMax != null) {
            return vehicleRepository.findByNameContainingIgnoreCase(name)
                    .stream()
                    .filter(v -> v.getPrice() >= priceMin && v.getPrice() <= priceMax)
                    .toList();
        } else if (name != null) {
            return vehicleRepository.findByNameContainingIgnoreCase(name);
        } else if (priceMin != null && priceMax != null) {
            return vehicleRepository.findByPriceBetween(priceMin, priceMax);
        } else {
            return vehicleRepository.findAll();
        }
    }

    // ✅ Ajout de la méthode de mise à jour de l'animation
    public Vehicle updateVehicleAnimation(Long vehicleId, String animationUrl) {
        return vehicleRepository.findById(vehicleId).map(vehicle -> {
            vehicle.setAnimationUrl(animationUrl);
            return vehicleRepository.save(vehicle);
        }).orElse(null);
    }
    
    // ✅ Ajout de la méthode de récupération de la vue du catalogue
    public List<List<Vehicle>> getCatalogView(int columns) {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<List<Vehicle>> formattedCatalog = new ArrayList<>();

        for (int i = 0; i < allVehicles.size(); i += columns) {
            formattedCatalog.add(allVehicles.subList(i, Math.min(i + columns, allVehicles.size())));
        }
            return formattedCatalog;
    }

    // ✅ Ajout de la méthode de recherche de véhicules par mots-clés et opérateur logique
    public List<Vehicle> searchVehicles(String keywords, String operator) {
        List<Vehicle> allVehicles = vehicleRepository.findAll();

        if (keywords == null || keywords.trim().isEmpty()) {
        return allVehicles; // Si aucun mot-clé n'est fourni, renvoyer tous les véhicules
        }

        String[] keywordArray = keywords.toLowerCase().split("\\s+"); // Séparer les mots par espace

        return allVehicles.stream().filter(vehicle -> {
            String vehicleData = (vehicle.getName() + " " + vehicle.getPrice()).toLowerCase();

            boolean matches;
            if ("AND".equalsIgnoreCase(operator)) {
                matches = Arrays.stream(keywordArray).allMatch(vehicleData::contains); // Tous les mots doivent être présents
            } else {
                matches = Arrays.stream(keywordArray).anyMatch(vehicleData::contains); // Au moins un mot doit être présent
            }

            return matches;
        }).collect(Collectors.toList());
    }


    // ✅ Recherche avancée (nom, prix min, prix max) et prise en charge des mots-clés
    public List<Vehicle> searchVehicles(String name, Double priceMin, Double priceMax, String keywords, String operator) {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // 🔍 Filtrage par nom et prix
        vehicles = vehicles.stream()
                .filter(vehicle -> (name == null || vehicle.getName().toLowerCase().contains(name.toLowerCase()))
                        && (priceMin == null || vehicle.getPrice() >= priceMin)
                        && (priceMax == null || vehicle.getPrice() <= priceMax))
                .collect(Collectors.toList());

        // 🔍 Recherche avancée par mots-clés et opérateur logique (AND, OR)
        if (keywords != null && !keywords.trim().isEmpty()) {
            List<String> keywordList = Arrays.asList(keywords.toLowerCase().split("\\s+"));

            vehicles = vehicles.stream()
                    .filter(vehicle -> {
                        List<String> vehicleWords = Arrays.asList(vehicle.getName().toLowerCase().split("\\s+"));
                        if ("AND".equalsIgnoreCase(operator)) {
                            return keywordList.stream().allMatch(vehicleWords::contains);
                        } else { // OR par défaut
                            return keywordList.stream().anyMatch(vehicleWords::contains);
                        }
                    })
                    .collect(Collectors.toList());
        }

        return vehicles;
    }

    // ✅ Appliquer une réduction de 20% sur les véhicules en stock depuis plus de 6 mois
    public List<Vehicle> applyDiscountForOldStock() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6); // Définir la date d'il y a 6 mois
        Date sixMonthsAgo = cal.getTime();

        List<Vehicle> oldStockVehicles = vehicleRepository.findAll().stream()
                .filter(vehicle -> vehicle.getDateAjout().before(sixMonthsAgo))
                .toList();

        for (Vehicle vehicle : oldStockVehicles) {
            double newPrice = vehicle.getPrice() * 0.8; // Appliquer une réduction de 20%
            vehicle.setPrice(newPrice);
            vehicleRepository.save(vehicle);
        }

        return oldStockVehicles;
    }

    // ✅ Mettre à jour les détails d'un véhicule
    public Vehicle updateVehicleDetails(Long vehicleId, int stockQuantity, int yearOfManufacture, String fuelType, int mileage) {
        return vehicleRepository.findById(vehicleId).map(vehicle -> {
            vehicle.setStockQuantity(stockQuantity);
            vehicle.setYearOfManufacture(yearOfManufacture);
            vehicle.setFuelType(fuelType);
            vehicle.setMileage(mileage);
            return vehicleRepository.save(vehicle);
        }).orElse(null);
    }
    

}
