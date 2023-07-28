package com.bohdansavshak.repository.redis;

import com.bohdansavshak.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalResultsRepository extends CrudRepository<Student, String> {}
