package com.insurebroker.controller;

import com.insurebroker.entity.Insurer;
import com.insurebroker.repository.InsurerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurers")
@RequiredArgsConstructor
public class InsurerController {

    private final InsurerRepository insurerRepository;

    @GetMapping
    public ResponseEntity<List<Insurer>> getAllInsurers() {
        return ResponseEntity.ok(insurerRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Insurer> createInsurer(@RequestBody Insurer insurer) {
        insurer.setCreatedAt(java.time.LocalDateTime.now());
        Insurer savedInsurer = insurerRepository.save(insurer);
        return ResponseEntity.ok(savedInsurer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Insurer> updateInsurer(@PathVariable Long id, @RequestBody Insurer details) {
        Insurer insurer = insurerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurer not found"));

        insurer.setName(details.getName());
        insurer.setCode(details.getCode());
        insurer.setContactEmail(details.getContactEmail());
        insurer.setContactPhone(details.getContactPhone());
        insurer.setAddress(details.getAddress());
        insurer.setActive(details.getActive());

        return ResponseEntity.ok(insurerRepository.save(insurer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurer(@PathVariable Long id) {
        insurerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}