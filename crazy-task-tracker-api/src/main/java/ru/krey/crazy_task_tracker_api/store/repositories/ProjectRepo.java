package ru.krey.crazy_task_tracker_api.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krey.crazy_task_tracker_api.store.entities.ProjectEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProjectRepo extends JpaRepository<ProjectEntity,Long> {

    Optional<ProjectEntity> findByName(String name);

    Stream<ProjectEntity> findByNameIsStartingWith(String prefixName);

    Stream<ProjectEntity> streamAllBy();

}
