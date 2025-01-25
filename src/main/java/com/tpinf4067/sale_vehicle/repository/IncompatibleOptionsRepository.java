package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.domain.IncompatibleOptions;
import com.tpinf4067.sale_vehicle.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncompatibleOptionsRepository extends JpaRepository<IncompatibleOptions, Long> {
    boolean existsByOption1AndOption2(Option option1, Option option2);
    
}
