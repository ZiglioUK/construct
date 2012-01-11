package construct;

import static construct.lib.Binary.*;
import static construct.Core.*;
import static construct.lib.Container.*;
import construct.lib.Container;

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
  public static class MappingError extends RuntimeException {
    public MappingError(String string) {
      super(string);
    }
  }
  public static class ConstError extends RuntimeException {
    public ConstError(String string) {
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
    return new BitIntegerAdapter( subcon, width, false, false, 8 );
  }

  static public Adapter BitIntegerAdapter( Construct subcon, final int width, final boolean swapped, final boolean signed ) {
    return new BitIntegerAdapter( subcon, width, swapped, signed, 8 );
  }

  static public Adapter BitIntegerAdapter( Construct subcon, final int width, final boolean swapped, final boolean signed, final int bytesize ) {
  	return new BitIntegerAdapter(subcon, width, swapped, signed, bytesize);
  }
  
  static public class BitIntegerAdapter extends Adapter{
  	final int width;
  	final boolean swapped;
  	final boolean signed;
  	final int bytesize;
  	
  	public BitIntegerAdapter( Construct subcon, final int width, final boolean swapped, final boolean signed, final int bytesize ) {
        super(subcon);
        this.width = width;
        this.swapped = swapped;
        this.signed = signed;
        this.bytesize = bytesize;
  		}
  	
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
  }

  /**
   * @param subcon the subcon to map
   * @param decoding the decoding (parsing) mapping (a dict)
   * @param encoding the encoding (building) mapping (a dict)
   * @param decdefault the default return value when the object is not found
      in the decoding mapping. if no object is given, an exception is raised.
      if `Pass` is used, the unmapped object will be passed as-is
   * @param encdefault the default return value when the object is not found
      in the encoding mapping. if no object is given, an exception is raised.
      if `Pass` is used, the unmapped object will be passed as-is
   * @return Adapter that maps objects to other objects.
    See SymmetricMapping and Enum.
   */
  static public Adapter MappingAdapter( Construct subcon, final Container decoding, final Container encoding, 
  																			final Object decdefault, final Object encdefault ) {
    return new Adapter(subcon)
    {
      public Object _encode( Object obj, Container context) {
      	if( encoding.contains(obj) )
      		return encoding.get(obj);
      	else {
      		if( encdefault == null )
      			throw new MappingError("no encoding mapping for " + obj );
      		if( encdefault == Pass )
      			return obj;
      		return encdefault;
      	}
      }
      public Object _decode( Object obj, Container context) {
      	if( obj instanceof byte[])
      		obj = ((byte[])obj)[0];
      	
      	if( decoding.contains(obj) )
      		return decoding.get(obj);
      	else{
      		if( decdefault == null )
      			throw new MappingError("no encoding mapping for " + obj );
      		if( decdefault == Pass )
      			return obj;
      		return decdefault;
      	}
      }
  };
}

  /**
   * @param subcon the subcon to validate
   * @param value the expected value
   * @return Adapter for enforcing a constant value ("magic numbers"). When decoding,
      the return value is checked; when building, the value is substituted in.
      Example:
      Const(Field("signature", 2), "MZ")
   */
  static public Adapter Const( Construct subcon, final Object value ){
  	return ConstAdapter(subcon,value);
  }
/**
 * @param subcon the subcon to validate
 * @param value the expected value
 * @return Adapter for enforcing a constant value ("magic numbers"). When decoding,
    the return value is checked; when building, the value is substituted in.
    Example:
    Const(Field("signature", 2), "MZ")
 */
static public Adapter ConstAdapter( Construct subcon, final Object value ){
	return new Adapter(subcon)
  {
    public Object _encode( Object obj, Container context) {
      if( obj == null || obj.equals(value))
        return value;
      else
      	throw new ConstError( "expected " + value + " found " + obj );
    }

    public Object _decode( Object obj, Container context) {
      if( !obj.equals(value) )
      	throw new ConstError( "expected " + value + " found " + obj );
      return obj;
    }
  };
}

/*
class MappingAdapter(Adapter):
    def _encode(self, obj, context):
    def _decode(self, obj, context):
 */
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