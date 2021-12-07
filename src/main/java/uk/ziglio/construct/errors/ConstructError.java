package uk.ziglio.construct.errors;

public class ConstructError extends RuntimeException {
  public ConstructError(String string) {
    super(string);
  }

  public ConstructError(String string, Exception e) {
    super(string, e);
  }
}