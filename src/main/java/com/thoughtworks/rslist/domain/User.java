package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @NotNull
  @Size(max = 8)
  private String name;

  @NotNull
  @Min(18)
  @Max(100)
  private int age;

  @NotNull
  private String gender;

  @Email
  private String email;

  @Pattern(regexp = "1[3-9]\\d{9}")
  private String phone;
}
