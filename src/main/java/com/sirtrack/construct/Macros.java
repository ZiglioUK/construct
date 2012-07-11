package com.sirtrack.construct;
import static com.sirtrack.construct.Adapters.*;
import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.lib.Binary.*;
import static com.sirtrack.construct.lib.Containers.*;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Core.Adapter;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.CountFunc;
import com.sirtrack.construct.Core.StaticField;
import com.sirtrack.construct.Core.Subconstruct;
import com.sirtrack.construct.Core.ValueFunc;
import com.sirtrack.construct.lib.BitStream.BitStreamReader;
import com.sirtrack.construct.lib.BitStream.BitStreamWriter;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Resizer;
import com.sirtrack.construct.lib.Containers.Container;

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
  static public Construct Field( String name, LengthFunc length ){
    return MetaField(name, length);
  }
  /**
  A field consisting of a specified number of bytes.
   * @param name the name of the field
   * @param length the length of the field. the length can be either an integer
    (StaticField), or a function that takes the context as an argument and
    returns the length (MetaField)
   * @return
   */
  static public Construct Field( String name, int length ){
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
  													Container( true, truth, false, falsehood ),
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
  static public FormatField UBInt8(String name){
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
# arrays
#===============================================================================
 */


/**
  Repeats the given unit a fixed number of times.
  >>> c = Array(4, UBInt8("foo"))
  >>> c.parse("\\x01\\x02\\x03\\x04")
  [1, 2, 3, 4]
  >>> c.parse("\\x01\\x02\\x03\\x04\\x05\\x06")
  [1, 2, 3, 4]
  >>> c.build([5,6,7,8])
  '\\x05\\x06\\x07\\x08'
  >>> c.build([5,6,7,8,9])
  Traceback (most recent call last):
    ...
  construct.core.RangeError: expected 4..4, found 5
 * @param countfunc a function that takes the context as a parameter and returns
      the number of elements of the array (count)
 * @param subcon construct to repeat
 */
public static MetaArray Array( CountFunc countfunc, Construct subcon){
  return new MetaArray(countfunc, subcon);
}

/**
  Repeats the given unit a fixed number of times.
 * @param count number of times to repeat
 * @param subcon construct to repeat
 */
public static MetaArray Array( final int count, Construct subcon ){
	MetaArray con = MetaArray( new CountFunc(){
		public int count(Container context) {
			return count;
		}
  }, subcon);
  con._clear_flag(con.FLAG_DYNAMIC);
	return con;
}

/**
 * @param subcon the subcon to be repeated
 * @param length_field a construct returning an integer
 * @return an array prefixed by a length field.
 */
public static Adapter PrefixedArray( Construct subcon, final StaticField length_field ){
  return LengthValueAdapter(
      Sequence( subcon.name,
                length_field,
                Array( new CountFunc(){
									public int count(Container ctx) {
										return (Integer)ctx.get(length_field.name);
									}},
                  subcon
                ))
//                nested = False
      );
}

public static Adapter PrefixedArray( Construct subcon ){
	return PrefixedArray( subcon, UBInt8("length") );
}

public static Range OpenRange(int mincount, Construct subcon){
	return Range( mincount, Integer.MAX_VALUE, subcon);
}

/**
 *  Repeats the given unit one or more times.
    >>> from construct import GreedyRange, UBInt8
    >>> c = GreedyRange(UBInt8("foo"))
    >>> c.parse("\\x01")
    [1]
    >>> c.parse("\\x01\\x02\\x03")
    [1, 2, 3]
    >>> c.parse("\\x01\\x02\\x03\\x04\\x05\\x06")
    [1, 2, 3, 4, 5, 6]
    >>> c.parse("")
    Traceback (most recent call last):
      ...
    construct.core.RangeError: expected 1..2147483647, found 0
    >>> c.build([1,2])
    '\\x01\\x02'
    >>> c.build([])
    Traceback (most recent call last):
      ...
    construct.core.RangeError: expected 1..2147483647, found 0
    """

    return OpenRange(1, subcon)
 * @param subcon ``Construct`` subcon: construct to repeat
 */
public static Range GreedyRange(Construct subcon){
	return OpenRange( 1, subcon );
}

/**
  Repeats the given unit zero or more times. This repeater can't
  fail, as it accepts lists of any length.

  :param ``Construct`` subcon: 

  >>> from construct import OptionalGreedyRange, UBInt8
  >>> c = OptionalGreedyRange(UBInt8("foo"))
  >>> c.parse("")
  []
  >>> c.parse("\\x01\\x02")
  [1, 2]
  >>> c.build([])
  ''
  >>> c.build([1,2])
  '\\x01\\x02'
 * @param subcon construct to repeat
 */
public static Range OptionalGreedyRange(Construct subcon){
	return OpenRange( 0, subcon );
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
            throw new SizeofError("size must be a multiple of 8, size = " + length );
        return length >> 3;
      }
    };
    if( !subcon._is_flag(subcon.FLAG_DYNAMIC) && subcon.sizeof() < MAX_BUFFER ){
      con = new Buffered( subcon,
              						BinaryEncoder(),
              						BinaryDecoder(),
              						resizer );
    } else {
      con = new Restream( subcon,
                          new BitStreamReader(),
                          new BitStreamWriter(),
                          resizer);
    }
    return con;
}

static public Subconstruct Embed( Construct subcon ){
	return Embedded( subcon );
}
/**
 * embeds a struct into the enclosing struct.
 * @param subcon the struct to embed
 * @return
 */
static public Subconstruct Embedded( Construct subcon ){
	return Reconfig( subcon.name, subcon, subcon.FLAG_EMBED, 0 );
}

/**
 * renames an existing construct
 * @param newname the new name
 * @param subcon the subcon to rename
 */
static public Subconstruct Rename( String newname, Construct subcon ){
	return Reconfig( newname, subcon, subcon.FLAG_EMBED, 0 );
}

/**
 * creates an alias for an existing element in a struct
 * @param newname the new name
 * @param oldname the name of an existing element
 */
static public Construct Alias(String newname, final String oldname){
  return Value( newname, new ValueFunc(){
  	public Object get(Container ctx) {
  		return ctx.get(oldname);
  }});
}

/**
 * You can pass your CRC implementation here
 * Use check and/or compute when parsing and building
 */
public static interface CRCFunc {
	int compute(byte[] data);

	boolean check(byte[] data, int crcval);
}

/**
 * @param subcon the Subconstruct the CRC is calculated on
 * @param crcfield the field that contains the computed CRC
 * @param crcfunc the function to compute the CRC
 * @return an instance of the CRC Subconstruct
 */
static public CRC CRC(Construct subcon, StaticField crcfield, CRCFunc crcfunc) {
	return new CRC(subcon, crcfield, crcfunc);
}

static public class CRC extends Subconstruct {
	CRCFunc crcfunc;
	StaticField crcfield;

	public CRC(Construct subcon, StaticField crcfield, CRCFunc crcfunc) {
		super(subcon);
		this.crcfield = crcfield;
		this.crcfunc = crcfunc;
	}

	@Override
	public Object _parse(ByteBufferWrapper stream, Container context) {
		byte[] data = _read_stream(stream, _sizeof(context));
		int crcval = (Integer) crcfield._parse(stream, context);
		boolean crccheck = crcfunc.check(data, crcval);

		if (crccheck) {
			Container c = (Container)(subcon._parse(new ByteBufferWrapper().wrap( data ), context));
			c.set(crcfield.name, crccheck); // set CRC value to true
			return c;
		}
		else{
			return Container(
					crcfield.name, crccheck, 				// set CRC value to false, could throw an Exception instead
					crcfield.name + " data", data 	// return also invalid data
			);
		}
	}

	@Override
	protected void _build(Object obj, ByteArrayOutputStream stream, Container context) {
//		 TODO needs testing
		 ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
		 subcon.build_stream(obj, stream2);
		 byte[] data = stream2.toByteArray();
		 int size = _sizeof(context);
		 if( data.length != size )
		 throw new RuntimeException( "Wrong data length: " + data.length );
		
		 int crcval = crcfunc.compute(data);
		 _write_stream(stream, size, data);
		 crcfield.build_stream(crcval, stream);
	}

	@Override
	protected int _sizeof(Container context) {
		return subcon.sizeof(context);
	}
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
static public Adapter Enum( Construct subcon, Object... pairs ){
	// we could do some static type checks, making sure that names are String
	// and that the size of values matches the size of subcon
	// Let's keep things simple for now
	final Container kw = Container(pairs);
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

/**
 * an embedded BitStruct. no name is necessary.
 * @param subcons the subcons that make up this structure
 * @return
 */
static public Construct EmbeddedBitStruct(Construct... subcons){
  return Bitwise(Embedded(Struct( null, subcons)));
}
/*
#===============================================================================
# conditional
#===============================================================================
*/
  /**
   * an if-then-else conditional construct: if the predicate indicates True,
      `then_subcon` will be used; otherwise `else_subcon`
     @param name the name of the construct
   * @param keyfunc a function taking the context as an argument and returning
   * @param then_subcon the subcon that will be used if the predicate returns True
   * @param else_subcon the subcon that will be used if the predicate returns False
   */
  public static Switch IfThenElse( String name, KeyFunc keyfunc, Construct then_subcon, Construct else_subcon ){
  	return Switch( name, keyfunc, true, then_subcon, false, else_subcon );
  }

  /**
   * @param keyfunc a function taking the context as an argument and returning
      True or False
   * @param subcon the subcon that will be used if the predicate returns True
   * @param elsevalue the value that will be used should the predicate return False.
   * @return an if-then conditional construct: if the predicate indicates True,
    subcon will be used; otherwise, `elsevalue` will be returned instead.
   */
  public static Switch If( KeyFunc keyfunc, Construct subcon, final Object elsevalue ){
  	return IfThenElse( subcon.name, keyfunc, subcon, Value("elsevalue", new ValueFunc(){
      public Object get(Container ctx) {
	      return elsevalue;
      }}));
  }

  /**
   * @param keyfunc a function taking the context as an argument and returning
      True or False
   * @param subcon the subcon that will be used if the predicate returns True
   * @return an if-then conditional construct: if the predicate indicates True,
    subcon will be used; otherwise, null will be returned instead.
   */
  public static Switch If( KeyFunc keyfunc, Construct subcon ){
  	return If( keyfunc, subcon, null );
  }
  
}
