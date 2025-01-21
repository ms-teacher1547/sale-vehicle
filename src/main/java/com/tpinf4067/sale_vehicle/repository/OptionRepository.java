package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
}
