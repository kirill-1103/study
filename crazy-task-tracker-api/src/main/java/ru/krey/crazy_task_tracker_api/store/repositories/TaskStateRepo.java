package ru.krey.crazy_task_tracker_api.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krey.crazy_task_tracker_api.store.entities.TaskStateEntity;

import java.util.List;
import java.util.Optional;

public interface TaskStateRepo extends JpaRepository<TaskStateEntity,Long> {
    Optional<TaskStateEntity> findTaskStateEntityByRightTaskStateIdIsNullAndProjectId(Long projectId);

    Optional<TaskStateEntity> findTaskStateEntitiesByProjectIdAndNameIgnoreCase(Long projectId,String name);

    Optional<TaskStateEntity> findTaskStateEntityByLeftTaskStateIdIsNullAndProjectId(Long projectId);
}
