package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;

/**
 * @author gaarahan
 */
public class User {

  @NotNull
  @Size(max = 8)
  @JsonProperty("user_name")
  private String name;

  @NotNull
  @Min(18)
  @Max(100)
  @JsonProperty("user_age")
  private int age;

  @NotNull
  @JsonProperty("user_gender")
  private String gender;

  @Email
  @JsonProperty("user_email")
  private String email;

  @JsonProperty("user_phone")
  @Pattern(regexp = "1[3-9][0-9]{9}")
  private String phone;

  public User() { }

  public User(String name, int age, String gender, String email, String phone) {
    this.name = name;
    this.age = age;
    this.gender = gender;
    this.email = email;
    this.phone = phone;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  public String getGender() {
    return gender;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }
}
