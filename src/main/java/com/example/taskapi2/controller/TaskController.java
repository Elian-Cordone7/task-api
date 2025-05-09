package com.example.taskapi2.controller;

import com.example.taskapi2.model.Task;
import com.example.taskapi2.util.JwtTokenUtil;
import com.example.taskapi2.service.ResponseService;
import com.example.taskapi2.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ResponseService responseService;

    public TaskController(TaskService taskService, JwtTokenUtil jwtTokenUtil, ResponseService responseService) {
        this.taskService = taskService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.responseService = responseService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("Authorization") String token) {

        String username = getUsernameFromToken(token);

        log.info("USUARIO ENCONTRADO POR TOKEN: "+username);

        task.setUserId(username);
        task.setCreatedAt(System.currentTimeMillis());

        Task createdTask = taskService.createTask(task);
        return responseService.createdResponse(null,ResponseEntity.status(HttpStatus.CREATED).body(createdTask));
    }

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);

        List<Task> tasks = taskService.getTasksByUserId(username);
        return responseService.successResponse(null,ResponseEntity.ok(tasks));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String taskId, @RequestBody Task updatedTask, @RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);

        Task existingTask = taskService.getTasksByUserId(username).stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);

        if (existingTask == null) {
            return responseService.errorResponse("El id ingresado no existe.");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDone(updatedTask.isDone());

        Task savedTask = taskService.updateTask(existingTask);
        return responseService.successResponse(null,ResponseEntity.ok(savedTask));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable String taskId, @RequestHeader("Authorization") String token) {
        String username = getUsernameFromToken(token);

        Task existingTask = taskService.getTasksByUserId(username).stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);

        if (existingTask == null) {
            return responseService.errorResponse("El id ingresado no existe.");
        }

        taskService.deleteTask(taskId);
        return responseService.successResponse("Tarea eliminada correctamente.");
    }

    private String getUsernameFromToken(String token) {
        try {
            return jwtTokenUtil.getUsernameFromToken(token.substring(7));
        } catch (Exception e) {
            return null;
        }
    }
}
