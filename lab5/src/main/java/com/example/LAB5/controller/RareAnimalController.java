package com.example.LAB5.controller;

import com.example.LAB5.model.RareAnimal;
import com.example.LAB5.repository.RareAnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
@CrossOrigin(origins = "*")
public class RareAnimalController {

    @Autowired
    private RareAnimalRepository repository;

    @GetMapping
    public List<RareAnimal> getAllAnimals() {
        return repository.findAllByOrderByIdAsc();
    }

    @PostMapping
    public RareAnimal addAnimal(@RequestBody RareAnimal animal) {
        return repository.save(animal);
    }

    @PutMapping("/{id}")
    public RareAnimal updateAnimal(@PathVariable Long id, @RequestBody RareAnimal animalDetails) {
        RareAnimal animal = repository.findById(id).orElseThrow();
        animal.setName(animalDetails.getName());
        animal.setScientificName(animalDetails.getScientificName());
        animal.setDescription(animalDetails.getDescription());
        animal.setPopulationLeft(animalDetails.getPopulationLeft());
        animal.setImageUrl(animalDetails.getImageUrl());
        return repository.save(animal);
    }

    @DeleteMapping("/{id}")
    public void deleteAnimal(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
