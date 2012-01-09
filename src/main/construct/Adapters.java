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
  public static class PaddingError extends RuntimeException {
    public PaddingError(String string) {
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

  static public Adapter PaddingAdapter( Construct subcon ) {
  	return PaddingAdapter( subcon, (byte)0x00, false );
  }

  /**
   * @param subcon the subcon to pad
   * @param pattern the padding pattern (character). default is "\x00"
   * @param strict whether or not to verify, during parsing, that the given 
      padding matches the padding pattern. default is False (unstrict)
   * @return Adapter for padding.
   */
  static public Adapter PaddingAdapter( Construct subcon, final byte pattern, final boolean strict ) {
    
    return new Adapter(subcon)
    {
      public Object _encode( Object obj, Container context) {
      	byte[] out = new byte[_sizeof(context)];
      	for( int i = 0; i<out.length; i++)
      		out[i] = pattern;
      	return out;
      }

      public Object _decode( Object obj, Container context) {
        if( strict ){
        	byte[] expected = new byte[_sizeof(context)];
        	for( int i = 0; i<expected.length; i++)
        		expected[i] = pattern;
        	
        	if( !obj.equals(expected))
        		throw new PaddingError( "Expected " + expected + " found " + obj );
        }
        return obj;
      }
    };
}
  
  /*
    def _decode(self, obj, context):
   */
}