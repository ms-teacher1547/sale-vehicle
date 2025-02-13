package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByNameContainingIgnoreCase(String name);
    List<Vehicle> findByPriceBetween(double min, double max);
    @Query("SELECT v FROM Vehicle v WHERE v.stockQuantity > 0")
    List<Vehicle> findAvailableVehicles();
    @Modifying
    @Query(value = "DELETE FROM cart_items WHERE vehicle_id = :vehicleId", nativeQuery = true)
    void deleteCartItemsForVehicle(@Param("vehicleId") Long vehicleId);
    @Modifying
    @Query(value = "DELETE FROM order_vehicles WHERE vehicle_id = :vehicleId", nativeQuery = true)
    void deleteOrderVehiclesForVehicle(@Param("vehicleId") Long vehicleId);

}
