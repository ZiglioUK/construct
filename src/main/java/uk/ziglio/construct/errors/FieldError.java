package uk.ziglio.construct.errors;

public class FieldError extends ConstructError {
  public FieldError(String string) {
    super(string);
  }

  public FieldError(String string, Exception e) {
    super(string, e);
  }
}