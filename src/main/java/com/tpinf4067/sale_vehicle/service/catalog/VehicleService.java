package com.tpinf4067.sale_vehicle.service.catalog;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.repository.VehicleRepository;
import com.tpinf4067.sale_vehicle.service.catalog.observer.EmailNotifier;
import com.tpinf4067.sale_vehicle.service.catalog.observer.Observer;
import com.tpinf4067.sale_vehicle.service.catalog.observer.VehicleNotifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleNotifier vehicleNotifier; // Ajout du notificateur

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleNotifier = new VehicleNotifier(); // Initialisation
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // 🔥 Notifier les observateurs
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

    public void removeObserver(Observer observer) {
        vehicleNotifier.removeObserver(observer);
    }
}
