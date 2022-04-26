package ru.krey.exam.historymicro;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/questions")
    public List<Question> getQuestions(@RequestParam("amount") int amount){
        List<Question> questions = historyService.getQuestions();
        Collections.shuffle(questions);
        return questions.stream().limit(amount).collect(Collectors.toList());
    }
}
