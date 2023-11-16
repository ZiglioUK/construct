package uk.ziglio.construct;
import static uk.ziglio.construct.Adapters.*;
import static uk.ziglio.construct.Core.*;
import static uk.ziglio.construct.fields.Fields.*;
import static uk.ziglio.construct.lib.Binary.*;

import uk.ziglio.construct.adapters.Adapter;
import uk.ziglio.construct.adapters.BitField;
import uk.ziglio.construct.adapters.Bits;
import uk.ziglio.construct.adapters.PaddingAdapter;
import uk.ziglio.construct.core.Buffered;
import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.KeyFunc;
import uk.ziglio.construct.core.MetaArray;
import uk.ziglio.construct.core.Range;
import uk.ziglio.construct.core.Reconfig;
import uk.ziglio.construct.core.Restream;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.core.Subconstruct;
import uk.ziglio.construct.core.Switch;
import uk.ziglio.construct.errors.SizeofError;
import uk.ziglio.construct.fields.FormatField;
import uk.ziglio.construct.fields.StaticField;
import uk.ziglio.construct.interfaces.CountFunc;
import uk.ziglio.construct.interfaces.LengthFunc;
import uk.ziglio.construct.interfaces.ValueFunc;
import uk.ziglio.construct.lib.Resizer;
import uk.ziglio.construct.lib.BitStream.BitStreamReader;
import uk.ziglio.construct.lib.BitStream.BitStreamWriter;
import uk.ziglio.construct.lib.Containers.Container;
import uk.ziglio.construct.macros.CRC;
import uk.ziglio.construct.macros.Embedded;
import uk.ziglio.construct.macros.Enum;
import uk.ziglio.construct.macros.Flag;
import uk.ziglio.construct.macros.IfThenElse;
import uk.ziglio.construct.macros.SBInt16;
import uk.ziglio.construct.macros.SBInt8;
import uk.ziglio.construct.macros.SymmetricMapping;
import uk.ziglio.construct.macros.UBInt16;
import uk.ziglio.construct.macros.UBInt32;
import uk.ziglio.construct.macros.UBInt8;
import uk.ziglio.construct.macros.ULInt16;
import uk.ziglio.construct.macros.ULInt8;

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
  public static Construct Field( String name, LengthFunc length ){
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
  public static Construct Field( String name, int length ){
      return new StaticField(name, length);
  }

  /** Use @len */
  public static Construct Field( String name ){
    return new StaticField(name);
}
 
	public static Bits Bits( final String name, final int length, boolean swapped, boolean signed, int bytesize ) {
    return new Bits( name,
         length,
         swapped,
         signed,
         bytesize
     );
   }

  public static Bits Bits( String name, int length ) {
    return new Bits( name, length, false, false, 8 );
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
  public static PaddingAdapter Padding( int length, byte pattern, boolean strict ){
  		return PaddingAdapter( Field( null, length ), pattern, strict ); 
  }

  public static PaddingAdapter Padding( int length  ){
  		return Padding( length, (byte)0x00, false );
  }

  public static Adapter Flag( String name ){
  		return new Flag( name );
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
  public static Adapter Flag( String name, byte truth, byte falsehood, Object defaultmapping ){
  		return new Flag( name, truth, falsehood, defaultmapping );
  }
  
  /**
  * @return unsigned, big endian 8-bit integer
  */
  public static UBInt8 UBInt8(String name){
   	return new UBInt8( name );
  }
	/**
  * @return unsigned, big endian 16-bit integer
  */
  public static UBInt16 UBInt16(String name){
   	return new UBInt16( name );
  }
	/**
	  * @return unsigned, big endian 32-bit integer
	  */
	  public static FormatField<Integer> UBInt32(String name){
	   	return new UBInt32(name);
	  }
		/**
	  * @return unsigned, big endian 64-bit integer
	  */
	  public static FormatField<Integer> UBInt64(String name){
	   	return new FormatField<Integer>( name, '>', 'Q' );
	  }

  /**
	  * @return signed, big endian 8-bit integer
	  */
	  public static SBInt8 SBInt8(String name){
	   	return new SBInt8( name );
	  }
	  /**
	  * @return signed, big endian 16-bit integer
	  */
	  public static SBInt16 SBInt16(String name){
	   	return new SBInt16( name );
	  }
    /**
	  * @return signed, big endian 32-bit integer
	  */
	  public static FormatField<Integer> SBInt32(String name){
	   	return new FormatField<Integer>( name, '>', 'l' );
	  }
  /**
	  * @return signed, big endian 64-bit integer
	  */
	  public static FormatField<Integer> SBInt64(String name){
	   	return new FormatField<Integer>( name, '>', 'q' );
	  }
	  
  /**
	  * @return unsigned, little endian 8-bit integer
	  */
    public static ULInt8 ULInt8(String name){
      return new ULInt8( name );
    }
    /**
	  * @return unsigned, little endian 16-bit integer
	  */
	  public static ULInt16 ULInt16(String name){
	   	return new ULInt16( name );
	  }
    /**
	  * @return unsigned, little endian 32-bit integer
	  */
	  public static FormatField<Integer> ULInt32(String name){
	   	return new FormatField<Integer>( name, '<', 'L' );
	  }
  /**
	  * @return unsigned, little endian 64-bit integer
	  */
	  public static FormatField<Integer> ULInt64(String name){
	   	return new FormatField<Integer>( name, '<', 'Q' );
	  }
  /**
	  * @return signed, little endian 8-bit integer
	  */
	  public static FormatField<Integer> SLInt8(String name){
	   	return new FormatField<Integer>( name, '<', 'b' );
	  }
  /**
	  * @return signed, little endian 16-bit integer
	  */
	  public static FormatField<Integer> SLInt16(String name){
	   	return new FormatField<Integer>( name, '<', 'h' );
	  }
  /**
	  * @return signed, little endian 32-bit integer
	  */
	  public static FormatField<Integer> SLInt32(String name){
	   	return new FormatField<Integer>( name, '<', 'l' );
	  }
  /**
	  * @return signed, little endian 64-bit integer
	  */
	  public static FormatField<Integer> SLInt64(String name){
	   	return new FormatField<Integer>( name, '<', 'q' );
	  }
  /**
	  * @return unsigned, native endianity 8-bit integer
	  */
	  public static FormatField<Integer> UNInt8(String name){
	   	return new FormatField<Integer>( name, '=', 'B' );
	  }
  /**
	  * @return unsigned, native endianity 16-bit integer
	  */
	  public static FormatField<Integer> UNInt16(String name){
	   	return new FormatField<Integer>( name, '=', 'H' );
	  }
  /**
	  * @return unsigned, native endianity 32-bit integer
	  */
	  public static FormatField<Long> UNInt32(String name){
	   	return new FormatField<Long>( name, '=', 'L' );
	  }
  /**
	  * @return unsigned, native endianity 64-bit integer
	  */
	  public static FormatField<Long> UNInt64(String name){
	   	return new FormatField<Long>( name, '=', 'Q' );
	  }
  /**
	  * @return signed, native endianity 8-bit integer
	  */
	  public static FormatField<Integer> SNInt8(String name){
	   	return new FormatField<Integer>( name, '=', 'b' );
	  }
  /**
	  * @return signed, native endianity 16-bit integer
	  */
	  public static FormatField<Integer> SNInt16(String name){
	   	return new FormatField<Integer>( name, '=', 'h' );
	  }
  /**
	  * @return signed, native endianity 32-bit integer
	  */
	  public static FormatField<Long> SNInt32(String name){
	   	return new FormatField<Long>( name, '=', 'l' );
	  }
  /**
	  * @return signed, native endianity 64-bit integer
	  */
	  public static FormatField<Long> SNInt64(String name){
	   	return new FormatField<Long>( name, '=', 'q' );
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
	MetaArray con = MetaArray( ctx -> count, subcon);
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
                Array( ctx -> (Integer)ctx.get(length_field.name), subcon ))
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
public static Subconstruct Bitwise(Construct subcon) {
	/*
    # subcons larger than MAX_BUFFER will be wrapped by Restream instead
    # of Buffered. implementation details, don't stick your nose in :)*/
    final int MAX_BUFFER = 1024 * 8;

    Subconstruct con;
    Resizer resizer = length -> {
          if( (length & 7) != 0 )
            throw new SizeofError("size must be a multiple of 8, size = " + length );
        return length >> 3;
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

public static Subconstruct Embed( Construct subcon ){
	return Embedded( subcon );
}
/**
 * embeds a struct into the enclosing struct.
 * @param subcon the struct to embed
 * @return
 */
public static <T extends Construct>Embedded<T> Embedded( T subcon ){
	return new Embedded<T>( subcon );
}

/**
 * renames an existing construct
 * @param newname the new name
 * @param subcon the subcon to rename
 */
public static Subconstruct Rename( String newname, Construct subcon ){
	return Reconfig( newname, subcon, subcon.FLAG_EMBED, 0 );
}

/**
 * creates an alias for an existing element in a struct
 * @param newname the new name
 * @param oldname the name of an existing element
 */
public static Construct Alias(String newname, final String oldname){
  return Value( newname, ctx -> ctx.get(oldname));
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
public static CRC CRC(Construct subcon, StaticField crcfield, CRCFunc crcfunc) {
	return new CRC(subcon, crcfield, crcfunc);
}

/**
 * @param subcon the Subconstruct the CRC is calculated on
 * @param keyfunc gets the CRC field from name and context
 * @param crcfunc the function to compute the CRC
 * @return an instance of the CRC Subconstruct
 */
public static CRC CRC(Construct subcon, KeyFunc keyfunc, CRCFunc crcfunc) {
	return new CRC(subcon, keyfunc, crcfunc);
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
public static <T> SymmetricMapping SymmetricMapping( Construct subcon, final Container mapping, T mappingdefault ){
	return new SymmetricMapping( subcon, mapping, mappingdefault );
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
public static Enum Enum( Construct subcon, Object... pairs ){
	return new Enum( subcon, pairs );
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
public static Construct BitStruct( String name, Construct... subcons ){
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
 * A Struct with no name
 * @param subcons
 * @return
 */
public static Embedded<Struct> EmbeddedStruct(Construct... subcons){
    return new Embedded<Struct>( new Struct( null, subcons ));
}

/**
 * an embedded BitStruct. no name is necessary.
 * @param subcons the subcons that make up this structure
 * @return
 */
public static Construct EmbeddedBitStruct(Construct... subcons){
  return Bitwise(EmbeddedStruct(subcons));
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
  public static IfThenElse IfThenElse( String name, KeyFunc keyfunc, Construct then_subcon, Construct else_subcon ){
  	return new IfThenElse( name, keyfunc, then_subcon, else_subcon );
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
  	return IfThenElse( subcon.name, keyfunc, subcon, Value("elsevalue", ctx -> elsevalue));
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

/**
 * """ BitFields, as the name suggests, are fields that operate on raw,
 * unaligned bits, and therefore must be enclosed in a BitStruct. Using them is
 * very similar to all normal fields: they take a name and a length (in bits).
 * 
 * >>> foo = BitStruct("foo", ... BitField("a", 3), ... Flag("b"), ...
 * Padding(3), ... Nibble("c"), ... BitField("d", 5), ... ) >>>
 * foo.parse("\\xe1\\x1f") Container(a = 7, b = False, c = 8, d = 31) >>> foo =
 * BitStruct("foo", ... BitField("a", 3), ... Flag("b"), ... Padding(3), ...
 * Nibble("c"), ... Struct("bar", ... Nibble("d"), ... Bit("e"), ... ) ... ) >>>
 * foo.parse("\\xe1\\x1f") Container(a = 7, b = False, bar = Container(d = 15, e
 * = 1), c = 8) """
 * 
 * @param name     name of the field
 * @param length   number of bits in the field, or a function that takes the
 *                 context as its argument and returns the length
 * @param swapped  whether the value is byte-swapped
 * @param signed   whether the value is signed
 * @param bytesize number of bits per byte, for byte-swapping
 * @return
 */
public static BitField BitField(String name, int length, boolean swapped, boolean signed, int bytesize) {
	return new BitField(name, length, swapped, signed, bytesize);
}

public static BitField BitField(final String name, final int length) {
	return new BitField(name, length);
}

/**
  * @return a 4-bit BitField; must be enclosed in a BitStruct
  */
  public static Adapter Nibble(String name){
  	return BitField( name, 4 );
  }

/**
  * @return an 8-bit BitField; must be enclosed in a BitStruct
  */
  public static Adapter Octet(String name){
  	return BitField( name, 8 );
  }
  
}
