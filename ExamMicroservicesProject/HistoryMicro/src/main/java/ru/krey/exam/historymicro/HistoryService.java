package ru.krey.exam.historymicro;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final QuestionRepo questionRepo;

    public List<Question> getQuestions(){
        return questionRepo.findAll();
    }
}
