package ru.forinnyy.tm.service;

import lombok.NonNull;
import ru.forinnyy.tm.api.repository.IProjectRepository;
import ru.forinnyy.tm.api.repository.ITaskRepository;
import ru.forinnyy.tm.api.service.IProjectTaskService;
import ru.forinnyy.tm.exception.entity.AbstractEntityException;
import ru.forinnyy.tm.exception.entity.ProjectNotFoundException;
import ru.forinnyy.tm.exception.entity.TaskNotFoundException;
import ru.forinnyy.tm.exception.field.AbstractFieldException;
import ru.forinnyy.tm.exception.field.ProjectIdEmptyException;
import ru.forinnyy.tm.exception.field.TaskIdEmptyException;
import ru.forinnyy.tm.exception.field.UserIdEmptyException;
import ru.forinnyy.tm.exception.user.AbstractUserException;
import ru.forinnyy.tm.exception.user.PermissionException;
import ru.forinnyy.tm.model.Project;
import ru.forinnyy.tm.model.Task;

import java.util.List;

public final class ProjectTaskService implements IProjectTaskService {

    @NonNull
    private final IProjectRepository projectRepository;

    @NonNull
    private final ITaskRepository taskRepository;

    public ProjectTaskService(
            @NonNull final IProjectRepository projectRepository,
            @NonNull final ITaskRepository taskRepository
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @NonNull
    @Override
    public Task bindTaskToProject(final String userId, final String projectId, final String taskId)
            throws AbstractFieldException, AbstractEntityException, AbstractUserException {
        if (userId == null || userId.isEmpty()) throw new UserIdEmptyException();
        if (projectId == null || projectId.isEmpty()) throw new ProjectIdEmptyException();
        if (taskId == null || taskId.isEmpty()) throw new TaskIdEmptyException();
        final Project project = projectRepository.findOneById(userId, projectId);
        final Task task = taskRepository.findOneById(userId, taskId);
        if (task == null) throw new TaskNotFoundException();
        if (project == null) throw new ProjectNotFoundException();
        if (!project.getUserId().equals(userId)) throw new PermissionException();
        if (!task.getUserId().equals(userId)) throw new PermissionException();
        task.setProjectId(projectId);
        return task;
    }

    @NonNull
    @Override
    public Task unbindTaskFromProject(final String userId, final String projectId, final String taskId)
            throws AbstractFieldException, AbstractEntityException, AbstractUserException {
        if (userId == null || userId.isEmpty()) throw new UserIdEmptyException();
        if (projectId == null || projectId.isEmpty()) throw new ProjectIdEmptyException();
        if (taskId == null || taskId.isEmpty()) throw new TaskIdEmptyException();
        if (!projectRepository.existsById(userId, projectId)) throw new ProjectNotFoundException();
        final Task task = taskRepository.findOneById(userId, taskId);
        if (task == null) throw new TaskNotFoundException();
        if (!task.getUserId().equals(userId)) throw new PermissionException();
        task.setProjectId(null);
        return task;
    }

    @Override
    public void removeProjectById(final String userId, final String projectId)
            throws AbstractFieldException, AbstractEntityException, AbstractUserException {
        if (userId == null || userId.isEmpty()) throw new UserIdEmptyException();
        if (projectId == null || projectId.isEmpty()) throw new ProjectIdEmptyException();
        if (!projectRepository.existsById(userId, projectId)) throw new ProjectNotFoundException();
        final Project project = projectRepository.findOneById(userId, projectId);
        if (!project.getUserId().equals(userId)) throw new PermissionException();
        @NonNull final List<Task> tasks = taskRepository.findAllByProjectId(userId, projectId);
        for (@NonNull final Task task: tasks) taskRepository.removeById(userId, task.getId());
        projectRepository.removeById(projectId);
    }

}
