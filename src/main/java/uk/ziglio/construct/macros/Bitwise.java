package uk.ziglio.construct.macros;

import static uk.ziglio.construct.lib.Binary.BinaryDecoder;
import static uk.ziglio.construct.lib.Binary.BinaryEncoder;

import uk.ziglio.construct.core.Buffered;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.errors.SizeofError;

/**
 * converts the stream to bits, and passes the bitstream to subcon
 * @param subcon a bitwise construct (usually BitField)
 * @return
 */
public class Bitwise extends Buffered {
	public Bitwise(Construct subcon ) {
    super( subcon,
				BinaryEncoder(),
				BinaryDecoder(),
				length -> {
		      if( (length & 7) != 0 )
		        throw new SizeofError("size must be a multiple of 8, size = " + length );
		    return length >> 3; });
	}
	
	public Bitwise( Construct... subcons ) {
		this( new Struct( null, subcons ));
	}
}