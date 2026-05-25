package com.example.LAB9.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rare_animals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RareAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must be shorter than 120 characters")
    private String name;

    @Size(max = 180, message = "Scientific name must be shorter than 180 characters")
    private String scientificName;

    @Size(max = 2000, message = "Description must be shorter than 2000 characters")
    private String description;

    @Min(value = 0, message = "Population left cannot be negative")
    private Integer populationLeft;

    @Lob
    private String imageUrl;
}
