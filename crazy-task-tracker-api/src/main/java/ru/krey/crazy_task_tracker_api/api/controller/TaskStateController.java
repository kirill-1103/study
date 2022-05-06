package ru.krey.crazy_task_tracker_api.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.krey.crazy_task_tracker_api.api.controller.helper.ControllerHelper;
import ru.krey.crazy_task_tracker_api.api.dto.TaskStateDTO;
import ru.krey.crazy_task_tracker_api.api.exceptions.BadRequestException;
import ru.krey.crazy_task_tracker_api.api.exceptions.NotFoundException;
import ru.krey.crazy_task_tracker_api.api.factories.TaskStateDTOFactory;
import ru.krey.crazy_task_tracker_api.store.entities.ProjectEntity;
import ru.krey.crazy_task_tracker_api.store.entities.TaskStateEntity;
import ru.krey.crazy_task_tracker_api.store.repositories.ProjectRepo;
import ru.krey.crazy_task_tracker_api.store.repositories.TaskStateRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class TaskStateController {

    private final ProjectRepo projectRepo;

    private final TaskStateRepo taskStateRepo;

    private final TaskStateDTOFactory taskStateDTOFactory;

    private final ControllerHelper controllerHelper;

    private static final String GET_TASK_STATES = "/api/projects/{project_id}/task-states";

    private static final String CREATE_TASK_STATE = "/api/projects/{project_id}/task-states";

    private static final String EDIT_TASK_STATE = "/api/task-states/{task_state_id}";

    private static final String CHANGE_TASK_STATE_POSITION = "/api/task-states/{task_state_id}/change/position";

    private static final String DELETE_TASK_STATE = "/api/task-states/{task_state_id}";

    @Transactional
    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDTO> getTaskStates(@PathVariable(value = "project_id") Long projectId) {
        ProjectEntity projectEntity = controllerHelper.getProjectByIdOrThrowException(projectId);

        return projectEntity.getTaskStates()
                .stream()
                .map(taskStateDTOFactory::makeTaskStateDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDTO createTaskState(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(value = "task_state_name") String taskStateName) {

        if (taskStateName.isEmpty()) {
            throw new BadRequestException("Task state's name can't be empty!");
        }

        ProjectEntity project = controllerHelper.getProjectByIdOrThrowException(projectId);

        project.getTaskStates().stream()
                .map(TaskStateEntity::getName)
                .filter(taskStateName::equals)
                .findAny()
                .ifPresent((it) -> {
                    throw new BadRequestException(String.format("Task State with name \"%s\" already exists!", taskStateName));
                });

        TaskStateEntity taskState = TaskStateEntity.builder()
                .project(project)
                .name(taskStateName)
                .build();

        //                            otherTaskState.setRightTaskState(taskState);
        //                            taskStateRepo.saveAndFlush(taskState);
        taskStateRepo
                .findTaskStateEntityByRightTaskStateIdIsNullAndProjectId(projectId)
                .ifPresent(taskState::setLeftTaskState);


        final TaskStateEntity savedTaskState = taskStateRepo.saveAndFlush(taskState);
        savedTaskState.getLeftTaskState().ifPresent(otherTaskState -> otherTaskState.setRightTaskState(savedTaskState));

        return taskStateDTOFactory.makeTaskStateDto(savedTaskState);
    }

    @Transactional
    @PatchMapping(EDIT_TASK_STATE)
    public TaskStateDTO editTaskState(
            @PathVariable(value = "task_state_id") Long taskStateId,
            @RequestParam(value = "task_state_name") String taskStateName) {

        if (taskStateName.isBlank()) {
            throw new BadRequestException("Task state name can't be empty!");
        }

        TaskStateEntity taskState = getTaskStateByIdOrThrowException(taskStateId);

        taskStateRepo
                .findTaskStateEntitiesByProjectIdAndNameIgnoreCase(taskState.getProject().getId(), taskStateName)
                .filter(anotherTaskState -> !Objects.equals(anotherTaskState.getId(), taskState.getId()))
                .ifPresent(it -> {
                    throw new BadRequestException(String.format("Task State with name \"%s\" already exists!", taskStateName));
                });


        taskState.setName(taskStateName);

        TaskStateEntity savedTaskState = taskStateRepo.saveAndFlush(taskState);

        return taskStateDTOFactory.makeTaskStateDto(savedTaskState);
    }

    @Transactional
    @PatchMapping(CHANGE_TASK_STATE_POSITION)
    public TaskStateDTO changePosition(
            @PathVariable(value = "task_state_id") Long taskStateId,
            @RequestParam(value = "left_task_state_id", required = false) Optional<Long> optLeftTaskStateId
    ){
        TaskStateEntity changeTaskState = getTaskStateByIdOrThrowException(taskStateId);
        ProjectEntity project = changeTaskState.getProject();

        Optional<Long> optOldLeftTaskStateId = changeTaskState.getLeftTaskState().map(TaskStateEntity::getId);

        if(optOldLeftTaskStateId.equals(optLeftTaskStateId)){
            return taskStateDTOFactory.makeTaskStateDto(changeTaskState);
        }

        Optional<TaskStateEntity> newLeftTaskState = optLeftTaskStateId
                .map(leftTaskStateId->{
                    if(taskStateId.equals(leftTaskStateId)){
                        throw  new BadRequestException("Left task state id equals changed task state!");
                    }
                    TaskStateEntity leftTaskState = getTaskStateByIdOrThrowException(leftTaskStateId);

                    if(!project.getId().equals(leftTaskState.getProject().getId())){
                        throw new BadRequestException("Task state position can be changed within the same project.");
                    }
                    return leftTaskState;
                });

        Optional<TaskStateEntity> newRightTaskState;
        if(newLeftTaskState.isEmpty()){
            newRightTaskState = project.getTaskStates()
                    .stream()
                    .filter(anotherTaskState-> anotherTaskState.getLeftTaskState().isEmpty())
                    .findAny();
        }else{
            newRightTaskState = newLeftTaskState.get().getRightTaskState();
        }

        Optional<TaskStateEntity> oldLeftTaskState = changeTaskState.getLeftTaskState();
        Optional<TaskStateEntity> oldRightTaskState = changeTaskState.getRightTaskState();

        oldLeftTaskState.ifPresent(it -> {
            it.setRightTaskState(oldRightTaskState.orElse(null));

            taskStateRepo.saveAndFlush(it);
        });
        oldRightTaskState.ifPresent(it -> {
            it.setLeftTaskState(oldLeftTaskState.orElse(null));

            taskStateRepo.saveAndFlush(it);
        });

        newLeftTaskState.ifPresentOrElse(
                it->{
                    it.setRightTaskState(changeTaskState);
                    changeTaskState.setLeftTaskState(it);
                },
                ()->{
                    changeTaskState.setLeftTaskState(null);
                }
        );

        newRightTaskState.ifPresentOrElse(
                it->{
                    changeTaskState.setRightTaskState(it);
                    it.setLeftTaskState(changeTaskState);
                },
                ()->{
                    changeTaskState.setRightTaskState(null);
                }
        );

        TaskStateEntity savedTaskState = taskStateRepo.saveAndFlush(changeTaskState);

        newLeftTaskState.ifPresent(taskStateRepo::saveAndFlush);
        newRightTaskState.ifPresent(taskStateRepo::saveAndFlush);

        return taskStateDTOFactory.makeTaskStateDto(savedTaskState);
    }

    @Transactional
    @DeleteMapping(DELETE_TASK_STATE)
    public TaskStateDTO deleteTaskState(@PathVariable(value="task_state_id") Long taskStateId){
        TaskStateEntity taskState = deleteTaskStateAndReturnEntity(taskStateId);
        return taskStateDTOFactory.makeTaskStateDto(taskState);
    }

    private TaskStateEntity deleteTaskStateAndReturnEntity(Long id){
        TaskStateEntity  taskState = taskStateRepo.findById(id)
                .orElseThrow(()->new NotFoundException(String.format("Task state with id %d not found!", id)));

        taskState.getLeftTaskState()
                .ifPresent((leftTaskState)->{
                    leftTaskState.setRightTaskState(taskState.getRightTaskState()
                            .orElse(null));
                });

        taskState.getRightTaskState()
                .ifPresent(rightTaskState->{
                    rightTaskState.setLeftTaskState(taskState.getLeftTaskState()
                            .orElse(null));
                });

        taskState.setLeftTaskState(null);
        taskState.setRightTaskState(null);
        taskStateRepo.delete(taskState);
        return taskState;
    }


    private TaskStateEntity getTaskStateByIdOrThrowException(Long taskStateId){
        return taskStateRepo.findById(taskStateId)
                .orElseThrow(()->new NotFoundException(String.format("Task state with id %d not found!", taskStateId)));
    }

}

