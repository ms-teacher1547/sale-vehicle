package com.tpinf4067.sale_vehicle.Controller;

import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.repository.CustomerRepository;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/with-subsidiaries")
    public ResponseEntity<?> getCompaniesWithSubsidiaries() {
        List<Customer> companiesWithSubsidiaries = customerRepository.findAll().stream()
            .filter(customer -> 
                customer.getType() == CustomerType.COMPANY && 
                customer.getSubsidiaries() != null && 
                !customer.getSubsidiaries().isEmpty()
            )
            .collect(Collectors.toList());

        return ResponseEntity.ok(companiesWithSubsidiaries);
    }
}
