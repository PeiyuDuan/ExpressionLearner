package com.example.backend.service.impl;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import com.example.backend.service.TaskService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service("TaskService")
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Map<String, Object> createTask(Map<String, Object> taskPrompt) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 创建任务并插入数据库
            Timestamp creationDate = new Timestamp(System.currentTimeMillis());
            Task task = new Task(taskPrompt, creationDate);
            taskRepository.save(task);
            response.put("success", true);
            response.put("data", task);
        } catch (DataAccessException e) {
            response.put("success", false);
            response.put("message", "Task creation failed: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Map<String, Object> getAllTasks() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 从数据库获取所有任务
            List<Task> tasks = taskRepository.findAll();
            if (tasks.isEmpty()) {
                response.put("success", false);
                response.put("message", "No tasks found.");
                return response;
            }
            response.put("success", true);
            response.put("data", tasks);
        } catch (DataAccessException e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve tasks: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Map<String, Object> getTaskDetails(Integer taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Task not found with ID: " + taskId);
                return response;
            }

            response.put("success", true);
            response.put("task", taskOptional.get());
        } catch (DataAccessException e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve task details: " + e.getMessage());
        }
        return response;
    }
}
