package ru.krey.crazy_task_tracker_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AckDTO {
    private Boolean answer;
    public static AckDTO makeAck(Boolean answer){
        return AckDTO.builder()
                .answer(answer)
                .build();
    }
}
