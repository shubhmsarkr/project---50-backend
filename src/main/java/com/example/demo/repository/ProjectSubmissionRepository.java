package com.example.demo.repository;

import com.example.demo.entity.ProjectSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, Long> {
    List<ProjectSubmission> findByStatus(String status);
    List<ProjectSubmission> findByUserId(Long userId);
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
}
