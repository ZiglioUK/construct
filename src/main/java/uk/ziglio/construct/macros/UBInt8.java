package uk.ziglio.construct.macros;

import uk.ziglio.construct.fields.FormatField;

public class UBInt8 extends FormatField<Integer> {
  public UBInt8(String name) {
    super(name, '>', 'B');
  }
}