package ru.krey.crazy_task_tracker_api.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;
import ru.krey.crazy_task_tracker_api.store.entities.TaskEntity;

import java.util.Optional;

public interface TaskRepo extends JpaRepository<TaskEntity,Long> {
    Optional<TaskEntity> findTaskEntityById(Long id);
}
