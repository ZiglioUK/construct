package construct;
import static construct.Core.*;
import static construct.Adapters.*;
import static construct.lib.Binary.*;
import construct.Core.Construct;
import construct.lib.Container;
import construct.lib.Resizer;

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
  static public Construct Field( String name, int length ){
    
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
   
  /**
   * Bits is just an alias for BitField
   */
  static public Adapter Bits( final String name, final int length ) {
     return BitField( name, length, false, false, 8 );
  }

  static public Adapter Bits( final String name, final int length, boolean swapped, boolean signed, int bytesize ) {
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
  /**
   * @param length the length of the field. the length can be either an integer,
      or a function that takes the context as an argument and returns the
      length
   * @param pattern the padding pattern (character) to use. default is "\x00"
   * @param strict whether or not to raise an exception is the actual padding
      pattern mismatches the desired pattern. default is False.
   * @return a padding field (value is discarded)
   */
  static public Adapter Padding( int length, byte pattern, boolean strict ){
  	return PaddingAdapter( Field( null, length ), pattern, strict ); 
  }
  static public Adapter Padding( int length  ){
  	return Padding( length, (byte)0x00, false );
  }

  static public Adapter Flag( String name ){
  	return Flag( name, (byte)1, (byte)0, false );
  }
  
  /**
   * @param name field name
   * @param truth value of truth (default 1)
   * @param falsehood value of falsehood (default 0)
   * @param defaultmapping default value (default False)
   * @return     A flag.

    Flags are usually used to signify a Boolean value, and this construct
    maps values onto the ``bool`` type.

    .. note:: This construct works with both bit and byte contexts.

    .. warning:: Flags default to False, not True. This is different from the
        C and Python way of thinking about truth, and may be subject to change
        in the future.

   */
  static public Adapter Flag( String name, byte truth, byte falsehood, Object defaultmapping ){

  	return SymmetricMapping(Field(name, 1),
  													Container( P(true, truth), P(false, falsehood )),
  													defaultmapping );
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
	  static public FormatField UBInt16(String name){
	   	return new FormatField( name, '>', 'H' );
	  }
  /**
	  * @return unsigned, big endian 32-bit integer
	  */
	  static public FormatField UBInt32(String name){
	   	return new FormatField( name, '>', 'L' );
	  }

  /**
	  * @return unsigned, big endian 64-bit integer
	  */
	  static public FormatField UBInt64(String name){
	   	return new FormatField( name, '>', 'Q' );
	  }

  /**
	  * @return signed, big endian 8-bit integer
	  */
	  static public FormatField SBInt8(String name){
	   	return new FormatField( name, '>', 'b' );
	  }
  /**
	  * @return signed, big endian 16-bit integer
	  */
	  static public FormatField SBInt16(String name){
	   	return new FormatField( name, '>', 'h' );
	  }
  /**
	  * @return signed, big endian 32-bit integer
	  */
	  static public FormatField SBInt32(String name){
	   	return new FormatField( name, '>', 'l' );
	  }
  /**
	  * @return signed, big endian 64-bit integer
	  */
	  static public FormatField SBInt64(String name){
	   	return new FormatField( name, '>', 'q' );
	  }
  /**
	  * @return unsigned, little endian 8-bit integer
	  */
	  static public FormatField ULInt8(String name){
	   	return new FormatField( name, '<', 'B' );
	  }
  /**
	  * @return unsigned, little endian 16-bit integer
	  */
	  static public FormatField ULInt16(String name){
	   	return new FormatField( name, '<', 'H' );
	  }
  /**
	  * @return unsigned, little endian 32-bit integer
	  */
	  static public FormatField ULInt32(String name){
	   	return new FormatField( name, '<', 'L' );
	  }
  /**
	  * @return unsigned, little endian 64-bit integer
	  */
	  static public FormatField ULInt64(String name){
	   	return new FormatField( name, '<', 'Q' );
	  }
  /**
	  * @return signed, little endian 8-bit integer
	  */
	  static public FormatField SLInt8(String name){
	   	return new FormatField( name, '<', 'b' );
	  }
  /**
	  * @return signed, little endian 16-bit integer
	  */
	  static public FormatField SLInt16(String name){
	   	return new FormatField( name, '<', 'h' );
	  }
  /**
	  * @return signed, little endian 32-bit integer
	  */
	  static public FormatField SLInt32(String name){
	   	return new FormatField( name, '<', 'l' );
	  }
  /**
	  * @return signed, little endian 64-bit integer
	  */
	  static public FormatField SLInt64(String name){
	   	return new FormatField( name, '<', 'q' );
	  }
  /**
	  * @return unsigned, native endianity 8-bit integer
	  */
	  static public FormatField UNInt8(String name){
	   	return new FormatField( name, '=', 'B' );
	  }
  /**
	  * @return unsigned, native endianity 16-bit integer
	  */
	  static public FormatField UNInt16(String name){
	   	return new FormatField( name, '=', 'H' );
	  }
  /**
	  * @return unsigned, native endianity 32-bit integer
	  */
	  static public FormatField UNInt32(String name){
	   	return new FormatField( name, '=', 'L' );
	  }
  /**
	  * @return unsigned, native endianity 64-bit integer
	  */
	  static public FormatField UNInt64(String name){
	   	return new FormatField( name, '=', 'Q' );
	  }
  /**
	  * @return signed, native endianity 8-bit integer
	  */
	  static public FormatField SNInt8(String name){
	   	return new FormatField( name, '=', 'b' );
	  }
  /**
	  * @return signed, native endianity 16-bit integer
	  */
	  static public FormatField SNInt16(String name){
	   	return new FormatField( name, '=', 'h' );
	  }
  /**
	  * @return signed, native endianity 32-bit integer
	  */
	  static public FormatField SNInt32(String name){
	   	return new FormatField( name, '=', 'l' );
	  }
  /**
	  * @return signed, native endianity 64-bit integer
	  */
	  static public FormatField SNInt64(String name){
	   	return new FormatField( name, '=', 'q' );
	  }
/*
#===============================================================================
# subconstructs
#===============================================================================
*/
	  
/**
 * converts the stream to bits, and passes the bitstream to subcon
 * @param subcon a bitwise construct (usually BitField)
 * @return
 */
static public Subconstruct Bitwise(Construct subcon) {
	/*
    # subcons larger than MAX_BUFFER will be wrapped by Restream instead
    # of Buffered. implementation details, don't stick your nose in :)*/
    final int MAX_BUFFER = 1024 * 8;

    Subconstruct con;
    Resizer resizer = new Resizer(){
			@Override
      public int resize(int length) {
          if( (length & 7) != 0 )
            throw new SizeofError("size must be a multiple of 8 " + length );
        return length >> 3;
      }
    };
    if( !subcon._is_flag(subcon.FLAG_DYNAMIC) && subcon.sizeof() < MAX_BUFFER ){
      con = new Buffered( subcon,
              						BinaryEncoder(),
              						BinaryDecoder(),
              						resizer
          							);
    } else {
    	throw new RuntimeException("unimplemented");
//      con = Restream(subcon,
//          stream_reader = BitStreamReader,
//          stream_writer = BitStreamWriter,
//          resizer = resizer)
    }
    return con;
}

/*
#===============================================================================
# mapping
#===============================================================================
*/
/**
 * @param subcon the subcon to map
 * @param mapping the encoding mapping (a dict); the decoding mapping is
      achieved by reversing this mapping
 * @param mappingdefault the default value to use when no mapping is found. if no
      default value is given, and exception is raised. setting to Pass would
      return the value "as is" (unmapped)
 * @return a symmetrical mapping: a->b, b->a.
 */
static public Adapter SymmetricMapping( Construct subcon, final Container mapping, Object mappingdefault ){
	return MappingAdapter( subcon, mapping.reverse(), mapping, mappingdefault, mappingdefault );
}

/**
 * @param subcon the subcon to map
 * @param mapping keyword arguments which serve as the encoding mapping
 * @param _default_ vn optional, keyword-only argument that specifies the
      default value to use when the mapping is undefined. if not given,
      and exception is raised when the mapping is undefined. use `Pass` to
      pass the unmapped value as-is
 * @return a set of named values mapping.
 */
static public Adapter Enum( Construct subcon, final Container kw ){
	return SymmetricMapping( subcon, kw, kw.get("_default_") );
}

//  return SymmetricMapping(subcon, kw, kw.pop(, NotImplemented));
/*
#===============================================================================
# structs
#===============================================================================
*/
/**
 * @param name the name of the struct
 * @param subcons the subcons that make up this structure
 * @return a struct of bitwise fields
 */
static public Construct BitStruct( String name, Construct... subcons ){
  return Bitwise(Struct(name, subcons));
}
	  /*
	  		def AlignedStruct(name, *subcons, **kw):
	  		    """a struct of aligned fields
	  		    * name - the name of the struct
	  		    * subcons - the subcons that make up this structure
	  		    * kw - keyword arguments to pass to Aligned: 'modulus' and 'pattern'
	  		    """
	  		    return Struct(name, *(Aligned(sc, **kw) for sc in subcons))

	  		def EmbeddedBitStruct(*subcons):
	  		    """an embedded BitStruct. no name is necessary.
	  		    * subcons - the subcons that make up this structure
	  		    """
	  		    return Bitwise(Embedded(Struct(None, *subcons)))
*/
}
