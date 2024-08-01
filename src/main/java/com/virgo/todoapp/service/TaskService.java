package com.virgo.todoapp.service;

import com.virgo.todoapp.utils.dto.*;
import com.virgo.todoapp.entity.meta.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Task create(TaskRequestDTO req);
    Page<Task> getAll(Pageable pageable, String name);
    Task getById(Integer id);
    Page<TaskDTO> getAllAdmin(Pageable pageable, String title);
    TaskDTO getByIdAdmin(Integer id);
    void delete(Integer id);
//    Task update (fitur)
    TaskDTO2 updateById(Integer id, TaskUpdateDTO req);
    TaskDTO2 updateById(Integer id, TaskUpdateDTO2 req);
}
