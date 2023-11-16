package uk.ziglio.construct.macros;

import uk.ziglio.construct.fields.FormatField;

public class UBInt32 extends FormatField<Integer> {
  public UBInt32(String name) {
    super(name, '>', 'L');
  }
}