package uk.ziglio.construct.adapters;

import static uk.ziglio.construct.lib.Binary.bin_to_int;
import static uk.ziglio.construct.lib.Binary.int_to_bin;
import static uk.ziglio.construct.lib.Binary.swap_bytes;

import uk.ziglio.construct.Adapter;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.lib.Containers.Container;

public class BitIntegerAdapter extends Adapter {
	final int width;
	final boolean swapped;
	final boolean signed;
	final int bytesize;

	public BitIntegerAdapter(Construct subcon, final int width, final boolean swapped, final boolean signed,
			final int bytesize) {
		super(subcon);
		this.width = width;
		this.swapped = swapped;
		this.signed = signed;
		this.bytesize = bytesize;
	}

	public Object encode(Object obj, Container context) {
		int intobj = (Integer) obj;
		if (intobj < 0 && !signed) {
			throw new BitIntegerError("object is negative, but field is not signed " + intobj);
		}
		byte[] obj2 = int_to_bin(intobj, width);
		if (swapped) {
			obj2 = swap_bytes(obj2, bytesize);
		}
		return obj2;
	}

	public Object decode(Object obj, Container context) {
		byte[] ba = (byte[]) obj;
		if (swapped) {
			ba = swap_bytes(ba, bytesize);
		}
		return bin_to_int(ba, signed);
	}
}