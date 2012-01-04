package construct;
import static construct.Core.*;
import static construct.Adapters.*;

public class Macros {

  /**
  A field consisting of a specified number of bytes.
   * @param name the name of the field
   * @param length the length of the field. the length can be either an integer
    (StaticField), or a function that takes the context as an argument and
    returns the length (MetaField)
   * @return
   */
  static public StaticField Field( String name, int length ){
    
  //  if callable(length):
  //      return MetaField(name, length)
  //  else:
        return new StaticField(name, length);
  }

	/**
	    """
	    BitFields, as the name suggests, are fields that operate on raw, unaligned
	    bits, and therefore must be enclosed in a BitStruct. Using them is very
	    similar to all normal fields: they take a name and a length (in bits).

	    :param str name: name of the field
	    :param int length: number of bits in the field, or a function that takes
	                       the context as its argument and returns the length
	    :param bool swapped: whether the value is byte-swapped
	    :param bool signed: whether the value is signed
	    :param int bytesize: number of bits per byte, for byte-swapping

	    >>> foo = BitStruct("foo",
	    ...     BitField("a", 3),
	    ...     Flag("b"),
	    ...     Padding(3),
	    ...     Nibble("c"),
	    ...     BitField("d", 5),
	    ... )
	    >>> foo.parse("\\xe1\\x1f")
	    Container(a = 7, b = False, c = 8, d = 31)
	    >>> foo = BitStruct("foo",
	    ...     BitField("a", 3),
	    ...     Flag("b"),
	    ...     Padding(3),
	    ...     Nibble("c"),
	    ...     Struct("bar",
	    ...             Nibble("d"),
	    ...             Bit("e"),
	    ...     )
	    ... )
	    >>> foo.parse("\\xe1\\x1f")
	    Container(a = 7, b = False, bar = Container(d = 15, e = 1), c = 8)
	    """
	 * @param name
	 * @param length
	 * @param swapped
	 * @param signed
	 * @param bytesize
	 * @return
	 */
//	static BitIntegerAdapter BitField( String name, int length, boolean swapped, boolean signed, int bytesize ) {
//	    return new BitIntegerAdapter( Field(name, length),
//	        length,
//	        swapped,
//	        signed,
//	        bytesize
//	    );
//	}
  static public Adapter BitField( final String name, final int length, boolean swapped, boolean signed, int bytesize ) {
   return BitIntegerAdapter( Field(name, length),
        length,
        swapped,
        signed,
        bytesize
    );
}
	
	/**
//	    """unsigned, big endian 16-bit integer"""
	 * @param name
	 * @return
	 */
	static FormatField UBInt16(String name){
	    return new FormatField( name, '>', 'H' );
	}
}
