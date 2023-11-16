package uk.ziglio.construct.macros;

import uk.ziglio.construct.fields.FormatField;

/**
* @return unsigned, little endian 16-bit integer
*/
  public class ULInt16 extends FormatField<Integer>{
    public ULInt16(String name){
      super( name, '<', 'H' );
    }
  }