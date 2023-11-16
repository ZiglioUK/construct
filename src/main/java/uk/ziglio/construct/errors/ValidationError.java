package uk.ziglio.construct.errors;

public class ValidationError extends RuntimeException {
    public ValidationError(String string) {
      super(string);
    }
  }