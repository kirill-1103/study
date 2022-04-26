package ru.krey.exam.javamicro;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private String question;
    private String answer;

}
