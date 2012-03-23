package com.sirtrack.construct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.sirtrack.construct.lib.*;
import com.sirtrack.construct.lib.Containers.Container;

import static com.sirtrack.construct.lib.Containers.*;

public class Core {
  public static class FieldError extends RuntimeException {
    public FieldError(String string) {
      super(string);
    }
  }
  public static class SizeofError extends RuntimeException {
    public SizeofError(String string) {
      super(string);
    }
  }
  public static class ValueError extends RuntimeException {
    public ValueError(String string) {
      super(string);
    }
  }
  public static class RangeError extends RuntimeException {
    public RangeError(String string) {
      super(string);
    }
  }
  public static class TypeError extends RuntimeException {
    public TypeError(String string) {
      super(string);
    }
  }
  public static class SwitchError extends RuntimeException {
    public SwitchError(String string) {
      super(string);
    }
  }
  public static class ArrayError extends RuntimeException {
    public ArrayError(String string, Exception e) {
      super(string, e);
    }
  }
  
/*
#===============================================================================
# Shorthand expressions
#===============================================================================
*/
//  	  Bits = BitField
//  		Byte = UBInt8
//  		Bytes = Field
//  		Const = ConstAdapter
//  		Tunnel = TunnelAdapter
//  		Embed = Embedded
static public byte[] ByteArray( int... ints ){
	byte[] ba = new byte[ints.length];
	int k = 0;
	for( int i : ints ){
		ba[k++] = (byte)i;
	}
	return ba;
}

static public byte[] ByteArray( byte[]... bas ){
	ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	for( byte[]ba : bas ){
		try {
	    out.write(ba);
    } catch (IOException e) {
	    throw new RuntimeException(e);
    }
	}
	return out.toByteArray();
}

/**
  A generic container of attributes.

  Containers are the common way to express parsed data.
 */
static public Container Container( Object... pairs ){
	return new Container( pairs );
}
  
/*
 * #===============================================================================
 * # abstract constructs
 * #===============================================================================
 */

/**
  The mother of all constructs.

  This object is generally not directly instantiated, and it does not
  directly implement parsing and building, so it is largely only of interest
  to subclass implementors.

  The external user API:

   * parse()
   * parse_stream()
   * build()
   * build_stream()
   * sizeof()

  Subclass authors should not override the external methods. Instead,
  another API is available:

   * _parse()
   * _build()
   * _sizeof()

  There is also a flag API:

   * _set_flag()
   * _clear_flag()
   * _inherit_flags()
   * _is_flag()

  And stateful copying:

   * __getstate__()
   * __setstate__()

  Attributes and Inheritance
  ==========================

  All constructs have a name and flags. The name is used for naming struct
  members and context dictionaries. Note that the name can either be a
  string, or None if the name is not needed. A single underscore ("_") is a
  reserved name, and so are names starting with a less-than character ("<").
  The name should be descriptive, short, and valid as a Python identifier,
  although these rules are not enforced.

  The flags specify additional behavioral information about this construct.
  Flags are used by enclosing constructs to determine a proper course of
  action. Flags are inherited by default, from inner subconstructs to outer
  constructs. The enclosing construct may set new flags or clear existing
  ones, as necessary.

  For example, if FLAG_COPY_CONTEXT is set, repeaters will pass a copy of
  the context for each iteration, which is necessary for OnDemand parsing.
*/
	static public abstract class Construct {
		
    public static final int FLAG_COPY_CONTEXT          = 0x0001;
    public static final int FLAG_DYNAMIC               = 0x0002;
    public static final int FLAG_EMBED                 = 0x0004;
    public static final int FLAG_NESTING               = 0x0008;
		
		int conflags;
		public String name;

		public Construct(String name) {
			this( name, 0 );
		}

		public Construct(String name, int flags) {
      if( name != null ){
  			if (name.equals("_") || name.startsWith("<"))
  				throw new ValueError("reserved name " + name); // raise
      }
			this.name = name;
			this.conflags = flags;
		}
		
		@Override
		public String toString(){
			return getClass().getName() + "(" + name + ")";
		}
		/**
        Set the given flag or flags.
		 * @param flag flag to set; may be OR'd combination of flags
		 */
		protected void _set_flag(int flag){
			conflags |= flag;
		}
		
		/**
        Clear the given flag or flags.
		 * @param flag flag to clear; may be OR'd combination of flags
		 */
		protected void _clear_flag( int flag ){
			conflags &= ~flag;
		}
		
		/**Pull flags from subconstructs.*/
		protected void _inherit_flags( Construct... subcons ){
			for( Construct sc : subcons ){
				_set_flag(sc.conflags);
			}
		}
		
		/**
        Check whether a given flag is set.
		 * @param flag flag to check
		 * @return
		 */
		protected boolean _is_flag( int flag ){
			return (conflags & flag) == flag;
		}

		public byte[] _read_stream( ByteBuffer stream, int length) {
			if (length < 0)
				throw new FieldError("length must be >= 0 " + length);
			{
				int len = stream.remaining();
				if (len < length)
					throw new FieldError("expected " + length + " found " + len);
				byte[] out = new byte[length];
				stream.get(out, 0, length);
				return out;
			}
		}

		static public int getDataLength( Object data ){
			if( data instanceof String)
				return ((String)data).length();
			else if( data instanceof Byte )
				return 1;
			else if( data instanceof Integer ){
				int num = (Integer)data;
				if( num < 256 )
					return 1;
				else if( num < 65536 )
					return 2;
  			else
  				return 4;
//  				return Integer.SIZE/8;
			} else if( data instanceof byte[] )
				return ((byte[])data).length;
			else return -1;
		}

		static public void appendDataStream( ByteArrayOutputStream stream, Object data ){
			if( data instanceof String)
	      try {
	        stream.write(((String)data).getBytes());
        } catch (IOException e) {
        	throw new ValueError( "Can't append data " + data + " " + e.getMessage());
        }
      else if( data instanceof Byte )
				stream.write((Byte)data);
			else if( data instanceof Integer )
				stream.write((Integer)data);
			else if( data instanceof byte[] )
	      try {
	        stream.write((byte[])data);
        } catch (IOException e) {
        	throw new ValueError( "Can't append data " + data + " " + e.getMessage());
        }
      else throw new ValueError( "Can't append data " + data);
		}
		
		public void _write_stream( ByteArrayOutputStream stream, int length, Object data) {
			if (length < 0)
				throw new FieldError("length must be >= 0 " + length);

			int datalength = getDataLength( data );
			if ( length != datalength )
				throw new FieldError("expected " + length + " found " + datalength);

			appendDataStream( stream, data );
		};

		/**
		 * Parse an in-memory buffer.
		 * 
		 * Strings, buffers, memoryviews, and other complete buffers can be parsed with this method.
		 * 
		 * @param data
		 */
		public <T>T parse(byte[] data) {
			return (T)parse_stream( ByteBuffer.wrap( data ));
		}

		public <T>T parse(String text) {
			return (T)parse_stream( ByteBuffer.wrap( text.getBytes() ));
		}

		/**
		 * Parse a stream.
		 * 
		 * Files, pipes, sockets, and other streaming sources of data are handled by this method.
		 */
		public Object parse_stream( ByteBuffer stream) {
			return _parse(stream, new Container());
		}

		abstract public Object _parse( ByteBuffer stream, Container context);

		/**
		 * Build an object in memory.
		 * 
		 * @param obj
		 * @return
		 */
		public byte[] build( Object obj) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			build_stream(obj, stream);

			return stream.toByteArray();
		}

		/**
		 * Build an object directly into a stream.
		 * 
		 * @param obj
		 * @param stream
		 */
		public void build_stream( Object obj, ByteArrayOutputStream stream) {
			_build(obj, stream, new Container());
		}

		// abstract void _build( String obj, OutputStream stream, Container
		// context);
		protected abstract void _build( Object obj, ByteArrayOutputStream stream, Container context);

		/**
		 * Calculate the size of this object, optionally using a context. Some constructs have no fixed size and can only know their size for a given hunk of data;
		 * these constructs will raise an error if they are not passed a context.
		 * 
		 * @param context
		 *          contextual data
		 * @return the length of this construct
		 */
		public int sizeof(Container context) {
			if (context == null) {
				context = new Container();
			}
			try {
				return _sizeof(context);
			} catch (Exception e) {
				throw new SizeofError(e.getMessage());
			}
		}

		public int sizeof() {
			return sizeof(null);
		}

		abstract protected int _sizeof(Container context);
	}

	/** 
	 * Abstract subconstruct (wraps an inner construct, inheriting its name and flags). 
	 */
	public static abstract class Subconstruct extends Construct {

		protected Construct subcon;

		/**
		 * @param subcon the construct to wrap
		 */
		public Subconstruct(Construct subcon) {
			super(subcon.name, subcon.conflags);
			this.subcon = subcon;
		}

		protected Subconstruct(String name, Construct subcon) {
			super(name, subcon.conflags);
			this.subcon = subcon;
    }

		@Override
		public Object _parse( ByteBuffer stream, Container context) {
			return subcon._parse(stream, context);
		}

		@Override
		protected void _build( Object obj, ByteArrayOutputStream stream, Container context) {
			subcon._build(obj, stream, context);
		}

		@Override
		protected int _sizeof(Container context){
			return subcon._sizeof(context);
		}
	}

	
/*
#===============================================================================
# arrays and repeaters
#===============================================================================
 */

	public static interface AdapterEncoder{
		public Object encode(Object obj, Container context);
	}
	
	public static interface AdapterDecoder{
		public Object decode(Object obj, Container context);
  }

	/**
   * """ Abstract adapter: calls _decode for parsing and _encode for building. """
   * 
   */
  public static abstract class Adapter extends Subconstruct implements AdapterEncoder, AdapterDecoder {
  	/**
  	 * @param name
  	 * @param subcon
  	 *          the construct to wrap
  	 */
  	public Adapter(Construct subcon) {
  		super(subcon);
  	}
  
  	@Override
  	public Object _parse( ByteBuffer stream, Container context) {
  		return decode(subcon._parse( stream, context ), context);
  	}
  
  	public void _build(Object obj, ByteArrayOutputStream stream, Container context) {
  		subcon._build(encode(obj, context), stream, context);
  	}
  
  	abstract public Object decode(Object obj, Container context);
  	abstract public Object encode(Object obj, Container context);

  }

/*
 * ===============================================================================
 * * Fields
 * ===============================================================================
 */
  
  	/**
  	 * A fixed-size byte field.
  	 */
  	public static class StaticField extends Construct {
  		int length;
  
  		/**
  		 * @param name
  		 *          field name
  		 * @param length
  		 *          number of bytes in the field
  		 */
  		public StaticField(String name, int length) {
  			super(name);
  			this.length = length;
  		}
  
  		@Override
  		public Object _parse( ByteBuffer stream, Container context) {
  			return _read_stream( stream, length);
  		}
  
  		@Override
  		protected void _build( Object obj, ByteArrayOutputStream stream, Container context) {
  			_write_stream(stream, length, obj);
  		}
  		
  		@Override
      protected int _sizeof(Container context) {
  			return length;
      }
  
  		/*
  		  * public int _sizeof( Container context ){ return length; }
  		  */
  	}
	/**
   * A field that uses ``struct`` to pack and unpack data.
   * 
   * See ``struct`` documentation for instructions on crafting format strings.
   */
  public static class FormatField extends StaticField {
  	int length;
  	Packer packer;
  
  	/**
  	 * @param name
  	 *          name of the field
  	 * @param endianness
  	 *          : format endianness string; one of "<", ">", or "="
  	 * @param format
  	 *          : a single format character
  	 */
  	public FormatField(String name, char endianity, char format) {
  		super(name, 0);
  
  		if (endianity != '>' && endianity != '<' && endianity != '=')
  			throw new ValueError("endianity must be be '=', '<', or '>' " + endianity);
  
  		packer = new Packer(endianity, format);
  		super.length = packer.length();
  
  	}
  
  	@Override
  	public Object _parse( ByteBuffer stream, Container context ) {
  		try {
  			return packer.unpack(stream);
  		} catch (Exception e) {
  			throw new FieldError(e.getMessage());
  		}
  	}
  
  	@Override
  	public void _build( Object obj, ByteArrayOutputStream stream, Container context) {
  		_write_stream(stream, super.length, packer.pack(obj));
  	}
  
  }

	/**
   * callable that takes a context and returns length as an int	 
  */
  static public interface LengthFunc{
  	 abstract int length(Container context);
  }

	/**
    A variable-length field. The length is obtained at runtime from a
    function.
    >>> foo = Struct("foo",
    ...     Byte("length"),
    ...     MetaField("data", lambda ctx: ctx["length"])
    ... )
    >>> foo.parse("\\x03ABC")
    Container(data = 'ABC', length = 3)
    >>> foo.parse("\\x04ABCD")
    Container(data = 'ABCD', length = 4)
  	 * @param name name of the field
  	 * @param lengthfunc callable that takes a context and returns
                                length as an int
   */
  public static MetaField MetaField(String name, LengthFunc lengthfunc ){
  	return new MetaField(name, lengthfunc);
  }

	public static class MetaField extends Construct {
  
  	LengthFunc lengthfunc;
  	
  	/**
  	 * @param name name of the field
  	 * @param lengthfunc callable that takes a context and returns
                                length as an int
  	 */
  	public MetaField(String name, LengthFunc lengthfunc) {
      super(name);
      this.lengthfunc = lengthfunc;
      this._set_flag(FLAG_DYNAMIC);
    }
  
  
  	@Override
    public Object _parse(ByteBuffer stream, Container context) {
      return _read_stream(stream, lengthfunc.length(context));
    }
  
  	@Override
    protected void _build(Object obj, ByteArrayOutputStream stream, Container context) {
  		_write_stream(stream, lengthfunc.length(context), obj);
    }
  
  	@Override
    protected int _sizeof(Container context) {
      return lengthfunc.length(context);
    }
  	
  }

/*	
#===============================================================================
# arrays and repeaters
#===============================================================================
*/

	/**
   * callable that takes a context and returns length as an int	 
  */
  static public interface CountFunc{
  	 abstract int count(Container context);
  }

  /**
  Example:
  MetaArray(lambda ctx: 5, UBInt8("foo"))
  See also Array, Range and RepeatUntil.

	 * @param countfunc a function that takes the context as a parameter and returns
      the number of elements of the array (count)
	 * @param subcon the subcon to repeat `countfunc()` times
	 * @return An array (repeater) of a meta-count. The array will iterate exactly
  `countfunc()` times. Will raise ArrayError if less elements are found.
	 */
	public static MetaArray MetaArray( CountFunc countfunc, Construct subcon){
		return new MetaArray(countfunc,subcon);
	}

/**
    An array (repeater) of a meta-count. The array will iterate exactly
    `countfunc()` times. Will raise ArrayError if less elements are found.
    See also Array, Range and RepeatUntil.

    Example:
    MetaArray(lambda ctx: 5, UBInt8("foo"))

 */
public static class MetaArray extends Subconstruct{

	CountFunc countfunc;
	
	/**
    Parameters:
    * countfunc - a function that takes the context as a parameter and returns
      the number of elements of the array (count)
    * subcon - the subcon to repeat `countfunc()` times
	 * @param length
	 * @param name
	 * @param subcon
	 */
	protected MetaArray( CountFunc countfunc, Construct subcon) {
	  	super(subcon);
      this.countfunc = countfunc;
      _clear_flag(FLAG_COPY_CONTEXT);
      _set_flag(FLAG_DYNAMIC);
  }

	@Override
	public Object _parse( ByteBuffer stream, Container context) {
    List obj = ListContainer();
    int c = 0;
    int count = countfunc.count(context);
    try{
        if( (subcon.conflags & FLAG_COPY_CONTEXT) != 0){
            while( c < count ){
                obj.add( subcon._parse(stream, context.clone()));
                c += 1;
            }
        }
        else{
            while( c < count ){
                obj.add( subcon._parse(stream, context) );
                c += 1;
            }
        }
    }
    catch( Exception e ){
        throw new ArrayError("expected " + count +", found " + c, e );
    }
    return obj;
	}

	@Override
	protected void _build( Object object, ByteArrayOutputStream stream, Container context) {
		
		List<Object> obj = (List<Object>)object;

		int count = countfunc.count(context);
	  
		if( obj.size() != count ){
	  	throw new ArrayError("expected " + count +", found " + obj.size(), null );
	  }
	  
		if( (subcon.conflags & FLAG_COPY_CONTEXT) != 0 ){
      for( Object subobj : obj ){
	          subcon._build(subobj, stream, context.clone());
	      }
		}
	  else{
      for( Object subobj : obj ){
	          subcon._build(subobj, stream, context);
      }
	  }
	}
		
	@Override
	protected int _sizeof(Container context){
    return subcon._sizeof(context) * countfunc.count(context);
	}
		
}	

	public static Range Range(int mincount, int maxcount, Construct subcon){
		return new Range(mincount,maxcount,subcon);
	}
/**
    A range-array. The subcon will iterate between `mincount` to `maxcount`
    times. If less than `mincount` elements are found, raises RangeError.
    See also GreedyRange and OptionalGreedyRange.

    The general-case repeater. Repeats the given unit for at least mincount
    times, and up to maxcount times. If an exception occurs (EOF, validation
    error), the repeater exits. If less than mincount units have been
    successfully parsed, a RangeError is raised.

    .. note::
       This object requires a seekable stream for parsing.
 */
public static class Range extends Subconstruct{

	/**
	 * @param mincount the minimal count
	 * @param maxcount the maximal count
	 * @param subcon the subcon to repeat
    >>> c = Range(3, 7, UBInt8("foo"))
    >>> c.parse("\\x01\\x02")
    Traceback (most recent call last):
      ...
    construct.core.RangeError: expected 3..7, found 2
    >>> c.parse("\\x01\\x02\\x03")
    [1, 2, 3]
    >>> c.parse("\\x01\\x02\\x03\\x04\\x05\\x06")
    [1, 2, 3, 4, 5, 6]
    >>> c.parse("\\x01\\x02\\x03\\x04\\x05\\x06\\x07")
    [1, 2, 3, 4, 5, 6, 7]
    >>> c.parse("\\x01\\x02\\x03\\x04\\x05\\x06\\x07\\x08\\x09")
    [1, 2, 3, 4, 5, 6, 7]
    >>> c.build([1,2])
    Traceback (most recent call last):
      ...
    construct.core.RangeError: expected 3..7, found 2
    >>> c.build([1,2,3,4])
    '\\x01\\x02\\x03\\x04'
    >>> c.build([1,2,3,4,5,6,7,8])
    Traceback (most recent call last):
      ...
    construct.core.RangeError: expected 3..7, found 8
	 */
	int mincount;
	int maxcout;

	public Range(int mincount, int maxcount, Construct subcon) {
		super(subcon);
		this.mincount = mincount;
		this.maxcout = maxcount;
		_clear_flag(FLAG_COPY_CONTEXT);
		_set_flag(FLAG_DYNAMIC);
	}
	
	@Override
	public Object _parse( ByteBuffer stream, Container context) {
//    obj = ListContainer()
		List<Object> obj = ListContainer();
		int c = 0;
		int pos = stream.position();
		try{
      if( (subcon.conflags & FLAG_COPY_CONTEXT) != 0 ){
        while( c < maxcout ){
            pos = stream.position();
            obj.add( subcon._parse(stream, context.clone() ));
            c += 1;
        }
      } else {
        while( c < maxcout ){
          pos = stream.position();
          obj.add( subcon._parse(stream, context ));
          c += 1;
        }
      }
		}
    catch( Exception e ){
      if( c < mincount ){
        throw new RangeError("expected " + mincount + " to " + maxcout + " found " + c + " " + e.getMessage() );
      }
      stream.position(pos);
    }
		
		return obj;
	}

	@Override
	protected void _build( Object object, ByteArrayOutputStream stream, Container context) {

		if( !(object instanceof List ))
			throw new TypeError( "Expected object array" );
		List<Object> obj = (List<Object>)object;
		if( obj.size() < mincount || obj.size() > maxcout ){
      throw new RangeError("expected " + mincount + " to " + maxcout + " found " + obj.size() );
		}
		
		int cnt = 0;
		try{
      if( (subcon.conflags & FLAG_COPY_CONTEXT) != 0 ){
        for( Object subobj : obj ){
          subcon._build(subobj, stream, context.clone() );
          cnt += 1;
        }
      } else {
        for( Object subobj : obj ){
          subcon._build(subobj, stream, context );
          cnt += 1;
        }
      }
		}
    catch( Exception e ){
        throw new RangeError( e.getMessage() );
     }
	}

	@Override
	protected int _sizeof(Container context){
    throw new SizeofError("can't calculate size");
	}
	
}
	
/*
* #===============================================================================
* # structures and sequences
* #===============================================================================
*/
	/**
    A sequence of named constructs, similar to structs in C. The elements are
    parsed and built in the order they are defined.
    See also Embedded.
    Example:
    Struct("foo",
        UBInt8("first_element"),
        UBInt16("second_element"),
        Padding(2),
        UBInt8("third_element"),
    )
	 */
	static public Struct Struct(String name, Construct... subcons){
		return new Struct( name, subcons );
	}
	static public class Struct extends Construct{
		public boolean nested = true;
		Construct[] subcons;
		/**
		 * @param name the name of the structure
		 * @param subcons a sequence of subconstructs that make up this structure.
		 */
		public Struct(String name, Construct... subcons) {
	    super(name);
	    this.subcons = subcons;
	    _inherit_flags(subcons);
	    _clear_flag(FLAG_EMBED);
	  }

		@Override
		public Object _parse( ByteBuffer stream, Container context) {
			
			Container obj;
			if( context.contains("<obj>")){
				obj = context.get("<obj>");
				context.del("<obj>");
			} else{
				obj = new Container();
				if( nested ){
					context = Container( "_", context );
				}
			}

			for( Construct sc: subcons ){
				if( (sc.conflags & FLAG_EMBED) != 0 ){
					context.set("<obj>", obj);
					sc._parse(stream, context);
				} else {
				  Object subobj = sc._parse(stream, context);
					if( sc.name != null ){
						obj.set( sc.name, subobj );
						context.set( sc.name, subobj );
					}
				}
			}
			return obj;
    }

		@Override
    protected void _build( Object obj, ByteArrayOutputStream stream, Container context ) {
			if( context.contains("<unnested>")){
				context.del("<unnested>");
			} else if( nested ){
				context = Container( "_", context );
			}
			for( Construct sc: subcons){
				Object subobj;
				if( (sc.conflags & FLAG_EMBED) != 0 ){
					context.set( "<unnested>", true );
					subobj = obj;
				} else if( sc.name == null ){
					subobj = null;
				} else if( obj instanceof Container ){
					Container container = (Container)obj;
					subobj = container.get( sc.name );
					
					if( subobj == null )
						throw new FieldError( "No field found: " + sc.name + " in " + subobj );
					
					context.set(sc.name, subobj);
				} else
						continue;
				
				sc._build(subobj, stream, context);
			}
    }

		@Override
    protected int _sizeof(Container context) {
        int sum = 0;
				if( nested )
            context = Container( "_", context );
        
        for( Construct sc: subcons ){
        	sum += sc._sizeof(context);
        }
        
        return sum;
    }
	}

	
	/**
	 * @param name the name of the structure
	 * @param subcons a sequence of subconstructs that make up this structure.
	 * @param nested: a keyword-only argument that indicates whether this struct
      creates a nested context. The default is True. This parameter is
      considered "advanced usage", and may be removed in the future.
	 * @return A sequence of unnamed constructs. The elements are parsed and built in the
    order they are defined.
    See also Embedded.
    Example:
    Sequence("foo",
        UBInt8("first_element"),
        UBInt16("second_element"),
        Padding(2),
        UBInt8("third_element"),
    )
	 */
	public static Sequence Sequence(String name, Construct... subcons){
		return new Sequence( name, subcons);
	}
	
	public static class Sequence extends Struct{
		public Sequence(String name, Construct... subcons ) {
	    super(name, subcons);
	  }
		
		@Override
		public Object _parse( ByteBuffer stream, Container context) {
      List obj;
			if( context.contains( "<obj>" )){
        obj = context.get( "<obj>" );
        context.del("<obj>");
      }
      else{
        obj = ListContainer();
        if( nested ){
            context = Container( "_", context );
        }
      }
      for( Construct sc: subcons ){
        if(( sc.conflags & FLAG_EMBED ) != 0 ){
            context.set( "<obj>", obj );
            sc._parse(stream, context);
        }
        else{
            Object subobj = sc._parse(stream, context);
            if( sc.name != null ){
                obj.add(subobj);
                context.set(sc.name, subobj);
            }
        }
      }
      return obj;
		}

		@Override
    protected void _build( Object obj, ByteArrayOutputStream stream, Container context ) {
      if( context.contains("<unnested>")) {
        context.del("<unnested>");
      }
      else if( nested ){
        context = Container( "_", context);
      }
      
      Object subobj;
      ListIterator objiter;
      if( obj instanceof List )
      	objiter = ((List)obj).listIterator();
      else
      	objiter = (ListIterator)obj;
      
      for( Construct sc: subcons ){
        if(( sc.conflags & FLAG_EMBED ) != 0 ){
            context.set( "<unnested>", true );
            subobj = objiter;
        }
        else if( sc.name == null ){
            subobj = null;
        }
        else {
            subobj = objiter.next();
            context.set( sc.name, subobj );
        }
        sc._build(subobj, stream, context);
		}
	 }
	}

	/*
#===============================================================================
# conditional
#===============================================================================
*/
	
	public static Construct NoDefault = new Construct( null ){

			@Override
      public Object _parse(ByteBuffer stream, Container context) {
				throw new SwitchError("no default case defined");
      }

			@Override
      protected void _build(Object obj, ByteArrayOutputStream stream, com.sirtrack.construct.lib.Containers.Container context) {
				throw new SwitchError("no default case defined");
	      
      }

			@Override
      protected int _sizeof(com.sirtrack.construct.lib.Containers.Container context) {
				throw new SwitchError("no default case defined");
      }
	};

	/**
   * a function that takes the context and returns a key	 
  */
  public static interface KeyFunc{
  	 Object key(Container context);
  }
  
  /**
  A conditional branch. Switch will choose the case to follow based on
  the return value of keyfunc. If no case is matched, and no default value
  is given, SwitchError will be raised.
  See also Pass.
  Example:
  Struct("foo",
      UBInt8("type"),
      Switch("value", lambda ctx: ctx.type, {
              1 : UBInt8("spam"),
              2 : UBInt16("spam"),
              3 : UBInt32("spam"),
              4 : UBInt64("spam"),
          }
      ),
  )
 * @param name the name of the construct
 * @param keyfunc a function that takes the context and returns a key, which
    will ne used to choose the relevant case.
 * @param cases a dictionary mapping keys to constructs. the keys can be any
    values that may be returned by keyfunc.
 */
public static Switch Switch(String name, KeyFunc keyfunc, Object... cases  ) {
	return new Switch( name,  keyfunc,  Container(cases),  NoDefault,  false );
}

/**
    A conditional branch. Switch will choose the case to follow based on
    the return value of keyfunc. If no case is matched, and no default value
    is given, SwitchError will be raised.
    See also Pass.
    Example:
    Struct("foo",
        UBInt8("type"),
        Switch("value", lambda ctx: ctx.type, {
                1 : UBInt8("spam"),
                2 : UBInt16("spam"),
                3 : UBInt32("spam"),
                4 : UBInt64("spam"),
            }
        ),
    )
	 * @param name the name of the construct
	 * @param keyfunc a function that takes the context and returns a key, which
      will ne used to choose the relevant case.
	 * @param cases a dictionary mapping keys to constructs. the keys can be any
      values that may be returned by keyfunc.
	 * @param defaultval a default value to use when the key is not found in the cases.
      if not supplied, an exception will be raised when the key is not found.
      You can use the builtin construct Pass for 'do-nothing'.
	 * @param include_key whether or not to include the key in the return value
      of parsing. defualt is False.
   */
  public static Switch Switch(String name, KeyFunc keyfunc, Container cases, Construct defaultval, boolean include_key ) {
  	return new Switch( name,  keyfunc,  cases,  defaultval,  include_key );
  }

/**
    A conditional branch. Switch will choose the case to follow based on
    the return value of keyfunc. If no case is matched, and no default value
    is given, SwitchError will be raised.
    See also Pass.
    Example:
    Struct("foo",
        UBInt8("type"),
        Switch("value", lambda ctx: ctx.type, {
                1 : UBInt8("spam"),
                2 : UBInt16("spam"),
                3 : UBInt32("spam"),
                4 : UBInt64("spam"),
            }
        ),
    )
 */
public static class Switch extends Construct{
	/**
	 * a function that takes the context and returns a key, which
      will ne used to choose the relevant case.
	 */
	KeyFunc keyfunc;
	Container cases;
	Construct defaultval;
	boolean include_key;

	/**
	 * @param name the name of the construct
	 * @param keyfunc a function that takes the context and returns a key, which
      will ne used to choose the relevant case.
	 * @param cases a dictionary mapping keys to constructs. the keys can be any
      values that may be returned by keyfunc.
	 * @param defaultval a default value to use when the key is not found in the cases.
      if not supplied, an exception will be raised when the key is not found.
      You can use the builtin construct Pass for 'do-nothing'.
	 * @param include_key whether or not to include the key in the return value
      of parsing. defualt is False.
	 */
	public Switch(String name, KeyFunc keyfunc, Container cases, Construct defaultval, boolean include_key ) {
	  super(name);
	  this.keyfunc = keyfunc;
	  this.cases = cases;
	  this.defaultval = defaultval;
	  this.include_key = include_key;
	  Construct[] ca = cases.values( Construct.class );
	  this._inherit_flags(ca);
	  this._set_flag(FLAG_DYNAMIC);
	}
	
	@Override
  public Object _parse(ByteBuffer stream, Container context) {
		Object key = keyfunc.key(context);
		Construct c = cases.get(key, defaultval);
		Object obj = c._parse(stream, context);
		if( include_key ){
			return Container( key, obj );
		} else {
			return obj;
		}
  }

	@Override
  protected void _build(Object obj, ByteArrayOutputStream stream, Container context) {
		Object key;
		if( include_key ){
			List list = (List)obj;
			key = list.get(0);
			obj = list.get(1);
		} else {
			key = keyfunc.key( context );
		}
		
		Construct casestruct = cases.get(key, defaultval);
		casestruct._build(obj, stream, context);
/*
        if self.include_key:
            key, obj = obj
        else:
            key = self.keyfunc(context)
        case = self.cases.get(key, self.default)
        case._build(obj, stream, context)
 */
	}
	@Override
  protected int _sizeof( Container context) {
		Construct casestruct = cases.get(keyfunc.key( context ), defaultval);
		return casestruct._sizeof(context);
  }
}

/*
#===============================================================================
# stream manipulation
#===============================================================================
*/
	/**
    Creates an in-memory buffered stream, which can undergo encoding and
    decoding prior to being passed on to the subconstruct.
    See also Bitwise.

    Note:
    * Do not use pointers inside Buffered

    Example:
    Buffered(BitField("foo", 16),
        encoder = decode_bin,
        decoder = encode_bin,
        resizer = lambda size: size / 8,
    )
	 */
	static public class Buffered extends Subconstruct{
		Encoder encoder;
		Decoder decoder;
		Resizer resizer;
		/**
		 * @param subcon the subcon which will operate on the buffer
		 * @param encoder a function that takes a string and returns an encoded
      string (used after building)
		 * @param decoder a function that takes a string and returns a decoded
      string (used before parsing)
		 * @param resizer a function that takes the size of the subcon and "adjusts"
      or "resizes" it according to the encoding/decoding process.
		 */
		public Buffered( Construct subcon, Encoder encoder, Decoder decoder, Resizer resizer ) {
	    super(subcon);
	    this.encoder = encoder;
	    this.decoder = decoder;
	    this.resizer = resizer;
    }
		@Override
		public Object _parse( ByteBuffer stream, Container context) {
      byte[] data = _read_stream(stream, _sizeof(context));
      byte[] stream2 = decoder.decode(data);
			return subcon._parse(ByteBuffer.wrap( stream2 ), context);
		}

		@Override
		protected void _build( Object obj, ByteArrayOutputStream stream, Container context) {
			int size = _sizeof(context);
			ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
			subcon._build(obj, stream2, context);
			byte[] data = encoder.encode(stream2.toString());
			if( data.length != size )
				throw new RuntimeException( "Wrong data length: " + data.length );
			_write_stream(stream, size, data);
		}
		@Override
    protected int _sizeof(Container context) {
			return resizer.resize( subcon._sizeof(context));
    }
	}
/*
class Pointer(Subconstruct):
    """
    Changes the stream position to a given offset, where the construction
    should take place, and restores the stream position when finished.
    See also Anchor, OnDemand and OnDemandPointer.

    Notes:
    * requires a seekable stream.

    Parameters:
    * offsetfunc: a function that takes the context and returns an absolute
      stream position, where the construction would take place
    * subcon - the subcon to use at `offsetfunc()`

    Example:
    Struct("foo",
        UBInt32("spam_pointer"),
        Pointer(lambda ctx: ctx.spam_pointer,
            Array(5, UBInt8("spam"))
        )
    )
    """
    __slots__ = ["offsetfunc"]
    def __init__(self, offsetfunc, subcon):
        Subconstruct.__init__(self, subcon)
        self.offsetfunc = offsetfunc
    def _parse(self, stream, context):
        newpos = self.offsetfunc(context)
        origpos = stream.tell()
        stream.seek(newpos)
        obj = self.subcon._parse(stream, context)
        stream.seek(origpos)
        return obj
    def _build(self, obj, stream, context):
        newpos = self.offsetfunc(context)
        origpos = stream.tell()
        stream.seek(newpos)
        self.subcon._build(obj, stream, context)
        stream.seek(origpos)
    def _sizeof(self, context):
        return 0

class Peek(Subconstruct):
    """
    Peeks at the stream: parses without changing the stream position.
    See also Union. If the end of the stream is reached when peeking,
    returns None.

    Notes:
    * requires a seekable stream.

    Parameters:
    * subcon - the subcon to peek at
    * perform_build - whether or not to perform building. by default this
      parameter is set to False, meaning building is a no-op.

    Example:
    Peek(UBInt8("foo"))
    """
    __slots__ = ["perform_build"]
    def __init__(self, subcon, perform_build = False):
        Subconstruct.__init__(self, subcon)
        self.perform_build = perform_build
    def _parse(self, stream, context):
        pos = stream.tell()
        try:
            return self.subcon._parse(stream, context)
        except FieldError:
            pass
        finally:
            stream.seek(pos)
    def _build(self, obj, stream, context):
        if self.perform_build:
            self.subcon._build(obj, stream, context)
    def _sizeof(self, context):
        return 0

class OnDemand(Subconstruct):
    """
    Allows for on-demand (lazy) parsing. When parsing, it will return a
    LazyContainer that represents a pointer to the data, but does not actually
    parses it from stream until it's "demanded".
    By accessing the 'value' property of LazyContainers, you will demand the
    data from the stream. The data will be parsed and cached for later use.
    You can use the 'has_value' property to know whether the data has already
    been demanded.
    See also OnDemandPointer.

    Notes:
    * requires a seekable stream.

    Parameters:
    * subcon -
    * advance_stream - whether or not to advance the stream position. by
      default this is True, but if subcon is a pointer, this should be False.
    * force_build - whether or not to force build. If set to False, and the
      LazyContainer has not been demaned, building is a no-op.

    Example:
    OnDemand(Array(10000, UBInt8("foo"))
    """
    __slots__ = ["advance_stream", "force_build"]
    def __init__(self, subcon, advance_stream = True, force_build = True):
        Subconstruct.__init__(self, subcon)
        self.advance_stream = advance_stream
        self.force_build = force_build
    def _parse(self, stream, context):
        obj = LazyContainer(self.subcon, stream, stream.tell(), context)
        if self.advance_stream:
            stream.seek(self.subcon._sizeof(context), 1)
        return obj
    def _build(self, obj, stream, context):
        if not isinstance(obj, LazyContainer):
            self.subcon._build(obj, stream, context)
        elif self.force_build or obj.has_value:
            self.subcon._build(obj.value, stream, context)
        elif self.advance_stream:
            stream.seek(self.subcon._sizeof(context), 1)

class Restream(Subconstruct):
    """
    Wraps the stream with a read-wrapper (for parsing) or a
    write-wrapper (for building). The stream wrapper can buffer the data
    internally, reading it from- or writing it to the underlying stream
    as needed. For example, BitStreamReader reads whole bytes from the
    underlying stream, but returns them as individual bits.
    See also Bitwise.

    When the parsing or building is done, the stream's close method
    will be invoked. It can perform any finalization needed for the stream
    wrapper, but it must not close the underlying stream.

    Note:
    * Do not use pointers inside Restream

    Parameters:
    * subcon - the subcon
    * stream_reader - the read-wrapper
    * stream_writer - the write wrapper
    * resizer - a function that takes the size of the subcon and "adjusts"
      or "resizes" it according to the encoding/decoding process.

    Example:
    Restream(BitField("foo", 16),
        stream_reader = BitStreamReader,
        stream_writer = BitStreamWriter,
        resizer = lambda size: size / 8,
    )
    """
    __slots__ = ["stream_reader", "stream_writer", "resizer"]
    def __init__(self, subcon, stream_reader, stream_writer, resizer):
        Subconstruct.__init__(self, subcon)
        self.stream_reader = stream_reader
        self.stream_writer = stream_writer
        self.resizer = resizer
    def _parse(self, stream, context):
        stream2 = self.stream_reader(stream)
        obj = self.subcon._parse(stream2, context)
        stream2.close()
        return obj
    def _build(self, obj, stream, context):
        stream2 = self.stream_writer(stream)
        self.subcon._build(obj, stream2, context)
        stream2.close()
    def _sizeof(self, context):
        return self.resizer(self.subcon._sizeof(context))
 */
	
/*
#===============================================================================
# miscellaneous
#===============================================================================
*/

	/**
	 * @param name the new name
	 * @param subcon the subcon to reconfigure
	 * @param setflags the flags to set (default is 0)
	 * @param clearflags the flags to clear (default is 0)
	 */
	static public Reconfig Reconfig(String name, Construct subcon ) {
		return new Reconfig(name, subcon);
	}

	/**
	 * @param name the new name
	 * @param subcon the subcon to reconfigure
	 * @param setflags the flags to set (default is 0)
	 * @param clearflags the flags to clear (default is 0)
	 */
	static public Reconfig Reconfig(String name, Construct subcon, int setflags, int clearflags ) {
		return new Reconfig(name, subcon, setflags, clearflags);
	}
/**
    Reconfigures a subconstruct. Reconfig can be used to change the name and
    set and clear flags of the inner subcon.
    Example:
    Reconfig("foo", UBInt8("bar"))
 */
static public class Reconfig extends Subconstruct{

	/**
	 * @param name the new name
	 * @param subcon the subcon to reconfigure
	 * @param setflags the flags to set (default is 0)
	 * @param clearflags the flags to clear (default is 0)
	 */
	public Reconfig(String name, Construct subcon, int setflags, int clearflags ) {
	  super(name, subcon);
	  _set_flag(setflags);
	  _clear_flag(clearflags);
  }

	public Reconfig(String name, Construct subcon ) {
	  this(name, subcon, 0, 0);
  }
	
}

/**
 * a function that takes the context and return the computed value
 */
public static interface ValueFunc{
	Object get( Container ctx );
}
/**
 *     A computed value.
    Example:
    Struct("foo",
        UBInt8("width"),
        UBInt8("height"),
        Value("total_pixels", lambda ctx: ctx.width * ctx.height),
    )
 * @param name the name of the value
 * @param func a function that takes the context and return the computed value
 */
public static Value Value( String name, ValueFunc func ){
	return new Value( name, func );
};

public static class Value extends Construct{
	ValueFunc func;
	
	public Value(String name, ValueFunc func ) {
	  super(name);
	  this.func = func;
	  _set_flag(FLAG_DYNAMIC);
  }

	@Override
  public Object _parse(ByteBuffer stream, com.sirtrack.construct.lib.Containers.Container context) {
	  return func.get(context);
  }

	@Override
  protected void _build(Object obj, ByteArrayOutputStream stream, com.sirtrack.construct.lib.Containers.Container context) {
	  context.set( name, func.get(context) );
  }

	@Override
  protected int _sizeof(com.sirtrack.construct.lib.Containers.Container context) {
	  return 0;
  }
	
}

/**
  """
  A do-nothing construct, useful as the default case for Switch, or
  to indicate Enums.
  See also Switch and Enum.

  Notes:
  * this construct is a singleton. do not try to instatiate it, as it
    will not work...

  Example:
  Pass
 */
	static public final PassClass Pass = PassClass.getInstance();
	
	static private class PassClass extends Construct{
		private static PassClass instance;
		
		private PassClass(String name) {
	    super(name);
    }

		public static synchronized com.sirtrack.construct.Core.PassClass getInstance() {
	    if( instance == null )
	    	instance = new PassClass(null); 
	    return instance;
    }

		@Override
    public Object _parse(ByteBuffer stream, Container context) {
	    return null;
    }

		@Override
    protected void _build(Object obj, ByteArrayOutputStream stream, Container context) {
	    // assert obj is None
    }

		@Override
    protected int _sizeof(Container context) {
	    return 0;
    }
		
		
	}
}
