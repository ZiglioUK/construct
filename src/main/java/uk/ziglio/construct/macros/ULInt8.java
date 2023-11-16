package uk.ziglio.construct.macros;

import uk.ziglio.construct.fields.FormatField;

/**
 * @return unsigned, little endian 8-bit integer
 */
  public class ULInt8 extends FormatField<Integer> {
  public ULInt8(String name){
    super( name, '<', 'B' );
  }
  }