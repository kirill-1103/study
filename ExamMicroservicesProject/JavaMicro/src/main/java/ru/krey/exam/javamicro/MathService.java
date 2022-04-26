package ru.krey.exam.javamicro;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MathService {
    Random random = new Random();
    final Integer maxValue = 100;

    public Question getRandomQuestion(){
        int a = random.nextInt(maxValue);
        int b = random.nextInt(maxValue);
        return Question.builder().question(a + " + " + b + " = ?" )
                .answer(String.valueOf(a+b)).build();
    }
}
