package ru.krey.crazy_task_tracker_api.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.krey.crazy_task_tracker_api.api.controller.helper.ControllerHelper;
import ru.krey.crazy_task_tracker_api.api.dto.AckDTO;
import ru.krey.crazy_task_tracker_api.api.dto.ProjectDTO;
import ru.krey.crazy_task_tracker_api.api.exceptions.BadRequestException;
import ru.krey.crazy_task_tracker_api.api.factories.ProjectDTOFactory;
import ru.krey.crazy_task_tracker_api.store.entities.ProjectEntity;
import ru.krey.crazy_task_tracker_api.store.repositories.ProjectRepo;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@RestController
public class ProjectController {

        private final ProjectRepo projectRepo;

        private final ProjectDTOFactory projectFactory;

        private final ControllerHelper controllerHelper;

        private static final String CREATE_PROJECT = "/api/projects";
        private static final String EDIT_PROJECT = "/api/projects/{project_id}";
        private static final String FETCH_PROJECT = "/api/projects";
        private static final String DELETE_PROJECT = "/api/projects/{project_id}";

        private static final String CREATE_OR_EDIT_PROJECT = "/api/projects/{project_id}";

        @Transactional//при ошибке в методе не будет сохранять в бд данные
        @GetMapping(FETCH_PROJECT)
        public List<ProjectDTO> fetchProjects(
                @RequestParam(value = "prefix_name",required = false) Optional<String> optionalPrefixName){
                optionalPrefixName = optionalPrefixName.filter(
                        prefixName -> !prefixName.trim().isEmpty());

                List<ProjectEntity> projects = //return all if prefix doesn't exist
                        optionalPrefixName
                                .map(projectRepo::findByNameIsStartingWith)
                                .orElseGet(projectRepo::streamAllBy).toList();

                return projects
                        .stream()
                        .map(projectFactory::makeProjectDto)
                        .collect(Collectors.toList());
        }

        @Transactional//при ошибке в методе не будет сохранять в бд данные
        @PostMapping(CREATE_PROJECT)
        public ProjectDTO createProject(@RequestParam("project_name") String projectName){

                if(projectName.trim().isEmpty()){
                        throw new BadRequestException("Name can't be empty.");
                }

                projectRepo
                        .findByName(projectName)
                        .ifPresent(project->{
                        throw new BadRequestException(String.format("project \"%s\" already exists.", projectName));
                });

                ProjectEntity project = projectRepo.saveAndFlush(//flush если нужно прочитать project до окончания метода
                        ProjectEntity.builder()
                                .name(projectName)
                                .build()
                );

                return projectFactory.makeProjectDto(project);
        }


        @Transactional//при ошибке в методе не будет сохранять в бд данные
        @PutMapping(CREATE_OR_EDIT_PROJECT)//может быть использован вместо двух методов create и update
        public ProjectDTO createOrEditProject(
                @PathVariable(value = "project_id",required = false) Optional<Long> optionalProjectId,
                @RequestParam(value = "project_name",required = false) Optional<String> optionalProjectName
        ){

                optionalProjectId.ifPresent(id -> log.info(String.format("id %s", id)));
                optionalProjectName.ifPresent(name -> log.info(String.format("name %s", name)));

                optionalProjectName = optionalProjectName.filter(name -> !name.trim().isEmpty());//если name пустая строка, то в optional будет пусто
//                boolean isCreate = !optionalProjectId.isPresent();

                if( !optionalProjectName.isPresent()){
                        throw new BadRequestException("Project name can't be empty");
                }

                final ProjectEntity project =  optionalProjectId //если объекта нет, то пробросится ошибка, если есть, то создаем объект по id
                        .map(controllerHelper::getProjectByIdOrThrowException)
                        .orElseGet(() -> ProjectEntity.builder().build());


                optionalProjectName//если нужно поменять имя проекта, но уже есть проект с таким именем, то выбрасываем исключение
                        .ifPresent(
                                projectName ->{
                                        projectRepo
                                                .findByName(projectName)
                                                .filter(anotherProject -> !Objects.equals(anotherProject.getId(),project.getId()))
                                                .ifPresent(anotherProject->{
                                                        throw new BadRequestException(
                                                                String.format("Project %s already exists!",projectName));
                                                        }
                                                );
                                        project.setUpdatedAt(Instant.now());
                                        project.setName(projectName);
                                }
                        );

                final ProjectEntity savedProject = projectRepo.saveAndFlush(project);

                return projectFactory.makeProjectDto(savedProject);
        }


        @Transactional
        @PatchMapping(EDIT_PROJECT)
        public ProjectDTO editProject(@PathVariable("project_id") Long projectId, @RequestParam(value = "project_name") String projectName){
                if(projectName.trim().isEmpty()){
                        throw new BadRequestException("Name can't be empty.");
                }

                ProjectEntity projectEntity = controllerHelper.getProjectByIdOrThrowException(projectId);

                projectRepo
                        .findByName(projectName)
                        .filter(anotherProject -> !Objects.equals(projectId,anotherProject.getId()))
                        .ifPresent(anotherProject -> {throw new BadRequestException(String.format("project \"%s\" already exists.", projectName));});

                projectEntity.setName(projectName);
                projectRepo.saveAndFlush(projectEntity);
                return projectFactory.makeProjectDto(projectEntity);
        }


        @Transactional
        @DeleteMapping(DELETE_PROJECT)
        public AckDTO deleteProject(@PathVariable("project_id") Long projectId){
                controllerHelper.getProjectByIdOrThrowException(projectId);

                projectRepo.deleteById(projectId);

                return AckDTO.makeAck(true);
        }




}
