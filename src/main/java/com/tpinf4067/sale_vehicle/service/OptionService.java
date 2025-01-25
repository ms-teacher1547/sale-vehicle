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
        // Vérifier si la catégorie fournie est valide
        if (option.getCategory() == null) {
            throw new IllegalArgumentException("⚠️ La catégorie de l'option ne peut pas être vide.");
        }
        return optionRepository.save(option);
    }
    

    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    // 📌 Ajouter une incompatibilité entre deux options
    public void addIncompatibility(Long optionId1, Long optionId2) {
        Option option1 = optionRepository.findById(optionId1).orElseThrow(() -> new RuntimeException("Option non trouvée"));
        Option option2 = optionRepository.findById(optionId2).orElseThrow(() -> new RuntimeException("Option non trouvée"));
    
        if (incompatibleRepository.existsByOption1AndOption2(option1, option2) ||
            incompatibleRepository.existsByOption1AndOption2(option2, option1)) {
            throw new RuntimeException("🚫 Cette incompatibilité existe déjà.");
        }
    
        IncompatibleOption incompatibleOption = new IncompatibleOption(option1, option2);
        incompatibleRepository.save(incompatibleOption);
    }

    // 🔥 Supprimer une option après avoir supprimé ses références
    public void deleteOption(Long id) {
        if (!optionRepository.existsById(id)) {
            throw new RuntimeException("❌ L'option avec l'ID " + id + " n'existe pas.");
        }

        // Supprimer toutes les relations où cette option est utilisée
        incompatibleRepository.deleteByOption1Id(id);
        incompatibleRepository.deleteByOption2Id(id);

        // Maintenant, supprimer l'option elle-même
        optionRepository.deleteById(id);
    }

    // 🔥 Récupérer toutes les incompatibilités
    public List<IncompatibleOption> getAllIncompatibilities() {
        return incompatibleRepository.findAll();
    }

    



}