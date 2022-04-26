package ru.krey.exam.javamicro;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MathController {

    private final MathService mathService;

    @GetMapping("/questions")
    public List<Question> getRandomQuestions(@RequestParam("amount") Integer amount){
        List<Question> questions = new ArrayList<>();
        for(int i = 0;i<amount;i++){
            questions.add(mathService.getRandomQuestion());
        }
        return questions;
    }
}
