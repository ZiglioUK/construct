package construct.core;

import construct.lib.Container;

/**
    """
    Abstract subconstruct (wraps an inner construct, inheriting it's
    name and flags).
    """
 *
 */
public abstract class Subconstruct extends Construct {

	protected Construct subcon;
	
	/**
	 * @param name
	 * @param subcon the construct to wrap
	 */
	public Subconstruct( Construct subcon ) {
        super( subcon.name, subcon.conflags );
		this.subcon = subcon;
	}

	@Override
	public Object _parse(String stream, Container context) {
        return subcon._parse(stream, context);
	}

	@Override
	protected void _build(byte[] obj, StringBuilder stream, Container context) {
        subcon._build(obj, stream, context);
	}

//    def _sizeof(self, context):
//        return self.subcon._sizeof(context)
}
