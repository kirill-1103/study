package ru.krey.crazy_task_tracker_api.api.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krey.crazy_task_tracker_api.api.dto.ProjectDTO;
import ru.krey.crazy_task_tracker_api.store.entities.ProjectEntity;

@Component
public class ProjectDTOFactory {

    public ProjectDTO makeProjectDto(ProjectEntity projectEntity){
       return ProjectDTO.builder()
               .id(projectEntity.getId())
               .name(projectEntity.getName())
               .createdAt(projectEntity.getCreatedAt())
               .updatedAt(projectEntity.getUpdatedAt())
               .build();
    }
}
