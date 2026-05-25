package com.example.LAB8.service;

import com.example.LAB8.model.RareAnimal;
import com.example.LAB8.repository.RareAnimalRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RareAnimalService {

    private final RareAnimalRepository rareAnimalRepository;

    public RareAnimalService(RareAnimalRepository rareAnimalRepository) {
        this.rareAnimalRepository = rareAnimalRepository;
    }

    public List<RareAnimal> findAll() {
        return rareAnimalRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public RareAnimal findById(Long id) {
        return rareAnimalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rare animal with id " + id + " was not found"));
    }

    public RareAnimal create(RareAnimal rareAnimal) {
        rareAnimal.setId(null);
        return rareAnimalRepository.save(rareAnimal);
    }

    public RareAnimal update(Long id, RareAnimal rareAnimal) {
        RareAnimal stored = findById(id);
        stored.setName(rareAnimal.getName());
        stored.setScientificName(rareAnimal.getScientificName());
        stored.setDescription(rareAnimal.getDescription());
        stored.setPopulationLeft(rareAnimal.getPopulationLeft());
        stored.setImageUrl(rareAnimal.getImageUrl());
        return rareAnimalRepository.save(stored);
    }

    public void delete(Long id) {
        RareAnimal stored = findById(id);
        rareAnimalRepository.delete(stored);
    }
}
