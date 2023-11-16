package uk.ziglio.construct.macros;

import static uk.ziglio.construct.lib.Binary.BinaryDecoder;
import static uk.ziglio.construct.lib.Binary.BinaryEncoder;

import uk.ziglio.construct.core.Buffered;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.errors.SizeofError;

public class BitwiseBuffered extends Buffered {
  public BitwiseBuffered(Construct subcon) {
    super( subcon,
        BinaryEncoder(),
        BinaryDecoder(),
        length -> {
            if( (length & 7) != 0 )
              throw new SizeofError("size must be a multiple of 8, size = " + length );
          return length >> 3;
        }
      );
  }
  
  @Override
  public Construct get(){
    return subcon;
  }

}