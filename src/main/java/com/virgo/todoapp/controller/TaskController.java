package com.virgo.todoapp.controller;

import com.virgo.todoapp.config.advisers.exception.AccessDeniedException;
import com.virgo.todoapp.config.advisers.exception.InvalidQueryParamException;
import com.virgo.todoapp.config.advisers.exception.NotFoundException;
import com.virgo.todoapp.entity.meta.Task;
import com.virgo.todoapp.entity.meta.User;
import com.virgo.todoapp.repo.TaskRepository;
import com.virgo.todoapp.service.impl.AuthenticationServiceImpl;
import com.virgo.todoapp.utils.dto.*;
import com.virgo.todoapp.service.TaskService;
import com.virgo.todoapp.utils.response.PaginationResponse;
import com.virgo.todoapp.utils.response.WebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api")
@RestControllerAdvice
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {
    private final TaskService taskService;
    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService, AuthenticationServiceImpl authenticationServiceImpl, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.authenticationServiceImpl = authenticationServiceImpl;
        this.taskRepository = taskRepository;
    }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody @Valid TaskRequestDTO req) {
        return taskService.create(req);
    }

    @GetMapping("/todos")
    public PaginationResponse<?> getAll(@PageableDefault Pageable pageable, @RequestParam(required = false, name = "name") String name) {
        if (name != null && name.trim().isEmpty() || Objects.equals(name, "INVALID_STATUS")) {
            throw new InvalidQueryParamException("Invalid query parameter: name cannot be empty");
        }
        Page<Task> taskPage = taskService.getAll(pageable, name);

        // Create a new PaginationResponse with adjusted currentPage and totalPages
        PaginationResponse<Task> response = new PaginationResponse<>(taskPage);
        response.setCurrentPage(response.getCurrentPage()); // Adjust back to the original page number
        response.setTotalPages(response.getTotalPages() -1); // Adjust the total pages

        return response;
    }

    @Operation(summary = "Get task by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil get Task", content = { @Content(schema = @Schema(implementation = WebResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("/todos/{id}")
    public Task getById(@PathVariable String id) {
        try {
            Integer taskId = Integer.parseInt(id);
            return taskService.getById(taskId);
        } catch (NumberFormatException e) {
            throw new NotFoundException("To-do item not found");
        }
    }

    @DeleteMapping("/todos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        try {
            Integer taskId = Integer.parseInt(id); // Validasi jika id adalah integer
            taskService.delete(taskId);
        } catch (NumberFormatException e) {
            throw new NotFoundException("To-do item not found");
        }
    }


    @PutMapping("/todos/{id}")
    public TaskDTO2 updateById(@PathVariable Integer id, @RequestBody @Valid TaskUpdateDTO req){
        return taskService.updateById(id, req);
    }


    @PatchMapping("/todos/{id}/status")
    public TaskDTO2 updateStatusById(@PathVariable Integer id, @RequestBody @Valid TaskUpdateDTO2 req){
        return taskService.updateById(id, req);
    }

    @GetMapping("/admin/todos")
    public PaginationResponse<?> getAllAdmin(@PageableDefault Pageable pageable, @RequestParam(required = false) String name) {
        return new PaginationResponse<>(taskService.getAllAdmin(pageable, name));
    }


    @GetMapping("/admin/todos/{id}")
    public TaskDTO getByIdAdmin(@PathVariable Integer id) {
        return taskService.getByIdAdmin(id);
    }
}
