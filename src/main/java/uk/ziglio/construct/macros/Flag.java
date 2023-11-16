package uk.ziglio.construct.macros;

import static uk.ziglio.construct.Core.Container;

import uk.ziglio.construct.Macros;

public class Flag extends SymmetricMapping {
  	
  	public Flag(String name, byte truth, byte falsehood, Object defaultmapping ){
	  	super( Macros.Field(name,1),
	  			Container( true, truth, false, falsehood ),
					defaultmapping );
  	}

  	public Flag(String name) {
  		this(name, (byte)1, (byte)0, false );
  	}
  }