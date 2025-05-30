package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* 1. Converting to an Immutable Class
Make the class final so it can't be extended
Make all fields private and final
Remove all setters
Return defensive copies of mutable objects (like the List)
Ensure proper construction
 */
public final class EmployeeImmutable { // final - the class can't be extended

  private final String firstName; // make all fields private and final
  private final String lastName;
  private final String email;
  private final Integer password;
  private final Boolean flagged;
  private final List<Integer> list;

  public EmployeeImmutable(String firstName, String lastName, String email, Integer password,
      Boolean flagged,
      List<Integer> list) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.flagged = flagged;
//    this.list = list;

    this.list = new ArrayList<>(list); // deep copy
//    this.list = list != null ? new ArrayList<>(list) : new ArrayList<>(); // Create a defensive copy of the list
  }
  // 只有 getter，没有 setter
  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public Integer getPassword() {
    return password;
  }

  public Boolean getFlagged() {
    return flagged;
  }

  public List<Integer> getList() {
    // Return a defensive copy so the internal list can't be modified
    // return unmodifiable list, avoid external change of the list
    return Collections.unmodifiableList(list);
  }

  // optional: provide a deep copy method
  public List<Integer> getListCopy() {
    return new ArrayList<>(list);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeImmutable employee = (EmployeeImmutable) o;
    return Objects.equals(firstName, employee.firstName) && Objects.equals(
        lastName, employee.lastName) && Objects.equals(email, employee.email)
        && Objects.equals(password, employee.password) && Objects.equals(flagged,
        employee.flagged) && Objects.equals(list, employee.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, email, password, flagged, list);
  }
}
