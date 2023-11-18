package uk.ziglio.construct;

import static uk.ziglio.construct.Macros.Field;
import static uk.ziglio.construct.lib.Binary.byteArrayToHexString;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import uk.ziglio.construct.adapters.*;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.interfaces.AdapterDecoder;
import uk.ziglio.construct.interfaces.AdapterEncoder;
import uk.ziglio.construct.lib.Containers.Container;

public class Adapters {
	/**
	 * """ Adapter for bit-integers (converts bitstrings to integers, and vice
	 * versa). See BitField.
	 * 
	 * Parameters: subcon - the subcon to adapt width - the size of the subcon, in
	 * bits swapped - whether to swap byte order (little endian/big endian). default
	 * is False (big endian) signed - whether the value is signed (two's
	 * complement). the default is False (unsigned) bytesize - number of bits per
	 * byte, used for byte-swapping (if swapped). default is 8. """
	 * 
	 */
	static public Adapter BitIntegerAdapter(Construct subcon, final int width) {
		return new BitIntegerAdapter(subcon, width, false, false, 8);
	}

	static public Adapter BitIntegerAdapter(Construct subcon, final int width, final boolean swapped,
			final boolean signed) {
		return new BitIntegerAdapter(subcon, width, swapped, signed, 8);
	}

	static public Adapter BitIntegerAdapter(Construct subcon, final int width, final boolean swapped,
			final boolean signed, final int bytesize) {
		return new BitIntegerAdapter(subcon, width, swapped, signed, bytesize);
	}

	/**
	 * @param subcon     the subcon to map
	 * @param decoding   the decoding (parsing) mapping (a dict)
	 * @param encoding   the encoding (building) mapping (a dict)
	 * @param decdefault the default return value when the object is not found in
	 *                   the decoding mapping. if no object is given, an exception
	 *                   is raised. if `Pass` is used, the unmapped object will be
	 *                   passed as-is
	 * @param encdefault the default return value when the object is not found in
	 *                   the encoding mapping. if no object is given, an exception
	 *                   is raised. if `Pass` is used, the unmapped object will be
	 *                   passed as-is
	 * @return Adapter that maps objects to other objects. See SymmetricMapping and
	 *         Enum.
	 */
	static public MappingAdapter MappingAdapter(Construct subcon, Container decoding, Container encoding,
			Object decdefault, Object encdefault) {
		return new MappingAdapter(subcon, decoding, encoding, decdefault, encdefault);
	}

	// public static int getLength( Object obj ){
	// if( obj instanceof String)
	// return ((String)obj).length();
	// else if( obj instanceof Arrays )
	//
	// }
	/**
	 * @param subcon the subcon returning a length-value pair
	 * @return Adapter for length-value pairs. It extracts only the value from the
	 *         pair, and calculates the length based on the value. See PrefixedArray
	 *         and PascalString.
	 */
	public static Adapter<Object, List> LengthValueAdapter(Construct subcon) {
		return ExprAdapter(subcon, (obj, context) -> {
			List l = new ArrayList();
			l.add(Construct.getDataLength(obj));
			l.add(obj);
			return l;
		}, (l, context) -> {
			return l.get(1);
		});
	}

	/**
	 * @param subcon the subcon to validate
	 * @param value  the expected value
	 * @return Adapter for enforcing a constant value ("magic numbers"). When
	 *         decoding, the return value is checked; when building, the value is
	 *         substituted in. Example: Const(Field("signature", 2), "MZ")
	 */
	static public Adapter ConstAdapter(Construct subcon, final Object value) {
		return new ConstAdapter(subcon, value);	
	}

	/**
	 * @param subcon the subcon to validate
	 * @param value  the expected value
	 * @return Adapter for enforcing a constant value ("magic numbers"). When
	 *         decoding, the return value is checked; when building, the value is
	 *         substituted in. Example: Const(Field("signature", 2), "MZ")
	 */
	static public Adapter Const(Construct subcon, final Object value) {
		return ConstAdapter(subcon, value);
	}

	/**
	 * A 'magic number' construct. it is used for file signatures, etc., to validate
	 * that the given pattern exists.
	 * 
	 * Example::
	 * 
	 * elf_header = Struct("elf_header", Magic("\x7fELF"),
	 * 
	 * @param data
	 * @return
	 */
	public static Adapter Magic(String data) {
		return ConstAdapter(Field(null, data.length()), data.getBytes());
	}

	/**
	 * Adapter for hex-dumping strings. It returns a HexString, which is a string
	 */
	static public Adapter<String, byte[]> HexDumpAdapter(Construct subcon) {
		return HexDumpAdapter(subcon, 16);
	}

	static public Adapter<String, byte[]> HexDumpAdapter(Construct subcon, final int linesize) {
		return ExprAdapter(subcon, (str, context) -> {
			str = str.replaceAll("[\n ]", "");
			return hexStringToByteArray(str);
		}, (ba, context) -> byteArrayToHexString(ba, 16));
	}

	/*
	 * class MappingAdapter(Adapter): def _encode(self, obj, context): def
	 * _decode(self, obj, context):
	 */
	static public PaddingAdapter PaddingAdapter(Construct subcon) {
		return new PaddingAdapter(subcon);
	}

	static public PaddingAdapter PaddingAdapter(Construct subcon, byte pattern, boolean strict) {
		return new PaddingAdapter(subcon, pattern, strict);
	}

	/**
	 * A generic adapter that accepts 'encoder' and 'decoder' as parameters. You can
	 * use ExprAdapter instead of writing a full-blown class when only a simple
	 * expression is needed. Example: ExprAdapter(UBInt8("foo"), encoder = lambda
	 * obj, ctx: obj / 4, decoder = lambda obj, ctx: obj * 4, )
	 * 
	 * @param subcon  the subcon to adapt
	 * @param encoder a function that takes (obj, context) and returns an encoded
	 *                version of obj
	 * @param decoder a function that takes (obj, context) and returns an decoded
	 *                version of obj
	 */
	public static <V, T> Adapter<V, T> ExprAdapter(Construct subcon, AdapterEncoder<V, T> encoder,
			AdapterDecoder<T, V> decoder) {
		return new ExprAdapter<V, T>(subcon, encoder, decoder);
	};

	/**
	 * >>> OneOf(UBInt8("foo"), [4,5,6,7]).parse("\\x05") 5 >>> OneOf(UBInt8("foo"),
	 * [4,5,6,7]).parse("\\x08") Traceback (most recent call last): ...
	 * construct.core.ValidationError: ('invalid object', 8) >>> >>>
	 * OneOf(UBInt8("foo"), [4,5,6,7]).build(5) '\\x05' >>> OneOf(UBInt8("foo"),
	 * [4,5,6,7]).build(9) Traceback (most recent call last): ...
	 * construct.core.ValidationError: ('invalid object', 9) Validates that the
	 * object is one of the listed values.
	 * 
	 * @param subcon object to validate
	 * @param valids a set of valid values
	 */
	public static ValidatorAdapter OneOf(Construct subcon, final List valids) {
		return new ValidatorAdapter(subcon, (Object obj, Container context) -> valids.contains(obj));
	}

	/***************************************************************
	 * Miscellaneous Adapters
	 ****************************************************************/

	/**
	 * @param name
	 * @return an IPv4 Address Adapter
	 */
	public static Adapter<InetAddress, byte[]> IpAddress(String name) {
		return IpAddressAdapter(Field(name, 4));
	}

	/**
	 * @param name
	 * @return an IPv6 Address Adapter
	 */
	public static Adapter<InetAddress, byte[]> Ipv6Address(String name) {
		return IpAddressAdapter(Field(name, 16));
	}

	public static Adapter<InetAddress, byte[]> IpAddressAdapter(Construct field) {
		return new ExprAdapter<InetAddress, byte[]>(field, (obj, context) -> obj.getAddress(), (obj, context) -> {
			try {
				return InetAddress.getByAddress(obj);
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		});
	};

	public static <T> BeanAdapter<T> BeanAdapter(Class<T> clazz, Construct subcon) {
		return new BeanAdapter<T>(clazz, subcon);
	}
}