package com.example.LAB9.repository;

import com.example.LAB9.model.RareAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "rare-animals", collectionResourceRel = "rareAnimals")
public interface RareAnimalRepository extends JpaRepository<RareAnimal, Long> {
}
