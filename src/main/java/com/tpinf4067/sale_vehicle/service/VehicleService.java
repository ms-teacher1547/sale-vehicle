package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.domain.Car;
import com.tpinf4067.sale_vehicle.domain.Scooter;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.catalog.observer.EmailNotifier;
import com.tpinf4067.sale_vehicle.patterns.catalog.observer.Observer;
import com.tpinf4067.sale_vehicle.patterns.catalog.observer.VehicleNotifier;
import com.tpinf4067.sale_vehicle.repository.VehicleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    // 🔥 Injection du notificateur
    private final VehicleRepository vehicleRepository;
    private final VehicleNotifier vehicleNotifier; // Ajout du notificateur
    private final String IMAGE_DIR = "uploads/";

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
    @Transactional
    public void deleteVehicleById(Long id) {
        // First, delete all references in order_vehicles
        vehicleRepository.deleteOrderVehiclesForVehicle(id);
        
        // Then, delete all references in cart_items
        vehicleRepository.deleteCartItemsForVehicle(id);
        
        // Finally, delete the vehicle itself
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
        cal.add(Calendar.SECOND, -10); // Définir la date d'il y a 10 secondes
        Date sixMonthsAgo = cal.getTime();

        List<Vehicle> oldStockVehicles = vehicleRepository.findAll().stream()
                .filter(vehicle -> vehicle.getDateAjout().before(sixMonthsAgo))
                .toList();

        for (Vehicle vehicle : oldStockVehicles) {
            double newPrice = vehicle.getPrice() * 0.2; // Appliquer une réduction de 20%
            vehicle.setPrice(newPrice);
            vehicleRepository.save(vehicle);
        }

        return oldStockVehicles;
    }

    // ✅ Mise à jour d'un véhicule avec gestion des références
    @Transactional
    public Vehicle updateVehicle(Vehicle updatedVehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(updatedVehicle.getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        // Mise à jour des propriétés de base
        existingVehicle.setName(updatedVehicle.getName());
        existingVehicle.setPrice(updatedVehicle.getPrice());
        existingVehicle.setStockQuantity(updatedVehicle.getStockQuantity());
        existingVehicle.setYearOfManufacture(updatedVehicle.getYearOfManufacture());
        existingVehicle.setFuelType(updatedVehicle.getFuelType());
        existingVehicle.setMileage(updatedVehicle.getMileage());

        // Mise à jour des propriétés spécifiques selon le type de véhicule
        if (existingVehicle instanceof Car && updatedVehicle instanceof Car) {
            ((Car) existingVehicle).setNumberOfDoors(((Car) updatedVehicle).getNumberOfDoors());
        } else if (existingVehicle instanceof Scooter && updatedVehicle instanceof Scooter) {
            ((Scooter) existingVehicle).setHasStorageBox(((Scooter) updatedVehicle).isHasStorageBox());
        }

        // Mise à jour des URLs si présents
        if (updatedVehicle.getImageUrl() != null) {
            existingVehicle.setImageUrl(updatedVehicle.getImageUrl());
        }
        if (updatedVehicle.getAnimationUrl() != null) {
            existingVehicle.setAnimationUrl(updatedVehicle.getAnimationUrl());
        }

        try {
            // Sauvegarder et notifier
            Vehicle savedVehicle = vehicleRepository.save(existingVehicle);
            vehicleNotifier.notifyObservers("🔄 Véhicule mis à jour : " + savedVehicle.getName());
            return savedVehicle;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du véhicule : " + e.getMessage());
        }
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
    
    public List<Vehicle> getAllAvailableVehicles() {
        List<Vehicle> availableVehicles = vehicleRepository.findAvailableVehicles();
        
        logger.info("Nombre de véhicules disponibles : {}", availableVehicles.size());
        
        return availableVehicles;
    }    

    // ✅ Méthode pour uploader une image
      public Vehicle uploadVehicleImage(Long vehicleId, MultipartFile file) {
        return vehicleRepository.findById(vehicleId).map(vehicle -> {
            try {
                // 🔥 Création du dossier s'il n'existe pas
                Path uploadPath = Paths.get(IMAGE_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 🔥 Sauvegarde du fichier
                String fileName = "vehicle_" + vehicleId + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 🔥 Mise à jour de l'URL dans la BD
                vehicle.setAnimationUrl("/uploads/vehicles" + fileName);
                vehicle.setImageUrl("/uploads/vehicles" + fileName);
                return vehicleRepository.save(vehicle);

            } catch (Exception e) {
                throw new RuntimeException("Impossible d'enregistrer l'image : " + e.getMessage());
            }
        }).orElseThrow(() -> new RuntimeException("Véhicule non trouvé !"));
    }

    public String saveVehicleImage(Long vehicleId, MultipartFile file) throws IOException {
        String uploadDir = "uploads/vehicles/";
        String fileName = "vehicle_" + vehicleId + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
    
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    
        // ✅ Mettre à jour l'URL de l'image dans la BD
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("Véhicule non trouvé !"));
        vehicle.setImageUrl("/uploads/vehicles/" + fileName);
        vehicleRepository.save(vehicle);
    
        return "/uploads/vehicles/" + fileName; // Retourne l'URL de l'image
    }
    
    

}