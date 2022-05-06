package ru.krey.crazy_task_tracker_api.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStateDTO {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @JsonProperty("created_at")
    Instant createdAt;

    @JsonProperty("left_task_state_id")
    Long leftTaskStateId;

    @JsonProperty("right_task_state_id")
    Long rightTaskStateId;

    @NotNull
    private List<TaskDTO> tasks;
}
