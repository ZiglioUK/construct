package construct;

import static construct.lib.Binary.*;
import static construct.Core.*;
import static construct.lib.Containers.*;

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

  static public Adapter BitIntegerAdapter( Construct subcon, final int width, final boolean swapped, final boolean signed ) {
    return BitIntegerAdapter( subcon, width, swapped, signed, 8 );
  }

  static public Adapter BitIntegerAdapter( Construct subcon, final int width, final boolean swapped, final boolean signed, final int bytesize ) {
      
      return new Adapter(subcon)
      {
        public Object _encode( Object obj, Container context) {
        	int intobj = (Integer)obj; 
          if( intobj < 0 && !signed ){
              throw new BitIntegerError("object is negative, but field is not signed " + intobj );
          }
          byte[] obj2 = int_to_bin( intobj, width );
          if( swapped ){
              obj2 = swap_bytes( obj2, bytesize );
          }
          return obj2;
        }

        public Object _decode( Object obj, Container context) {
          byte[] ba = (byte[])obj;
        	if( swapped ){
            ba = swap_bytes( ba, bytesize );
          }
          return bin_to_int(ba, signed );
        }
      };
  }
}