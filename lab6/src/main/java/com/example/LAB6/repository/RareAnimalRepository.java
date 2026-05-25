package com.example.LAB6.repository;

import com.example.LAB6.model.RareAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RareAnimalRepository extends JpaRepository<RareAnimal, Long> {

    List<RareAnimal> findAllByOrderByIdAsc();

    @Query("select r.id from RareAnimal r order by r.id asc")
    List<Long> findAllIdsOrdered();
}
