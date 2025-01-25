package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.domain.IncompatibleOption;
import com.tpinf4067.sale_vehicle.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncompatibleRepository extends JpaRepository<IncompatibleOption, Long> {
    void deleteByOption1Id(Long option1Id);
    void deleteByOption2Id(Long option2Id);
    @SuppressWarnings("null")
    List<IncompatibleOption> findAll();

    // Ajoutez cette méthode
    boolean existsByOption1AndOption2(Option option1, Option option2);
}
