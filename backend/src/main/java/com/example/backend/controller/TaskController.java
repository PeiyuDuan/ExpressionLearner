package com.example.backend.controller;

import com.example.backend.model.Task;
import com.example.backend.service.TaskService;
import com.example.backend.service.UserTaskStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserTaskStageService userTaskStageService;

    @Autowired
    public TaskController(TaskService taskService, UserTaskStageService userTaskStageService) {
        this.taskService = taskService;
        this.userTaskStageService = userTaskStageService;
    }

    // Get all tasks
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        Map<String, Object> response = taskService.getAllTasks();
        return ResponseEntity.ok(response);
    }

    // Get task details by task ID
    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskDetails(@PathVariable Integer taskId) {
        Map<String, Object> response = taskService.getTaskDetails(taskId);
        return response.get("success").equals(true)
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    // Create a new task
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Map<String, Object> taskPrompt) {
        Map<String, Object> response = taskService.createTask(taskPrompt);
        return response.get("success").equals(true)
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    // Get the stage of a task for a user
    @GetMapping("/{taskId}/stage")
    public ResponseEntity<?> getStage(@RequestParam Integer userId, @PathVariable Integer taskId) {
        try {
            Integer stage = userTaskStageService.getStage(userId, taskId);
            return ResponseEntity.ok(stage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching stage: " + e.getMessage());
        }
    }

    // Update the stage of a task for a user
    @PutMapping("/{taskId}/stage")
    public ResponseEntity<?> updateStage(
            @RequestParam Integer userId,
            @PathVariable Integer taskId,
            @RequestParam Integer stage) {
        try {
            userTaskStageService.updateStage(userId, taskId, stage);
            return ResponseEntity.ok("Stage updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating stage: " + e.getMessage());
        }
    }

    // Get all tasks for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTasks(@PathVariable Integer userId) {
        try {
            List<Task> tasks = userTaskStageService.getTasks(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching tasks: " + e.getMessage());
        }
    }

    // Get review tasks for a user
    @GetMapping("/user/{userId}/review")
    public ResponseEntity<?> getUserReviewTasks(@PathVariable Integer userId) {
        try {
            List<Task> reviewTasks = userTaskStageService.getUserReviewTasks(userId);
            return ResponseEntity.ok(reviewTasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching review tasks: " + e.getMessage());
        }
    }
}
