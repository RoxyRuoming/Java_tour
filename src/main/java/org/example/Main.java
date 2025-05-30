package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    // the store area of int and Integer is different in JVM storage
    int i = 2;
    Integer j = 1;

    // Demonstrate Employee immutability
    System.out.println("===== Employee Immutability Demo =====");
    List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3));
    EmployeeImmutable employee = new EmployeeImmutable("John", "Doe", "john@example.com", 12345, false, numbers);

    // Try to modify the original list
    numbers.add(4);
    System.out.println("Original list after modification: " + numbers);
    System.out.println("Employee's list (should be unaffected): " + employee.getList());

    // Try to modify the list returned by getList (should throw exception)
    try {
      employee.getList().add(5);
    } catch (UnsupportedOperationException e) {
      System.out.println("Cannot modify list from getList() - immutability preserved!");
    }

    // Use the copy method to get a modifiable copy
    List<Integer> copy = employee.getListCopy();
    copy.add(5);
    System.out.println("Modified copy: " + copy);
    System.out.println("Employee's list (still unmodified): " + employee.getList());

    // Demonstrate Singleton pattern
    System.out.println("\n===== EmployeeSingleton Demo =====");
    EmployeeSingleton singleton1 = EmployeeSingleton.getInstance();
    EmployeeSingleton singleton2 = EmployeeSingleton.getInstance();

    System.out.println("Are they the same instance? " + (singleton1 == singleton2));

    // Show current values
    System.out.println("Default first name: " + singleton1.getFirstName());

    // Modify the singleton (note: this is allowed but not typical for singletons)
    singleton1.setFirstName("Jane");

    // Show both variables reflecting the change (because they reference the same object)
    System.out.println("singleton1 first name: " + singleton1.getFirstName());
    System.out.println("singleton2 first name: " + singleton2.getFirstName());
  }
}