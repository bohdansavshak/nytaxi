package com.bohdansavshak.service;

import com.bohdansavshak.model.Student;
import com.bohdansavshak.respository.RedisBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl {

    private final RedisBookRepository bookRepository;

    public Mono<Student> create(Student book) {
        return bookRepository.save(book);
    }

    public Flux<Student> getAll(){
        return bookRepository.getAll();
    }

    public Mono<Student> getOne(String id){
        return bookRepository.get(id);
    }

    public Mono<Long> deleteById(String id) {
        return bookRepository.delete(id);
    }
}
