package uk.ziglio.construct.macros;

import uk.ziglio.construct.fields.FormatField;

public class SBInt8 extends FormatField<Integer> {
    public SBInt8(String name) {
      super(name, '>', 'b');
    }
  }