package com.example.demo.repository;

import com.example.demo.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByLessonIdOrderByOrderIndexAsc(Long lessonId);
}
