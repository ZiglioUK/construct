package construct;
import static construct.Core.*;
import static construct.Adapters.*;

public class Macros {

/*
	#===============================================================================
	# fields
	#===============================================================================
*/
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
	 * @param name name of the field
	 * @param length number of bits in the field, or a function that takes
	                       the context as its argument and returns the length
	 * @param swapped whether the value is byte-swapped
	 * @param signed whether the value is signed
	 * @param bytesize number of bits per byte, for byte-swapping
	 * @return
	 */
  static public Adapter BitField( final String name, final int length, boolean swapped, boolean signed, int bytesize ) {
   return BitIntegerAdapter( Field(name, length),
        length,
        swapped,
        signed,
        bytesize
    );
  }
   
  static public Adapter BitField( final String name, final int length ) {
     return BitField( name, length, false, false, 8 );
  }

/*
  #===============================================================================
	# field shortcuts
	#===============================================================================
*/
  /**
  * @return a 1-bit BitField; must be enclosed in a BitStruct
  */
  static public Adapter Bit(String name){
  	return BitField( name, 1 );
  }
  /**
  * @return a 4-bit BitField; must be enclosed in a BitStruct
  */
  static public Adapter Nibble(String name){
  	return BitField( name, 4 );
  }
  /**
  * @return an 8-bit BitField; must be enclosed in a BitStruct
  */
  static public Adapter Octet(String name){
  	return BitField( name, 8 );
  }
  /**
  * @return unsigned, big endian 8-bit integer
  */
  static FormatField UBInt8(String name){
   	return new FormatField( name, '>', 'B' );
  }
  /**
	  * @return unsigned, big endian 16-bit integer
	  */
	  static FormatField UBInt16(String name){
	   	return new FormatField( name, '>', 'H' );
	  }
  /**
	  * @return unsigned, big endian 32-bit integer
	  */
	  static FormatField UBInt32(String name){
	   	return new FormatField( name, '>', 'L' );
	  }

  /**
	  * @return unsigned, big endian 64-bit integer
	  */
	  static FormatField UBInt64(String name){
	   	return new FormatField( name, '>', 'Q' );
	  }

  /**
	  * @return signed, big endian 8-bit integer
	  */
	  static FormatField SBInt8(String name){
	   	return new FormatField( name, '>', 'b' );
	  }
  /**
	  * @return signed, big endian 16-bit integer
	  */
	  static FormatField SBInt16(String name){
	   	return new FormatField( name, '>', 'h' );
	  }
  /**
	  * @return signed, big endian 32-bit integer
	  */
	  static FormatField SBInt32(String name){
	   	return new FormatField( name, '>', 'l' );
	  }
  /**
	  * @return signed, big endian 64-bit integer
	  */
	  static FormatField SBInt64(String name){
	   	return new FormatField( name, '>', 'q' );
	  }
  /**
	  * @return unsigned, little endian 8-bit integer
	  */
	  static FormatField ULInt8(String name){
	   	return new FormatField( name, '<', 'B' );
	  }
  /**
	  * @return unsigned, little endian 16-bit integer
	  */
	  static FormatField ULInt16(String name){
	   	return new FormatField( name, '<', 'H' );
	  }
  /**
	  * @return unsigned, little endian 32-bit integer
	  */
	  static FormatField ULInt32(String name){
	   	return new FormatField( name, '<', 'L' );
	  }
  /**
	  * @return unsigned, little endian 64-bit integer
	  */
	  static FormatField ULInt64(String name){
	   	return new FormatField( name, '<', 'Q' );
	  }
  /**
	  * @return signed, little endian 8-bit integer
	  */
	  static FormatField SLInt8(String name){
	   	return new FormatField( name, '<', 'b' );
	  }
  /**
	  * @return signed, little endian 16-bit integer
	  */
	  static FormatField SLInt16(String name){
	   	return new FormatField( name, '<', 'h' );
	  }
  /**
	  * @return signed, little endian 32-bit integer
	  */
	  static FormatField SLInt32(String name){
	   	return new FormatField( name, '<', 'l' );
	  }
  /**
	  * @return signed, little endian 64-bit integer
	  */
	  static FormatField SLInt64(String name){
	   	return new FormatField( name, '<', 'q' );
	  }
  /**
	  * @return unsigned, native endianity 8-bit integer
	  */
	  static FormatField UNInt8(String name){
	   	return new FormatField( name, '=', 'B' );
	  }
  /**
	  * @return unsigned, native endianity 16-bit integer
	  */
	  static FormatField UNInt16(String name){
	   	return new FormatField( name, '=', 'H' );
	  }
  /**
	  * @return unsigned, native endianity 32-bit integer
	  */
	  static FormatField UNInt32(String name){
	   	return new FormatField( name, '=', 'L' );
	  }
  /**
	  * @return unsigned, native endianity 64-bit integer
	  */
	  static FormatField UNInt64(String name){
	   	return new FormatField( name, '=', 'Q' );
	  }
  /**
	  * @return signed, native endianity 8-bit integer
	  */
	  static FormatField SNInt8(String name){
	   	return new FormatField( name, '=', 'b' );
	  }
  /**
	  * @return signed, native endianity 16-bit integer
	  */
	  static FormatField SNInt16(String name){
	   	return new FormatField( name, '=', 'h' );
	  }
  /**
	  * @return signed, native endianity 32-bit integer
	  */
	  static FormatField SNInt32(String name){
	   	return new FormatField( name, '=', 'l' );
	  }
  /**
	  * @return signed, native endianity 64-bit integer
	  */
	  static FormatField SNInt64(String name){
	   	return new FormatField( name, '=', 'q' );
	  }
}
