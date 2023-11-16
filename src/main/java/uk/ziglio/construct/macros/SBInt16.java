package uk.ziglio.construct.macros;

import uk.ziglio.construct.fields.FormatField;

public class SBInt16 extends FormatField<Integer> {
  public SBInt16(String name) {
    super(name, '>', 'h');
  }
}