package com.example.LAB8.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/items")
public class ItemController {

    @GetMapping
    public String listPage(Model model) {
        model.addAttribute("apiBase", "/api/animals");
        return "index";
    }

    @GetMapping("/new")
    public String createPage(Model model) {
        model.addAttribute("pageTitle", "Create rare animal card");
        model.addAttribute("formMode", "create");
        model.addAttribute("apiBase", "/api/animals");
        return "animal-form";
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Edit rare animal card");
        model.addAttribute("formMode", "edit");
        model.addAttribute("itemId", id);
        model.addAttribute("apiBase", "/api/animals");
        return "animal-form";
    }
}
