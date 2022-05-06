package ru.krey.crazy_task_tracker_api.api.controller.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.krey.crazy_task_tracker_api.api.exceptions.NotFoundException;
import ru.krey.crazy_task_tracker_api.store.entities.ProjectEntity;
import ru.krey.crazy_task_tracker_api.store.repositories.ProjectRepo;

@RequiredArgsConstructor
@Service
public class ControllerHelper {

    private final ProjectRepo projectRepo;

    public ProjectEntity getProjectByIdOrThrowException(Long projectId) {
        return projectRepo
                .findById(projectId).
                orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with id %s doesn't exist",
                                        projectId
                                )
                        )
                );
    }
}
