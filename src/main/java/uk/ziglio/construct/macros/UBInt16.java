package uk.ziglio.construct.macros;

import uk.ziglio.construct.fields.FormatField;

public class UBInt16 extends FormatField<Integer> {
  public UBInt16(String name) {
    super(name, '>', 'H');
  }
}