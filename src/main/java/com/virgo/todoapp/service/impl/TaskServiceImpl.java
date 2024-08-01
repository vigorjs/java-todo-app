package com.virgo.todoapp.service.impl;

import com.virgo.todoapp.config.advisers.exception.AccessDeniedException;
import com.virgo.todoapp.config.advisers.exception.AuthenticationException;
import com.virgo.todoapp.config.advisers.exception.ValidateException;
import com.virgo.todoapp.service.AuthenticationService;
import com.virgo.todoapp.utils.dto.*;
import com.virgo.todoapp.entity.meta.Task;
import com.virgo.todoapp.entity.meta.User;
import com.virgo.todoapp.entity.enums.TaskStatus;
import com.virgo.todoapp.config.advisers.exception.NotFoundException;
import com.virgo.todoapp.repo.TaskRepository;
import com.virgo.todoapp.service.TaskService;
import com.virgo.todoapp.utils.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import static org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final AuthenticationService authService;
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Override
    public Task create(TaskRequestDTO req) {
        User user = authService.getUserAuthenticated();
        Date dte = parseDueDate(req.getDueDate());
        validateDueDate(dte);
        Task task = Task.builder()
                .title(req.getTitle())
                .createdAt(Date.from(Instant.now()))
                .description(req.getDescription())
                .dueDate(dte)
//                .deadline(req.getDeadline())
                .status(TaskStatus.PENDING)
                .user(user)
                .build();

        taskRepository.save(task);
        return task;
    }

    @Override
    public Page<Task> getAll(Pageable pageable, String title) {

        User currentUser = authService.getUserAuthenticated();
        Specification<Task> spec = TaskSpecification.getSpecification(currentUser, title);
        Page<Task> originalPage = taskRepository.findAll(spec, pageable);

        // Adjust the current page number by adding 1
        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize(), pageable.getSort());

        // Calculate the new total elements to achieve one less total page if necessary
        int originalTotalPages = originalPage.getTotalPages();
        long newTotalElements = originalTotalPages > 1 ? originalPage.getTotalElements() - originalPage.getSize() : originalPage.getTotalElements();

        // Create a new PageImpl object with the adjusted Pageable and total elements
        Page<Task> adjustedPage = new PageImpl<>(originalPage.getContent(), adjustedPageable, newTotalElements+1);

        return adjustedPage;
    }


    @Override
    public Task getById(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow( () -> new NotFoundException("Task Not Found"));

        if (!Objects.equals(task.getUser(), authService.getUserAuthenticated())){
            throw new AuthenticationException("User not valid");
        }

        return task;
    }
    @Override
    public Page<TaskDTO> getAllAdmin(Pageable pageable, String title) {
        Specification<Task> spec = TaskSpecification.hasName(title);;
        return taskRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    @Override
    public TaskDTO getByIdAdmin(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow( () -> new NotFoundException("Task Not Found"));

        return convertToDTO(task);
    }

    @Override
    public void delete(Integer id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("To-do item not found"));

        if (!task.getUser().equals(authService.getUserAuthenticated())) {
            throw new AccessDeniedException("Access denied");
        }

        taskRepository.deleteById(id);
    }

    @Override
    public TaskDTO2 updateById(Integer id, TaskUpdateDTO req){
        Task task = getById(id);

        if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            task.setTitle(req.getTitle());
        }
        if (req.getDescription() != null && !req.getDescription().isEmpty()) {
            task.setDescription(req.getDescription());
        }
        if (req.getDueDate() != null) {
            Date dte = parseDueDate(req.getDueDate());
            validateDueDate(dte);
            task.setDueDate(dte);
        }
//        if (req.getDeadline() != null) {
//            task.setDeadline(req.getDeadline());
//        }
        if (req.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(req.getStatus().toString()));
        }
//        if (req.getCategory() != null) {
//            task.setCategory(categoryService.getById(req.getCategory()));
//        }

        Task taskk = taskRepository.save(task);

        assert req.getStatus() != null;
        TaskDTO2 res = TaskDTO2.builder()
                .id(taskk.getId())
                .title(req.getTitle())
                .description(req.getDescription())
                .dueDate(req.getDueDate())
                .status(req.getStatus().toString())
                .createdAt(taskk.getCreatedAt())
                .build();
        return res;
    }

    @Override
    public TaskDTO2 updateById(Integer id, TaskUpdateDTO2 req){
        Task task = getById(id);
        if (req.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(req.getStatus().toString()));
        }

        Task taskk = taskRepository.save(task);

        assert req.getStatus() != null;
        TaskDTO2 res = TaskDTO2.builder()
                .id(taskk.getId())
                .title(taskk.getTitle())
                .description(taskk.getDescription())
                .dueDate(taskk.getDueDate().toString())
                .status(taskk.getStatus().toString())
                .createdAt(taskk.getCreatedAt())
                .build();
        return res;
    }

    public TaskDTO convertToDTO(Task task){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return TaskDTO.builder()
                .id(task.getId().toString())
                .userId(task.getUser().getId().toString())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(formatter.format(task.getDueDate()))
                .status(task.getStatus().toString())
                .createdAt(task.getCreatedAt())
                .build();
    }

    private Date parseDueDate(String dueDateStr) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.parse(dueDateStr);
        } catch (ParseException e) {
            throw new ValidateException("Invalid dueDate format");
        }
    }

    private void validateDueDate(Date dueDate) {
        if (dueDate == null) {
            throw new ValidateException("due date");
        }
        Date now = new Date();
        if (dueDate.before(now)) {
            throw new ValidateException("Due date must be in the future duedate");
        }
    }
}
