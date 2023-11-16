package uk.ziglio.construct.errors;

public class MappingError extends RuntimeException {
    public MappingError(String string) {
      super(string);
    }
  }