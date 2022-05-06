package ru.krey.crazy_task_tracker_api.api.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krey.crazy_task_tracker_api.api.dto.TaskDTO;
import ru.krey.crazy_task_tracker_api.api.dto.TaskStateDTO;
import ru.krey.crazy_task_tracker_api.store.entities.TaskStateEntity;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskStateDTOFactory {

        private final TaskDTOFactory taskDTOFactory;

        public TaskStateDTO makeTaskStateDto(TaskStateEntity entity){
            return TaskStateDTO.builder()
                    .id(entity.getId())
                    .createdAt(entity.getCreatedAt())
                    .leftTaskStateId(entity.getLeftTaskState().map(TaskStateEntity::getId).orElse(null))
                    .rightTaskStateId(entity.getRightTaskState().map(TaskStateEntity::getId).orElse(null))
                    .name(entity.getName())
                    .tasks(
                            entity.getTasks()
                                    .stream()
                                    .map(taskDTOFactory::makeTaskDto)
                                    .collect(Collectors.toList())
                    )
                    .build();
        }
}




