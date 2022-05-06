package ru.krey.crazy_task_tracker_api.api.factories;

import org.springframework.stereotype.Component;
import ru.krey.crazy_task_tracker_api.api.dto.TaskDTO;
import ru.krey.crazy_task_tracker_api.store.entities.TaskEntity;

@Component
public class TaskDTOFactory {

    public TaskDTO makeTaskDto(TaskEntity taskEntity){
        return TaskDTO.builder()
                .id(taskEntity.getId())
                .createdAt(taskEntity.getCreatedAt())
                .description(taskEntity.getDescription())
                .name(taskEntity.getName())
                .position(taskEntity.getPosition())
                .taskStateId(taskEntity.getTaskState().getId())
                .build();
    }
}
