package ru.krey.crazy_task_tracker_api.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.krey.crazy_task_tracker_api.api.controller.helper.ControllerHelper;
import ru.krey.crazy_task_tracker_api.api.dto.TaskDTO;
import ru.krey.crazy_task_tracker_api.api.exceptions.BadRequestException;
import ru.krey.crazy_task_tracker_api.api.exceptions.NotFoundException;
import ru.krey.crazy_task_tracker_api.api.factories.TaskDTOFactory;
import ru.krey.crazy_task_tracker_api.store.entities.ProjectEntity;
import ru.krey.crazy_task_tracker_api.store.entities.TaskEntity;
import ru.krey.crazy_task_tracker_api.store.entities.TaskStateEntity;
import ru.krey.crazy_task_tracker_api.store.repositories.TaskRepo;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Transactional
public class TaskController {

    private final TaskRepo taskRepo;

    private final TaskDTOFactory taskDTOFactory;

    private final ControllerHelper controllerHelper;

    private final static String CREATE_TASK = "/api/project/{task_name}/task";

    private final static String GET_TASK_BY_ID = "/api/task";

    private final static String EDIT_TASK = "/api/tasks/{task_id}";

    private final static String DELETE_TASK = "/api/tasks/{task_id}";

    private final static String CHANGE_POSITION = "/api/tasks/position/{task_id}";

    @PostMapping(CREATE_TASK)
    public TaskDTO createTask(
        @PathVariable(value="task_name") String taskName,
        @RequestParam(value="project_id") Long projectId,
        @RequestParam(value="description") String description
    ){
        if(taskName.isBlank()){
            throw new BadRequestException("Task name can't be blank");
        }
        if(description.isBlank()){
            throw new BadRequestException("Task description can't be blank");
        }

        ProjectEntity project = controllerHelper.getProjectByIdOrThrowException(projectId);

        TaskStateEntity firstTaskState = project.getTaskStates()
                        .stream()
                                .filter(taskState->taskState.getLeftTaskState().isEmpty())
                                        .findFirst()
                                                .orElseThrow(()->new BadRequestException("Project must have at least one TaskState to create Task!"));

        checkTaskWithSameNameInThisProject(project,taskName);

        TaskEntity taskEntity = TaskEntity.builder()
                .name(taskName)
                .position(firstTaskState.getTasks().size()+1L)
                .taskState(firstTaskState)
                .description(description)
                .build();

        taskEntity = taskRepo.saveAndFlush(taskEntity);
        return taskDTOFactory.makeTaskDto(taskEntity);
    }


    @GetMapping(GET_TASK_BY_ID)
    public TaskDTO getTask(@RequestParam(value="task_id") Long taskId){
        TaskEntity taskEntity = getTaskOrThrowException(taskId);
        return taskDTOFactory.makeTaskDto(taskEntity);
    }

    @PatchMapping(EDIT_TASK)
    public TaskDTO editTask(
            @PathVariable(value="task_id") Long taskId,
            @RequestParam(value="task_name",required = false) Optional<String> optTaskName,
            @RequestParam(value="task_description",required = false) Optional<String> optTaskDescription
            ){
        if(optTaskDescription.isEmpty() && optTaskName.isEmpty() ){
            throw new BadRequestException("Task's name or task's description should be in request!");
        }

        TaskEntity task = getTaskOrThrowException(taskId);

        ProjectEntity project = controllerHelper
                .getProjectByIdOrThrowException(task.getTaskState().getProject().getId());

        optTaskName.ifPresent(taskName->{
            if(taskName.isBlank()){
                throw new BadRequestException("Name's value can't be empty if name exist in request!");
            }
            checkTaskWithSameNameInThisProject(project,taskName);
            task.setName(taskName);
        });

        optTaskDescription.ifPresent(taskDescription->{
            if(taskDescription.isBlank()){
                throw new BadRequestException("Description's value can't be empty if it exist in request!");
            }
            task.setDescription(taskDescription);
        });

        TaskEntity savedTask = taskRepo.saveAndFlush(task);

        return taskDTOFactory.makeTaskDto(savedTask);
    }

    @DeleteMapping(DELETE_TASK)
    public TaskDTO deleteTask(@PathVariable(value="task_id") Long taskId){
        TaskEntity task = getTaskOrThrowException(taskId);
        List<TaskEntity> tasksInTaskState = task.getTaskState().getTasks();

        tasksInTaskState.stream()
                .filter(otherTask -> otherTask.getPosition()>task.getPosition())
                .forEach(otherTask -> {
                    otherTask.setPosition(otherTask.getPosition()-1);
                    taskRepo.saveAndFlush(otherTask);
                });

        taskRepo.delete(task);
        return taskDTOFactory.makeTaskDto(task);
    }

    @PatchMapping(CHANGE_POSITION)
    public TaskDTO changePosition(
            @PathVariable(value = "task_id") Long taskId,
            @RequestParam(value = "new_position") Long position
    ){
        TaskEntity task = getTaskOrThrowException(taskId);

        List<TaskEntity> otherTasks = task.getTaskState().getTasks();

        if(position > otherTasks.size()){
            throw new BadRequestException("Bad position!");
        }

        if(position >= task.getPosition()){
            otherTasks.stream()
                    .filter(otherTask -> otherTask.getPosition()<=position && otherTask.getPosition()>task.getPosition())
                    .forEach(otherTask->{
                        otherTask.setPosition(otherTask.getPosition()-1);
                        taskRepo.saveAndFlush(otherTask);
                    });
        }else{
            otherTasks.stream()
                    .filter(otherTask -> otherTask.getPosition()>=position && otherTask.getPosition()<task.getPosition())
                    .forEach(otherTask->{
                        otherTask.setPosition(otherTask.getPosition()+1);
                        taskRepo.saveAndFlush(otherTask);
                    });
        }
        task.setPosition(position);
        TaskEntity savedTask = taskRepo.saveAndFlush(task);
        return taskDTOFactory.makeTaskDto(savedTask);
    }



    private TaskEntity getTaskOrThrowException(Long taskId){
        return taskRepo.findTaskEntityById(taskId)
                .orElseThrow(()->new NotFoundException(String.format("Task with id %d not found.", taskId)));
    }

    private void checkTaskWithSameNameInThisProject(ProjectEntity project, String taskName) {
        project.getTaskStates()
                .forEach((taskState)->{
                    taskState.getTasks()
                            .stream()
                            .map(TaskEntity::getName)
                            .filter(taskName::equalsIgnoreCase)
                            .findAny()
                            .ifPresent(it->{
                                throw new BadRequestException(String.format("Task with name %s already exists in project", taskName));
                            });
                });
    }
}
