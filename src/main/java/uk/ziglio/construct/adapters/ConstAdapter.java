package uk.ziglio.construct.adapters;

import java.util.Arrays;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.errors.ConstError;
import uk.ziglio.construct.interfaces.AdapterDecoder;
import uk.ziglio.construct.interfaces.AdapterEncoder;
import uk.ziglio.construct.lib.Containers.Container;

public class ConstAdapter extends ExprAdapter {

	/**
	 * @param subcon the subcon to validate
	 * @param value  the expected value
	 * @return Adapter for enforcing a constant value ("magic numbers"). When
	 *         decoding, the return value is checked; when building, the value is
	 *         substituted in. Example: Const(Field("signature", 2), "MZ")
	 */
	@SuppressWarnings("unchecked")
	public ConstAdapter(Construct subcon, final Object value) {
		super(subcon, 
				(obj, context) -> {
			if (obj == null || obj.equals(value))
				return value;
			else
				throw new ConstError("expected " + value + " found " + obj);
		}, 
		(obj, context) -> {
			if (value instanceof byte[] && Arrays.equals((byte[]) obj, (byte[]) value))
				return obj;
			else if (!obj.equals(value))
				throw new ConstError("expected " + value + " found " + obj);
			return obj;
		});
	}
}
