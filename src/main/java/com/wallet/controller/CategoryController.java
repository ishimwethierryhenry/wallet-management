package com.wallet.controller;


import org.springframework.ui.Model; // Correct import for Spring MVC Model
import com.wallet.model.Category;
import com.wallet.model.SubCategory;
import com.wallet.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newCategory", new Category());
        return "categories/list";
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        categoryService.createCategory(category);
        return "redirect:/categories";
    }

    @PostMapping("/{categoryId}/subcategories")
    public String addSubCategory(@PathVariable Long categoryId, @ModelAttribute SubCategory subCategory) {
        categoryService.addSubCategory(categoryId, subCategory);
        return "redirect:/categories";
    }
}