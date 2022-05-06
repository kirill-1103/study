package ru.krey.crazy_task_tracker_api.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @JsonProperty("created_at")
    Instant createdAt;

    @NotNull
    private String description;

    @NotNull
    private Long position;

    @NotNull
    private Long taskStateId;

}
