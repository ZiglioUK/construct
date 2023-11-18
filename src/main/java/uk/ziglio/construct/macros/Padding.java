package uk.ziglio.construct.macros;

import uk.ziglio.construct.Macros;
import uk.ziglio.construct.adapters.PaddingAdapter;
import uk.ziglio.construct.annotations.len;
import uk.ziglio.construct.interfaces.LengthConstruct;

public class Padding extends PaddingAdapter {
  	public Padding( int length, byte pattern, boolean strict ) {
  		super( Macros.Field( null, length ), pattern, strict );
  	}
  	
  	public Padding( String name, int length ) {
  		this( length, (byte)0x00, false );
  	}

//  	public Padding( int length ) {
//  		this( null, length );
//  	}

  }