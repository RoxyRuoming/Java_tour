package org.example.exceptions;

import java.io.IOException;

public class MyException extends RuntimeException{ // unchecked exception
  // SerilizationUID -> serializable interface
  private String errorCode; // tier - layer - type
  private String message;

  public void MyMethod() throws IOException {
    // try catch block


  }


}
