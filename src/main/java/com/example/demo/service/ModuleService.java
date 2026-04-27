package com.example.demo.service;

import com.example.demo.dto.ModuleDTO;
import com.example.demo.entity.Lesson;
import com.example.demo.entity.Module;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final com.example.demo.repository.QuizRepository quizRepository;
    private final com.example.demo.repository.PageRepository pageRepository;

    public ModuleService(ModuleRepository moduleRepository, LessonRepository lessonRepository, UserRepository userRepository, ModelMapper modelMapper, com.example.demo.repository.QuizRepository quizRepository, com.example.demo.repository.PageRepository pageRepository) {
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.quizRepository = quizRepository;
        this.pageRepository = pageRepository;
    }

    public List<ModuleDTO> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ModuleDTO> getModulesByLessonId(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return moduleRepository.findByLessonIdOrderByOrderIndexAsc(lessonId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ModuleDTO getModuleById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        return convertToDTO(module);
    }

    public ModuleDTO createModule(ModuleDTO moduleDTO) {
        Module module = modelMapper.map(moduleDTO, Module.class);
        
        if (moduleDTO.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(moduleDTO.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            module.setLesson(lesson);
        }
        
        Module savedModule = moduleRepository.save(module);
        return convertToDTO(savedModule);
    }

    public ModuleDTO updateModule(Long id, ModuleDTO moduleDTO) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        
        module.setTitle(moduleDTO.getTitle());
        module.setOrderIndex(moduleDTO.getOrderIndex());
        
        if (moduleDTO.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(moduleDTO.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            module.setLesson(lesson);
        }
        
        Module updatedModule = moduleRepository.save(module);
        return convertToDTO(updatedModule);
    }

    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    public void completeModule(Long moduleId, Long userId, Long pointsEarned) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        com.example.demo.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getCompletedModules().contains(module)) {
            user.getCompletedModules().add(module);
            user.setPoints(user.getPoints() + pointsEarned.intValue());
            userRepository.save(user);
        }
    }

    private ModuleDTO convertToDTO(Module module) {
        ModuleDTO dto = modelMapper.map(module, ModuleDTO.class);
        if (module.getLesson() != null) {
            dto.setLessonId(module.getLesson().getId());
        }
        return dto;
    }

    public void adminCreateModuleWithQuiz(Long lessonId, String title, String quizJson) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        Module module = new Module();
        module.setLesson(lesson);
        module.setTitle(title);
        module.setOrderIndex((int) moduleRepository.count() + 1);
        module = moduleRepository.save(module);

        com.example.demo.entity.Page p1 = new com.example.demo.entity.Page();
        p1.setModule(module);
        p1.setPageNumber(1);
        p1.setContent("Welcome to " + title + ". This is an auto-generated admin module.");
        pageRepository.save(p1);

        com.example.demo.entity.Quiz q1 = new com.example.demo.entity.Quiz();
        q1.setModule(module);
        q1.setQuestionsJson(quizJson != null && !quizJson.trim().isEmpty() ? quizJson : "[]");
        quizRepository.save(q1);
    }
}
