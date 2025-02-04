package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.patterns.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOrderId(Long orderId);
}
