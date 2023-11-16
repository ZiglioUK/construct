package uk.ziglio.construct.adapters;

import uk.ziglio.construct.macros.Macros;

public class BitField extends BitIntegerAdapter {
	public BitField(final String name, final int length, boolean swapped, boolean signed, int bytesize) {
		super(Macros.Field(name, length), length, swapped, signed, bytesize);
	}

	public BitField(final String name, final int length) {
		this(name, length, false, false, 8);
	}
}