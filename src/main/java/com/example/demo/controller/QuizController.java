package com.example.demo.controller;

import com.example.demo.entity.Quiz;
import com.example.demo.repository.QuizRepository;
import com.example.demo.repository.ModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules/{moduleId}/quiz")
public class QuizController {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;

    public QuizController(QuizRepository quizRepository, ModuleRepository moduleRepository) {
        this.quizRepository = quizRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public ResponseEntity<Quiz> getQuizByModule(@PathVariable Long moduleId) {
        return moduleRepository.findById(moduleId)
                .flatMap(module -> quizRepository.findByModuleId(moduleId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
