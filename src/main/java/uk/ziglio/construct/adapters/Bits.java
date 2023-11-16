package uk.ziglio.construct.adapters;

import uk.ziglio.construct.macros.Macros;

/**
   * Bits is just an alias for BitField
   */
  public class Bits extends BitIntegerAdapter{
    
    public Bits( final String name, final int length, boolean swapped, boolean signed, int bytesize ) {
      super( Macros.Field(name, length), length, swapped, signed, bytesize );
    }

    public Bits( final String name, final int length ) {
      this( name, length, false, false, 8 );
    }

    @Override
    public Integer get() {
      return (Integer)val;
    }
  }