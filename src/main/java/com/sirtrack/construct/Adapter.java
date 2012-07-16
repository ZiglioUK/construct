package com.sirtrack.construct;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.Subconstruct;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Containers.Container;

/**
 * """ Abstract adapter: calls _decode for parsing and _encode for building. """
 * 
 */
public abstract class Adapter extends Subconstruct implements AdapterEncoder, AdapterDecoder {
	/**
	 * @param name
	 * @param subcon
	 *          the construct to wrap
	 */
	public Adapter(Construct subcon) {
		super(subcon);
	}

	@Override
	public Object _parse( ByteBufferWrapper stream, Container context) {
		return decode(subcon._parse( stream, context ), context);
	}

	public void _build(Object obj, ByteArrayOutputStream stream, Container context) {
		subcon._build(encode(obj, context), stream, context);
	}

	abstract public Object decode(Object obj, Container context);
	abstract public Object encode(Object obj, Container context);

}
