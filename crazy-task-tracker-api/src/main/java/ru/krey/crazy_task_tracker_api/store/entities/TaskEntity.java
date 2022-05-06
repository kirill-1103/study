package ru.krey.crazy_task_tracker_api.store.entities;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;

    private Long position;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @ManyToOne
    @NotNull
    private TaskStateEntity taskState;
}
