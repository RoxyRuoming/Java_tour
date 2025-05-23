package org.example.exceptions;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class StreamApiPractice {
  public static void main(String[] args) {
    // Example 1: Basic Filtering and Mapping
    List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
    List<String> fileteredNames = names.stream()
        .filter(name ->  name.length() > 3)
        .map(String::toUpperCase)
        .collect(Collectors.toList());
    System.out.println("Example1: " + fileteredNames);


    // Example 2: Summing Numbers
    List<Integer> numbers = Arrays.asList(1,2,3,4,5);
    int sum = numbers.stream()
        .filter(n->n%2 == 0)
        .mapToInt(Integer:: intValue)
        .sum();
    System.out.println("Example2: " + sum);

  // Find maximum value
      Optional<Integer> max = numbers.stream()
          .max(Comparator.naturalOrder());

      max.ifPresent(m -> System.out.println("Max value: " + m)); // 9
    // grouping by
    List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");

// Group words by their length
    Map<Integer, List<String>> groupedByLength = words.stream()
        .collect(Collectors.groupingBy(String::length));

    System.out.println(groupedByLength);
// {5=[apple], 6=[banana, cherry], 4=[date], 10=[elderberry]}

    // flatmap example
    List<List<Integer>> numberLists = Arrays.asList(
        Arrays.asList(1, 2, 3),
        Arrays.asList(4, 5, 6),
        Arrays.asList(7, 8, 9)
    );

// Flatten the list of lists
    List<Integer> flattened = numberLists.stream()
        .flatMap(List::stream)
        .collect(Collectors.toList());

    System.out.println(flattened); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

    // distinct and sorting
    List<Integer> numbers1 = Arrays.asList(5, 3, 5, 2, 7, 3, 1);

// Get distinct numbers and sort them
    List<Integer> distinctSorted = numbers1.stream()
        .distinct()
        .sorted()
        .collect(Collectors.toList());

    System.out.println(distinctSorted); // [1, 2, 3, 5, 7]
  }
}