package com.example.demo.controller;

import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectSubmission;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.ProjectSubmissionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
public class ProjectSubmissionController {

    private final ProjectSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ProjectSubmissionController(ProjectSubmissionRepository submissionRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitProject(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long projectId = payload.get("projectId");

        if (submissionRepository.existsByUserIdAndProjectId(userId, projectId)) {
            return ResponseEntity.badRequest().body("Already submitted.");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Project project = projectRepository.findById(projectId).orElseThrow();

        ProjectSubmission submission = new ProjectSubmission();
        submission.setUser(user);
        submission.setProject(project);
        submission.setStatus("PENDING");
        submissionRepository.save(submission);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingSubmissions() {
        List<ProjectSubmission> pending = submissionRepository.findByStatus("PENDING");
        List<Map<String, Object>> result = new ArrayList<>();
        for (ProjectSubmission s : pending) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("username", s.getUser().getUsername());
            map.put("projectTitle", s.getProject().getTitle());
            map.put("userId", s.getUser().getId());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveSubmission(@PathVariable Long id) {
        ProjectSubmission submission = submissionRepository.findById(id).orElseThrow();
        submission.setStatus("APPROVED");
        submissionRepository.save(submission);

        // Add 100 points
        User user = submission.getUser();
        user.setPoints(user.getPoints() + 100);
        
        // Also add project to user's completed list
        if (!user.getCompletedProjects().contains(submission.getProject())) {
            user.getCompletedProjects().add(submission.getProject());
        }
        
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
