package cz.osu.backend.service;

import cz.osu.backend.exception.ResourceNotFoundException;
import cz.osu.backend.model.db.Category;
import cz.osu.backend.model.db.Course;
import cz.osu.backend.model.dto.course.CategoryRequestDTO;
import cz.osu.backend.model.dto.course.CategoryResponseDTO;
import cz.osu.backend.model.dto.course.CourseResponseDTO;
import cz.osu.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        Category category = new Category();
        category.setName(request.getName());

        Category savedCategory = categoryRepository.save(category);

        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(savedCategory.getId());
        response.setName(savedCategory.getName());

        return response;
    }

    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.map(category -> {
            CategoryResponseDTO dto = new CategoryResponseDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            return dto;
        });
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kategorie s ID " + id + " nebyla nalezena"));
    }

    public CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO request) {
        Category category = getCategoryById(id);
        category.setName(request.getName());
        Category savedCategory = categoryRepository.save(category);

        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(savedCategory.getId());
        response.setName(savedCategory.getName());

        return response;
    }

    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kategorie s ID " + id + " nebyla nalezena");
        }
        categoryRepository.deleteById(id);
    }
}
