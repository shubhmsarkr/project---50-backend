package com.example.demo.controller;

import com.example.demo.entity.Page;
import com.example.demo.repository.PageRepository;
import com.example.demo.repository.ModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/pages")
public class PageController {

    private final PageRepository pageRepository;
    private final ModuleRepository moduleRepository;

    public PageController(PageRepository pageRepository, ModuleRepository moduleRepository) {
        this.pageRepository = pageRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public ResponseEntity<List<Page>> getPagesByModule(@PathVariable Long moduleId) {
        return moduleRepository.findById(moduleId)
                .map(module -> ResponseEntity.ok(pageRepository.findByModuleIdOrderByPageNumberAsc(moduleId)))
                .orElse(ResponseEntity.notFound().build());
    }
}
