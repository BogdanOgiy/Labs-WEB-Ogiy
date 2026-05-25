package com.example.LAB6.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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
    private Long id;

    private String name;
    private String scientificName;
    private String description;
    private Integer populationLeft;
    @Lob
    private String imageUrl;
}
