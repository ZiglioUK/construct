package uk.ziglio.construct.adapters;

import uk.ziglio.construct.Adapter;
import static uk.ziglio.construct.macros.Macros.BitField;

public class Bit extends BitField {
    public Bit(String name){
      super( name, 1 );
    }

	/*
	  #===============================================================================
		# field shortcuts
		#===============================================================================
	*/
	  /**
	  * @return a 1-bit BitField; must be enclosed in a BitStruct
	  */
	  public static Adapter Bit(String name){
	  	return BitField( name, 1 );
	  }
  }