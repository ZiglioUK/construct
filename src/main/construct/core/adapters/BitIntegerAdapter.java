package construct.core.adapters;

import construct.core.Adapter;
import construct.core.Construct;
import construct.lib.Container;

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
public class BitIntegerAdapter extends Adapter {
	
	public class BitIntegerError extends RuntimeException {

		public BitIntegerError(String string) {
			super(string);
		}
	}
	
	int width;
	boolean swapped = false;
	boolean signed = false;
	int bytesize = 8;
	
	public BitIntegerAdapter( Construct subcon, int width, boolean swapped, boolean signed, int bytesize ) {
		super( subcon );
        this.width = width;
        this.swapped = swapped;
        this.signed = signed;
        this.bytesize = bytesize;
	}
	
    protected byte[] _encode( int obj, Container context) { 
	    if( obj < 0 && !signed ){
	        throw new BitIntegerError("object is negative, but field is not signed " + obj );
	    }
	    obj2 = int_to_bin( obj, width );
	    if( swapped ){
	        obj2 = swap_bytes( obj2, bytesize );
	    }
	    return obj2;
	}
	
	protected Object[] _decode( Object obj, Container context) {
		if( swapped ){
            obj = swap_bytes(obj, bytesize = self.bytesize)
		}
        return bin_to_int(obj, signed = self.signed)
	}
}
