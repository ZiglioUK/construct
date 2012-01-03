package construct.core;

import construct.lib.Container;

/**
	"""
	Abstract adapter: calls _decode for parsing and _encode for building.
	"""
 *
 */
public abstract class Adapter extends Subconstruct {

    /**
     * @param name
     * @param subcon the construct to wrap
     */
    public Adapter( Construct subcon ) {
		super(subcon);
	}

	@Override
	public Object _parse(String stream, Container context) {
        return _decode( subcon._parse(stream, context), context);
	}

	public void _build( int obj, StringBuilder stream, Container context) {
        subcon._build( _encode(obj, context), stream, context );
	}

    abstract public int _decode( byte[] obj, Container context);
    abstract public byte[] _encode( int obj, Container context);
}
