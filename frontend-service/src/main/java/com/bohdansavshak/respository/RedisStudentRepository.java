package com.bohdansavshak.respository;

import static com.bohdansavshak.config.utils.ObjectMapperUtilsForRedis.STUDENT_KEY;

import com.bohdansavshak.config.utils.ObjectMapperUtilsForRedis;
import com.bohdansavshak.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RedisStudentRepository {

  private final RedisReactiveComponent reactiveRedisComponent;

  public Mono<Student> save(Student book) {
    return reactiveRedisComponent.set(STUDENT_KEY, book.getId(), book).map(b -> book);
  }

  public Mono<Student> get(String key) {
    return reactiveRedisComponent
        .get(STUDENT_KEY, key)
        .flatMap(d -> Mono.just(ObjectMapperUtilsForRedis.objectMapper(d, Student.class)));
  }

  public Flux<Student> getAll() {
    return reactiveRedisComponent
        .get(STUDENT_KEY)
        .map(b -> ObjectMapperUtilsForRedis.objectMapper(b, Student.class))
        .collectList()
        .flatMapMany(Flux::fromIterable);
  }

  public Mono<Long> delete(String id) {
    return reactiveRedisComponent.remove(STUDENT_KEY, id);
  }
}
