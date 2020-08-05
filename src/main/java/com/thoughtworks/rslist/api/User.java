package com.thoughtworks.rslist.api;

/**
 * @author gaarahan
 */
public class User {

  private String name;
  private int age;
  private String gender;
  private String email;
  private String phone;

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
