package com.bohdansavshak.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@NoArgsConstructor
@Data
@RedisHash("Student")
public class Student implements Serializable {

  public enum Gender {
    MALE,
    FEMALE
  }

  private String id;
  private String name;
  private Gender gender;
  private int grade;
  // ...
}
