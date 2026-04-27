package com.example.demo.controller;

import com.example.demo.dto.ModuleDTO;
import com.example.demo.service.ModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<ModuleDTO>> getModulesByLessonId(@PathVariable Long lessonId) {
        return ResponseEntity.ok(moduleService.getModulesByLessonId(lessonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @PostMapping
    public ResponseEntity<ModuleDTO> createModule(@RequestBody ModuleDTO moduleDTO) {
        return new ResponseEntity<>(moduleService.createModule(moduleDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable Long id, @RequestBody ModuleDTO moduleDTO) {
        return ResponseEntity.ok(moduleService.updateModule(id, moduleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeModule(@PathVariable Long id, @RequestBody java.util.Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long pointsEarned = payload.get("pointsEarned");
        if (pointsEarned == null) pointsEarned = 50L; // Fallback
        moduleService.completeModule(id, userId, pointsEarned);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin-create")
    public ResponseEntity<?> adminCreateModule(@RequestBody java.util.Map<String, String> payload) {
        Long lessonId = Long.parseLong(payload.get("lessonId"));
        String title = payload.get("title");
        String quizJson = payload.get("quizJson");
        moduleService.adminCreateModuleWithQuiz(lessonId, title, quizJson);
        return ResponseEntity.ok().build();
    }
}
