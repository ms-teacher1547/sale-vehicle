package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.domain.IncompatibleOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncompatibleRepository extends JpaRepository<IncompatibleOption, Long> {
}
