package com.example.LAB9.controller;

import com.example.LAB9.model.RareAnimal;
import com.example.LAB9.service.RareAnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/animals")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Rare Animals", description = "CRUD operations for rare animals")
public class RareAnimalRestController {

    private final RareAnimalService rareAnimalService;

    public RareAnimalRestController(RareAnimalService rareAnimalService) {
        this.rareAnimalService = rareAnimalService;
    }

    @GetMapping
    @Operation(summary = "Get all rare animals")
    public List<RareAnimal> getAllAnimals() {
        log.info("Fetching rare animals list");
        return rareAnimalService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rare animal by id")
    public RareAnimal getAnimal(@PathVariable Long id) {
        log.info("Fetching rare animal with id={}", id);
        return rareAnimalService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a rare animal")
    public RareAnimal createAnimal(@Valid @RequestBody RareAnimal rareAnimal) {
        log.info("Creating rare animal: {}", rareAnimal.getName());
        return rareAnimalService.create(rareAnimal);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rare animal by id")
    public RareAnimal updateAnimal(@PathVariable Long id, @Valid @RequestBody RareAnimal rareAnimal) {
        log.info("Updating rare animal with id={}", id);
        return rareAnimalService.update(id, rareAnimal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete rare animal by id")
    public void deleteAnimal(@PathVariable Long id) {
        log.info("Deleting rare animal with id={}", id);
        rareAnimalService.delete(id);
    }
}
