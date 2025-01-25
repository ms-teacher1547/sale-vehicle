package com.tpinf4067.sale_vehicle.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpinf4067.sale_vehicle.domain.IncompatibleOption;
import com.tpinf4067.sale_vehicle.domain.Option;
import com.tpinf4067.sale_vehicle.service.OptionService;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    // 🔹 Ajouter une option (ADMIN uniquement)
    @PostMapping("/")
    public ResponseEntity<?> createOption(@RequestBody Option option) {
        try {
            Option savedOption = optionService.save(option);
            return ResponseEntity.ok(savedOption);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("⚠️ Catégorie invalide. Utilisez l'une de ces valeurs : PERFORMANCE, SÉCURITÉ, CONFORT, MULTIMÉDIA.");
        }
    }
    

    // 🔹 Récupérer toutes les options (PUBLIC)
    @GetMapping("/")
    public List<Option> getAllOptions() {
        return optionService.getAllOptions();
    }

    // 🔥 Définir l'incompatibilité entre deux options
    @PostMapping("/incompatible")
    public ResponseEntity<String> addIncompatibleOptions(@RequestBody IncompatibilityRequest request) {
        optionService.addIncompatibility(request.getOptionId1(), request.getOptionId2());
        return ResponseEntity.ok("🚫 Incompatibilité ajoutée avec succès !");
    }

    // 🔥 Supprimer une option (ADMIN uniquement)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
        return ResponseEntity.ok("🚫 Option supprimée avec succès !");
    }

    // 🔥 Récupérer toutes les incompatibilités existantes
    @GetMapping("/incompatible")
    public ResponseEntity<List<IncompatibleOption>> getAllIncompatibilities() {
        return ResponseEntity.ok(optionService.getAllIncompatibilities());
    }



    // 📌 Classe interne pour gérer la requête
    static class IncompatibilityRequest {
        private Long optionId1;
        private Long optionId2;

        // Getters et Setters
        public Long getOptionId1() {
            return optionId1;
        }

        public void setOptionId1(Long optionId1) {
            this.optionId1 = optionId1;
        }

        public Long getOptionId2() {
            return optionId2;
        }

        public void setOptionId2(Long optionId2) {
            this.optionId2 = optionId2;
        }
    }

}
