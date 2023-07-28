package com.bohdansavshak.controller;

import com.bohdansavshak.model.Student;
import com.bohdansavshak.repository.redis.TotalResultsRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class StudentController {

    private final TotalResultsRepository totalResultsRepository;

    @GetMapping("/hello")
    public Iterable<Student> hello() {
        Student student = new Student();
        student.setId(UUID.randomUUID().toString());
        student.setName("MyName");
        student.setGender(Student.Gender.MALE);
        student.setGrade(10);
        Student save = totalResultsRepository.save(student);
        return totalResultsRepository.findAll();
    }
}
