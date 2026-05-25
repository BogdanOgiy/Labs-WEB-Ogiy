package com.example.LAB6.controller;

import com.example.LAB6.model.RareAnimal;
import com.example.LAB6.repository.RareAnimalRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final RareAnimalRepository repository;

    public ItemController(RareAnimalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", repository.findAllByOrderByIdAsc());
        return "index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new RareAnimal());
        model.addAttribute("pageTitle", "Add Animal");
        model.addAttribute("formAction", "/items");
        return "animal-form";
    }

    @PostMapping
    public String createItem(@ModelAttribute("item") RareAnimal item) {
        item.setId(findNextAvailableId());
        repository.save(item);
        return "redirect:/items";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        RareAnimal item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Animal not found: " + id));
        model.addAttribute("item", item);
        model.addAttribute("pageTitle", "Edit Animal");
        model.addAttribute("formAction", "/items/" + id);
        return "animal-form";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable Long id, @ModelAttribute("item") RareAnimal itemDetails) {
        RareAnimal item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Animal not found: " + id));
        item.setName(itemDetails.getName());
        item.setScientificName(itemDetails.getScientificName());
        item.setDescription(itemDetails.getDescription());
        item.setPopulationLeft(itemDetails.getPopulationLeft());
        item.setImageUrl(itemDetails.getImageUrl());
        repository.save(item);
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Animal not found: " + id);
        }
        repository.deleteById(id);
        return "redirect:/items";
    }

    private Long findNextAvailableId() {
        List<Long> ids = repository.findAllIdsOrdered();
        long expectedId = 1L;
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            if (id > expectedId) {
                break;
            }
            if (id.equals(expectedId)) {
                expectedId++;
            }
        }
        return expectedId;
    }
}
