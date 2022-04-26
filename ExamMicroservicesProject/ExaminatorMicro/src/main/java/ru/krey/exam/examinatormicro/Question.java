package ru.krey.exam.examinatormicro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {
    private String question;
    private String answer;
}
