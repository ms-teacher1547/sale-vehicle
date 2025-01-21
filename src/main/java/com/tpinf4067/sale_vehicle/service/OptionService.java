package com.tpinf4067.sale_vehicle.service;

import org.springframework.stereotype.Service;

import com.tpinf4067.sale_vehicle.domain.IncompatibleOption;
import com.tpinf4067.sale_vehicle.domain.Option;
import com.tpinf4067.sale_vehicle.repository.IncompatibleRepository;
import com.tpinf4067.sale_vehicle.repository.OptionRepository;

import java.util.List;

@Service
public class OptionService {
    private final OptionRepository optionRepository;
    private final IncompatibleRepository incompatibleRepository;


    public OptionService(OptionRepository optionRepository, IncompatibleRepository incompatibleRepository) {
        this.optionRepository = optionRepository;
        this.incompatibleRepository = incompatibleRepository;
    }

    public Option save(Option option) {
        return optionRepository.save(option);
    }

    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    // 📌 Ajouter une incompatibilité entre deux options
    public void addIncompatibility(Long optionId1, Long optionId2) {
        Option option1 = optionRepository.findById(optionId1).orElseThrow(() -> new RuntimeException("Option non trouvée"));
        Option option2 = optionRepository.findById(optionId2).orElseThrow(() -> new RuntimeException("Option non trouvée"));

        IncompatibleOption incompatibleOption = new IncompatibleOption(option1, option2);
        incompatibleRepository.save(incompatibleOption);
    }
}