package uk.ziglio.construct.macros;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.Struct;

public class BitStruct extends Struct {
	public BitStruct() {
			super(null);
			Construct subcon = new Bitwise(subcons);
			this._set_flag( Construct.FLAG_EMBED );
			subcon._set_flag( Construct.FLAG_EMBED );
			subcons = new Construct[]{subcon};
	}
}