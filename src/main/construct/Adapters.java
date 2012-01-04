package construct;

import static construct.lib.Binary.*;
import static construct.Core.*;
import construct.lib.Container;

public class Adapters
{
  public static class BitIntegerError extends RuntimeException {
    public BitIntegerError(String string) {
      super(string);
    }
  }
  
  /**
  """
  Adapter for bit-integers (converts bitstrings to integers, and vice versa).
  See BitField.
  
  Parameters:
  * subcon - the subcon to adapt
  * width - the size of the subcon, in bits
  * swapped - whether to swap byte order (little endian/big endian). 
    default is False (big endian)
  * signed - whether the value is signed (two's complement). the default
    is False (unsigned)
  * bytesize - number of bits per byte, used for byte-swapping (if swapped).
    default is 8.
  """
  *
  */
  static public Adapter BitIntegerAdapter( Construct subcon, final int width ) {
    return BitIntegerAdapter( subcon, width, false, false, 8 );
  }
  
  static public Adapter BitIntegerAdapter( Construct subcon, final int width, final boolean swapped, final boolean signed, final int bytesize ) {
      
      return new Adapter(subcon)
      {
        public byte[] _encode( int obj, Container context) { 
          if( obj < 0 && !signed ){
              throw new BitIntegerError("object is negative, but field is not signed " + obj );
          }
          byte[] obj2 = int_to_bin( obj, width );
          if( swapped ){
              obj2 = swap_bytes( obj2, bytesize );
          }
          return obj2;
        }

        public int _decode( byte[] obj, Container context) {
          if( swapped ){
            obj = swap_bytes( obj, bytesize );
          }
          return bin_to_int(obj, signed );
        }
      };
  }
}