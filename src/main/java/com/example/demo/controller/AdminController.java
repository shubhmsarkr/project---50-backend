package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final ProjectRepository projectRepository;

    public AdminController(UserRepository userRepository, LessonRepository lessonRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("lessons", lessonRepository.count());
        stats.put("projects", projectRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("points", u.getPoints());
            map.put("verified", u.getVerified());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> details = new HashMap<>();
        details.put("modules", user.getCompletedModules().stream().map(m -> {
            Map<String, Object> mmap = new HashMap<>();
            mmap.put("id", m.getId());
            mmap.put("title", m.getTitle());
            return mmap;
        }).collect(Collectors.toList()));
        details.put("projects", user.getCompletedProjects().stream().map(p -> {
            Map<String, Object> pmap = new HashMap<>();
            pmap.put("id", p.getId());
            pmap.put("title", p.getTitle());
            return pmap;
        }).collect(Collectors.toList()));
        return ResponseEntity.ok(details);
    }

    @PostMapping("/users/{id}/verify")
    public ResponseEntity<Void> verifyUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
