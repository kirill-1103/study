package ru.krey.exam.historymicro;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<Question,Integer> {
}
