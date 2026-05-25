package com.example.LAB5.repository;

import com.example.LAB5.model.RareAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RareAnimalRepository extends JpaRepository<RareAnimal, Long> {

    List<RareAnimal> findAllByOrderByIdAsc();
}
