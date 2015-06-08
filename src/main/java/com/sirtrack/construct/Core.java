package com.sirtrack.construct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.sirtrack.construct.lib.*;
import com.sirtrack.construct.lib.BitStream.BitStreamReader;
import com.sirtrack.construct.lib.BitStream.BitStreamWriter;
import com.sirtrack.construct.lib.Containers.Container;

import static com.sirtrack.construct.Macros.Field;
import static com.sirtrack.construct.lib.Binary.hexStringToByteArray;
import static com.sirtrack.construct.lib.Containers.*;

public class Core {

  public static class ConstructError extends RuntimeException {
    public ConstructError(String string) {
      super(string);
    }

    public ConstructError(String string, Exception e) {
      super(string, e);
    }
  }

  public static class FieldError extends ConstructError {
    public FieldError(String string) {
      super(string);
    }

    public FieldError(String string, Exception e) {
      super(string, e);
    }
  }

  public static class SizeofError extends ConstructError {
    public SizeofError(String string) {
      super(string);
    }
  }

  public static class ValueError extends ConstructError {
    public ValueError(String string) {
      super(string);
    }
  }

  public static class RangeError extends ConstructError {
    public RangeError(String string) {
      super(string);
    }
  }

  public static class TypeError extends ConstructError {
    public TypeError(String string) {
      super(string);
    }
  }

  public static class SwitchError extends ConstructError {
    public SwitchError(String string) {
      super(string);
    }
  }

  public static class ArrayError extends ConstructError {
    public ArrayError(String string, Exception e) {
      super(string, e);
    }
  }

  /*
   * #============================================================================
   * === # Shorthand expressions
   * #================================================
   * ===============================
   */
  // Bits = BitField
  // Byte = UBInt8
  // Bytes = Field
  // Const = ConstAdapter
  // Tunnel = TunnelAdapter
  // Embed = Embedded
  static public byte[] ByteArray(int... ints) {
    byte[] ba = new byte[ints.length];
    int k = 0;
    for (int i : ints) {
      ba[k++] = (byte) i;
    }
    return ba;
  }

  static public byte[] ByteArray(byte[]... bas) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (byte[] ba : bas) {
      try {
        out.write(ba);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return out.toByteArray();
  }

  /**
   * A generic container of attributes.
   * 
   * Containers are the common way to express parsed data.
   */
  static public <T> Container Container(Object... pairs) {
    return new Container(pairs);
  }

  /*
   * #============================================================================
   * === # abstract constructs
   * #==================================================
   * =============================
   */
  public static byte[] _read_stream(ByteBufferWrapper stream, int length) {
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
  static public abstract class Construct implements Cloneable {

    public static final int FLAG_COPY_CONTEXT = 0x0001;
    public static final int FLAG_DYNAMIC = 0x0002;
    public static final int FLAG_EMBED = 0x0004;
    public static final int FLAG_NESTING = 0x0008;

    public int conflags;
    public String name;
    protected Object val;

    public Construct() {
      this(null, 0);
      // must set name later
    }

    public Construct(String name) {
      this(name, 0);
    }

    public Construct(String name, int flags) {
      setName(name);
      this.conflags = flags;
    }

    public void setName(String name){
      if (name != null) {
        if (name.equals("_") || name.startsWith("<"))
          throw new ValueError("reserved name " + name); // raise
      }
      this.name = name;
    }
    
    public Construct clone() throws CloneNotSupportedException {
      return (Construct) super.clone();
    }
    
    @Override
    public String toString() {
      if( get() != null)
        return get().toString();
      else
        return getClass().getName() + "(" + name + ")";
    }

    public Object get() {
      return val;
    }

    public void set(Object val) {
      this.val = val;
    }

    /**
     * Set the given flag or flags.
     * 
     * @param flag
     *          flag to set; may be OR'd combination of flags
     */
    void _set_flag(int flag) {
      conflags |= flag;
    }

    /**
     * Clear the given flag or flags.
     * 
     * @param flag
     *          flag to clear; may be OR'd combination of flags
     */
    public void _clear_flag(int flag) {
      conflags &= ~flag;
    }

    /** Pull flags from subconstructs. */
    public void _inherit_flags(Construct... subcons) {
      for (Construct sc : subcons) {
        _set_flag(sc.conflags);
      }
    }

    /**
     * Check whether a given flag is set.
     * 
     * @param flag
     *          flag to check
     * @return
     */
    boolean _is_flag(int flag) {
      return (conflags & flag) == flag;
    }

    static public int getDataLength(Object data) {
      if (data instanceof String)
        return ((String) data).length();
      else if (data instanceof Byte || data instanceof Character)
        return 1;
      else if (data instanceof Integer) {
        int num = (Integer) data;
        if (num < 256)
          return 1;
        else if (num < 65536)
          return 2;
        else
          return 4;
        // return Integer.SIZE/8;
      } else if (data instanceof byte[])
        return ((byte[]) data).length;
      else if (data instanceof List)
        return ((List) data).size();
      else
        throw new RuntimeException("Data length unknown for " + data);
    }

    static public void appendDataStream(ByteArrayOutputStream stream,
        Object data) {
      if (data instanceof String)
        try {
          stream.write(((String) data).getBytes());
        } catch (IOException e) {
          throw new ValueError("Can't append data " + data + " "
              + e.getMessage());
        }
      else if (data instanceof Byte)
        stream.write((Byte) data);
      else if (data instanceof Integer)
        stream.write((Integer) data);
      else if (data instanceof byte[])
        try {
          stream.write((byte[]) data);
        } catch (IOException e) {
          throw new ValueError("Can't append data " + data + " "
              + e.getMessage());
        }
      else
        throw new ValueError("Can't append data " + data);
    }

    public void _write_stream(ByteArrayOutputStream stream, int length,
        Object data) {
      if (length < 0)
        throw new FieldError("length must be >= 0 " + length);

      int datalength = getDataLength(data);
      if (length != datalength)
        throw new FieldError("expected " + length + " found " + datalength);

      appendDataStream(stream, data);
    };

    /**
     * Parse an in-memory buffer.
     * 
     * Strings, buffers, memoryviews, and other complete buffers can be parsed
     * with this method.
     * 
     * @param data
     */
    public <T> T parse(byte[] data) {
      return (T) parse_stream(new ByteBufferWrapper().wrap(data));
    }

    /**
     * @param hex
     *          a string representation of hex bytes: 65535 = "FFFF"
     * @return
     */
    public <T> T parse(String hex) {
      byte[] data = hexStringToByteArray(hex);
      return (T) parse(data);
    }

    /**
     * Parse an in-memory buffer. Also accepts a context, useful for passing
     * initial values
     * 
     * @param data
     * @param context
     * @return
     */
    public <T> T parse(byte[] data, Container context) {
      return (T) _parse(new ByteBufferWrapper().wrap(data), context);
    }

    public <T> T parse(String hex, Container context) {
      byte[] data = hexStringToByteArray(hex);
      return (T) _parse(new ByteBufferWrapper().wrap(data), context);
    }

    /**
     * Parse a stream.
     * 
     * Files, pipes, sockets, and other streaming sources of data are handled by
     * this method.
     */
    public Object parse_stream(ByteBufferWrapper stream) {
      return _parse(stream, new Container());
    }

    abstract public Object _parse(ByteBufferWrapper stream, Container context);

    /**
     * Build an object in memory.
     * 
     * @param obj
     * @return
     */
    public byte[] build(Object obj) {
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
    public void build_stream(Object obj, ByteArrayOutputStream stream) {
      _build(obj, stream, new Container());
    }

    // abstract public void _build( String obj, OutputStream stream, Container
    // context);
    public abstract void _build(Object obj, ByteArrayOutputStream stream,
        Container context);

    /**
     * Calculate the size of this object, optionally using a context. Some
     * constructs have no fixed size and can only know their size for a given
     * hunk of data; these constructs will raise an error if they are not passed
     * a context.
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

    public abstract int _sizeof(Container context);
    
  }

  /**
   * Abstract subconstruct (wraps an inner construct, inheriting its name and
   * flags).
   */
  public static abstract class Subconstruct<T extends Construct> extends Construct {

    protected T subcon;

    /**
     * @param subcon
     *          the construct to wrap
     */
    public Subconstruct(T subcon) {
      super(subcon.name, subcon.conflags);
      this.subcon = subcon;
    }

    Subconstruct(String name, T subcon) {
      super(name, subcon.conflags);
      this.subcon = subcon;
    }

    public Subconstruct<T> clone() throws CloneNotSupportedException {
      Subconstruct<T> s = (Subconstruct<T>) super.clone();
      s.subcon = (T)subcon.clone();
      return s;
    }
    
//    @Override
//    public T get(){
//      return subcon;
//    }
    
    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      return subcon._parse(stream, context);
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      subcon._build(obj, stream, context);
    }

    @Override
    public int _sizeof(Container context) {
      return subcon._sizeof(context);
    }
  }

  /*
   * ============================================================================
   * === * Fields
   * ================================================================
   * ===============
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
    public Object _parse(ByteBufferWrapper stream, Container context) {
      return _read_stream(stream, length);
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      _write_stream(stream, length, obj);
    }

    @Override
    public int _sizeof(Container context) {
      return length;
    }


    /*
     * public int _sizeof( Container context ){
@Override
public Construct clone() {
  // TODO Auto-generated method stub
  return null;
} return length; }
     */
  }

  /**
   * A field that uses ``struct`` to pack and unpack data.
   * 
   * See ``struct`` documentation for instructions on crafting format strings.
   */
  public static class FormatField<T extends Number> extends StaticField {
    int length;
    Packer<T> packer;

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
        throw new ValueError("endianity must be be '=', '<', or '>' "
            + endianity);

      packer = new Packer<T>(endianity, format);
      super.length = packer.length();

    }

    @Override
    public T _parse(ByteBufferWrapper stream, Container context) {
      try {
        return packer.unpack(stream.bb);
      } catch (Exception e) {
        throw new FieldError(e.getMessage(), e);
      }
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      _write_stream(stream, super.length, packer.pack(obj));
    }

    @Override
    public T get() {
      return (T)val;
    }

  }

  /**
   * callable that takes a context and returns length as an int
   */
  static public interface LengthFunc {
    abstract int length(Container context);
  }

  /**
   * @param name
   *          context field name
   * @return get length from context field
   */
  static public LengthFunc LengthField(final String name) {
    return new LengthFunc() {
      public int length(Container ctx) {
        return (Integer) ctx.get(name);
      }
    };
  }

  /**
   * A variable-length field. The length is obtained at runtime from a function.
   * >>> foo = Struct("foo", ... Byte("length"), ... MetaField("data", lambda
   * ctx: ctx["length"]) ... ) >>> foo.parse("\\x03ABC") Container(data = 'ABC',
   * length = 3) >>> foo.parse("\\x04ABCD") Container(data = 'ABCD', length = 4)
   * 
   * @param name
   *          name of the field
   * @param lengthfunc
   *          callable that takes a context and returns length as an int
   */
  public static MetaField MetaField(String name, LengthFunc lengthfunc) {
    return new MetaField(name, lengthfunc);
  }

  public static class MetaField extends Construct {

    LengthFunc lengthfunc;

    /**
     * @param name
     *          name of the field
     * @param lengthfunc
     *          callable that takes a context and returns length as an int
     */
    public MetaField(String name, LengthFunc lengthfunc) {
      super(name);
      this.lengthfunc = lengthfunc;
      this._set_flag(FLAG_DYNAMIC);
    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      return _read_stream(stream, lengthfunc.length(context));
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      _write_stream(stream, lengthfunc.length(context), obj);
    }

    @Override
    public int _sizeof(Container context) {
      return lengthfunc.length(context);
    }

  }

  /*
   * #============================================================================
   * === # arrays and repeaters
   * #================================================
   * ===============================
   */

  /**
   * callable that takes a context and returns length as an int
   */
  static public interface CountFunc {
    abstract int count(Container context);
  }

  /**
   * Example: MetaArray(lambda ctx: 5, UBInt8("foo")) See also Array, Range and
   * RepeatUntil.
   * 
   * @param countfunc
   *          a function that takes the context as a parameter and returns the
   *          number of elements of the array (count)
   * @param subcon
   *          the subcon to repeat `countfunc()` times
   * @return An array (repeater) of a meta-count. The array will iterate exactly
   *         `countfunc()` times. Will raise ArrayError if less elements are
   *         found.
   */
  public static MetaArray MetaArray(CountFunc countfunc, Construct subcon) {
    return new MetaArray(countfunc, subcon);
  }

  /**
   * An array (repeater) of a meta-count. The array will iterate exactly
   * `countfunc()` times. Will raise ArrayError if less elements are found. See
   * also Array, Range and RepeatUntil.
   * 
   * Example: MetaArray(lambda ctx: 5, UBInt8("foo"))
   */
  public static class MetaArray<T extends Construct> extends Subconstruct<T> {

    CountFunc countfunc;

    /**
     * Parameters: countfunc - a function that takes the context as a parameter
     * and returns the number of elements of the array (count) subcon - the
     * subcon to repeat `countfunc()` times
     * 
     * @param length
     * @param name
     * @param subcon
     */
    public MetaArray(CountFunc countfunc, T subcon) {
      super(subcon);
      this.countfunc = countfunc;
      _clear_flag(FLAG_COPY_CONTEXT);
      _set_flag(FLAG_DYNAMIC);
    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      List obj = ListContainer();
      int c = 0;
      int count = countfunc.count(context);
      try {
        if ((subcon.conflags & FLAG_COPY_CONTEXT) != 0) {
          while (c < count) {
            obj.add(subcon._parse(stream, context.clone()));
            c += 1;
          }
        } else {
          while (c < count) {
            obj.add(subcon._parse(stream, context));
            c += 1;
          }
        }
      } catch (Exception e) {
        throw new ArrayError("expected " + count + ", found " + c, e);
      }
      val = obj;
      return obj;
    }

    @Override
    public void _build(Object object, ByteArrayOutputStream stream,
        Container context) {

      List<Object> obj = (List<Object>) object;

      int count = countfunc.count(context);

      if (obj.size() != count) {
        throw new ArrayError("expected " + count + ", found " + obj.size(),
            null);
      }

      if ((subcon.conflags & FLAG_COPY_CONTEXT) != 0) {
        for (Object subobj : obj) {
          subcon._build(subobj, stream, context.clone());
        }
      } else {
        for (Object subobj : obj) {
          subcon._build(subobj, stream, context);
        }
      }
    }

    @Override
    public int _sizeof(Container context) {
      return subcon._sizeof(context) * countfunc.count(context);
    }

  }

  public static <T extends Construct>Range Range(int mincount, int maxcount, T subcon) {
    return new Range<T>(mincount, maxcount, subcon);
  }

  /**
   * A range-array. The subcon will iterate between `mincount` to `maxcount`
   * times. If less than `mincount` elements are found, raises RangeError. See
   * also GreedyRange and OptionalGreedyRange.
   * 
   * The general-case repeater. Repeats the given unit for at least mincount
   * times, and up to maxcount times. If an exception occurs (EOF, validation
   * error), the repeater exits. If less than mincount units have been
   * successfully parsed, a RangeError is raised.
   * 
   * .. note:: This object requires a seekable stream for parsing.
   */
  public static class Range<T extends Construct> extends Subconstruct<T> {

    /**
     * @param mincount
     *          the minimal count
     * @param maxcount
     *          the maximal count
     * @param subcon
     *          the subcon to repeat >>> c = Range(3, 7, UBInt8("foo")) >>>
     *          c.parse("\\x01\\x02") Traceback (most recent call last): ...
     *          construct.core.RangeError: expected 3..7, found 2 >>>
     *          c.parse("\\x01\\x02\\x03") [1, 2, 3] >>>
     *          c.parse("\\x01\\x02\\x03\\x04\\x05\\x06") [1, 2, 3, 4, 5, 6] >>>
     *          c.parse("\\x01\\x02\\x03\\x04\\x05\\x06\\x07") [1, 2, 3, 4, 5,
     *          6, 7] >>>
     *          c.parse("\\x01\\x02\\x03\\x04\\x05\\x06\\x07\\x08\\x09") [1, 2,
     *          3, 4, 5, 6, 7] >>> c.build([1,2]) Traceback (most recent call
     *          last): ... construct.core.RangeError: expected 3..7, found 2 >>>
     *          c.build([1,2,3,4]) '\\x01\\x02\\x03\\x04' >>>
     *          c.build([1,2,3,4,5,6,7,8]) Traceback (most recent call last):
     *          ... construct.core.RangeError: expected 3..7, found 8
     */
    int mincount;
    int maxcout;

    public Range(int mincount, int maxcount, T subcon) {
      super(subcon);
      this.mincount = mincount;
      this.maxcout = maxcount;
      _clear_flag(FLAG_COPY_CONTEXT);
      _set_flag(FLAG_DYNAMIC);
    }

    @Override
    public List<T> get(){
      return (List<T>)val;
    }
    
    @Override
    public void set( Object val ){
    }
    
    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      // obj = ListContainer()
      List<Object> obj = ListContainer();
      val = ListContainer();
      
      int c = 0;
      int pos = stream.position();
      try {
        if ((subcon.conflags & FLAG_COPY_CONTEXT) != 0) {
          while (c < maxcout) {
            T clone = (T) subcon.clone();
            pos = stream.position();
            get().add( clone ); 
            obj.add(clone._parse(stream, context.clone()));
            c += 1;
          }
        } else {
          while (c < maxcout) {
            T clone = (T) subcon.clone();
            pos = stream.position();
            get().add(clone);
            obj.add(clone._parse(stream, context));
            c += 1;
          }
        }
      } catch (Exception e) {
        if (c < mincount) {
          throw new RangeError("expected " + mincount + " to " + maxcout
              + " found " + c + " " + e.getMessage());
        }
        stream.position(pos);
      }

      return obj;
    }

    @Override
    public void _build(Object object, ByteArrayOutputStream stream,
        Container context) {

      if (!(object instanceof List))
        throw new TypeError("Expected object array");
      List<Object> obj = (List<Object>) object;
      if (obj.size() < mincount || obj.size() > maxcout) {
        throw new RangeError("expected " + mincount + " to " + maxcout
            + " found " + obj.size());
      }

      int cnt = 0;
      try {
        if ((subcon.conflags & FLAG_COPY_CONTEXT) != 0) {
          for (Object subobj : obj) {
            subcon._build(subobj, stream, context.clone());
            cnt += 1;
          }
        } else {
          for (Object subobj : obj) {
            subcon._build(subobj, stream, context);
            cnt += 1;
          }
        }
      } catch (Exception e) {
        throw new RangeError(e.getMessage());
      }
    }

    @Override
    public int _sizeof(Container context) {
      throw new SizeofError("can't calculate size");
    }

  }

  /*
   * #============================================================================
   * === # structures and sequences
   * #============================================
   * ===================================
   */
  /**
   * A sequence of named constructs, similar to structs in C. The elements are
   * parsed and built in the order they are defined. See also Embedded. Example:
   * Struct("foo", UBInt8("first_element"), UBInt16("second_element"),
   * Padding(2), UBInt8("third_element"), )
   */
  static public Struct Struct(String name, Construct... subcons) {
    return new Struct(name, subcons);
  }
  static public Struct Struct(Construct... subcons) {
    return new Struct(null, subcons);
  }

  static public class Struct extends Construct {
    public boolean nested = true;
    public Construct[] subcons;

    /**
     * @param name
     *          the name of the structure
     * @param subcons
     *          a sequence of subconstructs that make up this structure.
     */
    public Struct(String name, Construct... subcons) {
      super(name);
      this.subcons = subcons;
      _inherit_flags(subcons);
      _clear_flag(FLAG_EMBED);
    }

    @Override
    public Struct clone() throws CloneNotSupportedException {
      Struct clone = (Struct) super.clone();

      clone.subcons = new Construct[subcons.length];
      Field[] fields = getClass().getDeclaredFields();

      int i = 0;
      for( Field f : fields ){
        if (Construct.class.isAssignableFrom(f.getType()))
	        try{
	          f.setAccessible(true);
	          // clone field 
	          Construct fclone = ((Construct)f.get(this)).clone();
	          // set the field clone into the Struct clone 
	          f.set(clone, fclone);
	          // also add the field clone to the subcons array 
	          clone.subcons[i++] = fclone;
	        } catch( Exception e ){
	          throw new RuntimeException(e);
	        }
          
        // Clone elements in the subcons array
        // Because we cater for both static and runtime Struct definitions,
        // we need to make sure subcons don't end up twice in the subcons array
        // This case has to handle only the runtime (old) definition
        // So if we already have stuff in the subcons array, carry on
        else if ( /*f.getType() == Construct[].class &&*/ f.getName().equals("subcons") && clone.subcons[0] == null )
	        try{
	          i = 0;
	          for( Construct c : subcons ){
	            clone.subcons[i++] = c.clone();
	          }

	        } catch( Exception e ){
	          throw new RuntimeException(e);
	      }
        else
        	continue;
      }
      return clone;
    }

    /**
     * This is a special constructor for typesafe Structs.
     * Instead of passing an array of Subcons at runtime,
     * this constructor inspects the public fields of type Construct for this Struct
     * and invokes each field's constructor by passing the field name.
     * It's assumed that all declared fields have a public constructor: Construct( String name )
     * @param name
     */
    public Struct(String name) {
      super(name);
      Constructor fctor;
      Field field = null;
      String fname;
      try {
        Field[] fields = getClass().getFields();
        List<Construct> subconf = new ArrayList<Construct>();

        for( int i = 0; i < fields.length; i++ ) {
          field = fields[i];
          field.setAccessible(true);
          Class clazz = field.getType();
          
          if (!Construct.class.isAssignableFrom(clazz))
            continue;

          fname = field.getName();
          fctor = clazz.getConstructors()[0];
          fctor.setAccessible(true);
          Construct inst;
          Object enclosingInst;
          switch (fctor.getParameterTypes().length) {
          // TODO should check that the first instance is of the right type: enclosing type or String
          case 2: // inner classes
            try{
              // static class case
              enclosingInst = getClass().getDeclaredField("this$0").get(this);
            } catch( NoSuchFieldException nsfe ){
              // private nested class case
              enclosingInst = this;
            }
            inst = (Construct) fctor.newInstance(enclosingInst, fname);
            break;
          case 1:
            if( String.class.isAssignableFrom( fctor.getParameterTypes()[0] )){
              inst = (Construct) fctor.newInstance(fname);
            } else {
              // no arguments constructor
              try{
                // static class case
                enclosingInst = getClass().getDeclaredField("this$0").get(this);
              } catch( NoSuchFieldException nsfe ){
                // private nested class case
                enclosingInst = this;
              }
              inst = (Construct) fctor.newInstance(enclosingInst);
              
              // now call name setter with fname
              inst.setName(fname); 
            }
            break;
          case 0:
            inst = (Construct) fctor.newInstance();
            break;
          default:
            throw new Exception("No default case: " + fctor);
          }
          field.set(this, inst);
          subconf.add(inst);
        }
        subcons = new Construct[subconf.size()];
        subcons = subconf.toArray(subcons);
        _inherit_flags(subcons);
        _clear_flag(FLAG_EMBED);
      } catch (Exception e) {
        throw new RuntimeException("Error constructing field " + field + "\r\n" + e.toString(), e);
      }
    }

    public Struct() {
      this((String) null);
    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {

      Container obj;
      if (context.contains("<obj>")) {
        obj = context.get("<obj>");
        context.del("<obj>");
      } else {
        obj = new Container();
        if (nested) {
          context = Container("_", context);
        }
      }

      for (Construct sc : subcons) {
        if ((sc.conflags & FLAG_EMBED) != 0) {
          context.set("<obj>", obj);
          Object val = sc._parse(stream, context);
          sc.set( val );
        } else {
          Object val = sc._parse(stream, context);
          sc.set( val );
          if (sc.name != null) {
            obj.set(sc.name, val);
            context.set(sc.name, val);
          }
        }
      }
      return obj;
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      if (context.contains("<unnested>")) {
        context.del("<unnested>");
      } else if (nested) {
        context = Container("_", context);
      }
      for (Construct sc : subcons) {
        Object subobj;
        if ((sc.conflags & FLAG_EMBED) != 0) {
          context.set("<unnested>", true);
          subobj = obj;
        } else if (sc.name == null) {
          subobj = null;
        } else if (obj instanceof Container) {
          Container container = (Container) obj;
          subobj = container.get(sc.name);

          if (subobj == null)
            throw new FieldError("No field found: " + sc.name + " in " + subobj);

          context.set(sc.name, subobj);
        } else
          continue;

        sc._build(subobj, stream, context);
      }
    }

    @Override
    public int _sizeof(Container context) {
      int sum = 0;
      // if( nested )
      // context = Container( "_", context );

      for (Construct sc : subcons) {
        sum += sc._sizeof(context);
      }

      return sum;
    }
  }

  /**
   * @param name
   *          the name of the structure
   * @param subcons
   *          a sequence of subconstructs that make up this structure.
   * @param nested
   *          : a keyword-only argument that indicates whether this struct
   *          creates a nested context. The default is True. This parameter is
   *          considered "advanced usage", and may be removed in the future.
   * @return A sequence of unnamed constructs. The elements are parsed and built
   *         in the order they are defined. See also Embedded. Example:
   *         Sequence("foo", UBInt8("first_element"), UBInt16("second_element"),
   *         Padding(2), UBInt8("third_element"), )
   */
  public static Sequence Sequence(String name, Construct... subcons) {
    return new Sequence(name, subcons);
  }

  public static class Sequence extends Struct {
    public Sequence(String name, Construct... subcons) {
      super(name, subcons);
    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      List obj;
      if (context.contains("<obj>")) {
        obj = context.get("<obj>");
        context.del("<obj>");
      } else {
        obj = ListContainer();
        if (nested) {
          context = Container("_", context);
        }
      }
      for (Construct sc : subcons) {
        if ((sc.conflags & FLAG_EMBED) != 0) {
          context.set("<obj>", obj);
          sc._parse(stream, context);
        } else {
          Object subobj = sc._parse(stream, context);
          if (sc.name != null) {
            obj.add(subobj);
            context.set(sc.name, subobj);
          }
        }
      }
      return obj;
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      if (context.contains("<unnested>")) {
        context.del("<unnested>");
      } else if (nested) {
        context = Container("_", context);
      }

      Object subobj;
      ListIterator objiter;
      if (obj instanceof List)
        objiter = ((List) obj).listIterator();
      else
        objiter = (ListIterator) obj;

      for (Construct sc : subcons) {
        if ((sc.conflags & FLAG_EMBED) != 0) {
          context.set("<unnested>", true);
          subobj = objiter;
        } else if (sc.name == null) {
          subobj = null;
        } else {
          subobj = objiter.next();
          context.set(sc.name, subobj);
        }
        sc._build(subobj, stream, context);
      }
    }
  }

  /*
   * #============================================================================
   * === # conditional
   * #==========================================================
   * =====================
   */

  public static Construct NoDefault = new Construct(null) {

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      throw new SwitchError("no default case defined");
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        com.sirtrack.construct.lib.Containers.Container context) {
      throw new SwitchError("no default case defined");

    }

    @Override
    public int _sizeof(com.sirtrack.construct.lib.Containers.Container context) {
      throw new SwitchError("no default case defined");
    }

  };

  /**
   * a function that takes the context and returns a key
   */
  public abstract static class KeyFunc {
    public final String key;

    public KeyFunc(String key) {
      this.key = key;
    }

    public KeyFunc() {
      this.key = null;
    }

    public String key() {
      return key;
    }

    public abstract Object get(Container context);
  }

  /**
   * @param key
   *          a context key
   * @param val
   *          a value
   * @return A KeyFunc that evaluates ctx.get(key).equals(val)
   */
  public static KeyFunc Equals(final String key, final Object val) {
    return new KeyFunc(key) {
      public Object get(Container ctx) {
        return ctx.get(key).equals(val);
      };
    };
  }

  /**
   * @param key
   *          a context key
   * @return ctx.get(key)
   */
  public static KeyFunc KeyVal(final String key) {
    return new KeyFunc(key) {
      public Object get(Container ctx) {
        return ctx.get(key);
      };
    };
  }

  /**
   * A conditional branch. Switch will choose the case to follow based on the
   * return value of keyfunc. If no case is matched, and no default value is
   * given, SwitchError will be raised. See also Pass. Example: Struct("foo",
   * UBInt8("type"), Switch("value", lambda ctx: ctx.type, { 1 : UBInt8("spam"),
   * 2 : UBInt16("spam"), 3 : UBInt32("spam"), 4 : UBInt64("spam"), } ), )
   * 
   * @param name
   *          the name of the construct
   * @param keyfunc
   *          a function that takes the context and returns a key, which will ne
   *          used to choose the relevant case.
   * @param cases
   *          a dictionary mapping keys to constructs. the keys can be any
   *          values that may be returned by keyfunc.
   */
  public static Switch Switch(String name, KeyFunc keyfunc, Object... cases) {
    return new Switch(name, keyfunc, Container(cases));
  }

  /**
   * A conditional branch. Switch will choose the case to follow based on the
   * return value of keyfunc. If no case is matched, and no default value is
   * given, SwitchError will be raised. See also Pass. Example: Struct("foo",
   * UBInt8("type"), Switch("value", lambda ctx: ctx.type, { 1 : UBInt8("spam"),
   * 2 : UBInt16("spam"), 3 : UBInt32("spam"), 4 : UBInt64("spam"), } ), )
   * 
   * @param name
   *          the name of the construct
   * @param keyfunc
   *          a function that takes the context and returns a key, which will ne
   *          used to choose the relevant case.
   * @param cases
   *          a dictionary mapping keys to constructs. the keys can be any
   *          values that may be returned by keyfunc.
   * @param defaultval
   *          a default value to use when the key is not found in the cases. if
   *          not supplied, an exception will be raised when the key is not
   *          found. You can use the builtin construct Pass for 'do-nothing'.
   * @param include_key
   *          whether or not to include the key in the return value of parsing.
   *          defualt is False.
   */
  public static Switch Switch(String name, KeyFunc keyfunc, Container cases,
      Construct defaultval, boolean include_key) {
    return new Switch(name, keyfunc, cases, defaultval, include_key);
  }

  /**
   * A conditional branch. Switch will choose the case to follow based on the
   * return value of keyfunc. If no case is matched, and no default value is
   * given, SwitchError will be raised. See also Pass. Example: Struct("foo",
   * UBInt8("type"), Switch("value", lambda ctx: ctx.type, { 1 : UBInt8("spam"),
   * 2 : UBInt16("spam"), 3 : UBInt32("spam"), 4 : UBInt64("spam"), } ), )
   */
  public static class Switch extends Construct {
    /**
     * a function that takes the context and returns a key, which will ne used
     * to choose the relevant case.
     */
    public KeyFunc keyfunc;
    public Container cases;
    public Construct defaultval;
    public boolean include_key;

    /**
     * @param name
     *          the name of the construct
     * @param keyfunc
     *          a function that takes the context and returns a key, which will
     *          ne used to choose the relevant case.
     * @param cases
     *          a dictionary mapping keys to constructs. the keys can be any
     *          values that may be returned by keyfunc.
     * @param defaultval
     *          a default value to use when the key is not found in the cases.
     *          if not supplied, an exception will be raised when the key is not
     *          found. You can use the builtin construct Pass for 'do-nothing'.
     * @param include_key
     *          whether or not to include the key in the return value of
     *          parsing. defualt is False.
     */
    public Switch(String name, KeyFunc keyfunc, Container cases,
        Construct defaultval, boolean include_key) {
      super(name);
      this.keyfunc = keyfunc;
      this.cases = cases;
      this.defaultval = defaultval;
      this.include_key = include_key;
      Construct[] ca = cases.values(Construct.class);
      this._inherit_flags(ca);
      this._set_flag(FLAG_DYNAMIC);
    }

    public Switch(String name, KeyFunc keyfunc, Container cases) {
      this(name, keyfunc, cases, NoDefault, false);
    }

    @Override
    public Object get() {
      return val;
    }

    @Override
    public void set(Object val) {
      // do nothing: prevent Structs from setting val to the parsed value
      // keep the Switch case construct as a value
      //this.val = val;
    }

    @Override
    public Switch clone() throws CloneNotSupportedException {
      Switch c = (Switch) super.clone();
      c.cases = cases.clone(); // TODO check deep copy
      c.defaultval = defaultval.clone();

      return c;
    }
    
    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      Object key = keyfunc.get(context);
      /* assign the case Construct as a value for Switch
       * users can then retrieve the case Construct with get()*/
      val = cases.get(key, defaultval);
      Object res = ((Construct)val)._parse(stream, context);
      if (include_key) 
        res = Container(key, res);
     return res;
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      Object key;
      if (include_key) {
        List list = (List) obj;
        key = list.get(0);
        obj = list.get(1);
      } else {
        key = keyfunc.get(context);
      }

      Construct casestruct = cases.get(key, defaultval);
      casestruct._build(obj, stream, context);
      /*
       * if self.include_key: key, obj = obj else: key = self.keyfunc(context)
       * case = self.cases.get(key, self.default) case._build(obj, stream,
       * context)
       */
    }

    @Override
    public int _sizeof(Container context) {
      Construct casestruct = cases.get(keyfunc.get(context), defaultval);
      return casestruct._sizeof(context);
    }

  }

  /*
   * #============================================================================
   * === # stream manipulation
   * #==================================================
   * =============================
   */
  /**
   * Creates an in-memory buffered stream, which can undergo encoding and
   * decoding prior to being passed on to the subconstruct. See also Bitwise.
   * 
   * Note: Do not use pointers inside Buffered
   * 
   * Example: Buffered(BitField("foo", 16), encoder = decode_bin, decoder =
   * encode_bin, resizer = lambda size: size / 8, )
   */
  static public class Buffered<T extends Construct> extends Subconstruct<T> {
    public Encoder encoder;
    public Decoder decoder;
    public Resizer resizer;

    /**
     * Creates an in-memory buffered stream, which can undergo encoding and
     * decoding prior to being passed on to the subconstruct. See also Bitwise.<br/>
     * <br/>
     * Note: Do not use pointers inside Buffered
     * 
     * @param subcon
     *          the subcon which will operate on the buffer
     * @param encoder
     *          a function that takes a string and returns an encoded string
     *          (used after building)
     * @param decoder
     *          a function that takes a string and returns a decoded string
     *          (used before parsing)
     * @param resizer
     *          a function that takes the size of the subcon and "adjusts" or
     *          "resizes" it according to the encoding/decoding process.
     */
    public Buffered(T subcon, Encoder encoder, Decoder decoder, Resizer resizer) {
      super(subcon);
      this.encoder = encoder;
      this.decoder = decoder;
      this.resizer = resizer;
    }

//    @Override
//    public T get(){
//      return subcon;
//    }
    
//    @Override
//    public void set( Object val ){
//      subcon.set(val);
//    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      byte[] data = _read_stream(stream, _sizeof(context));
      byte[] stream2 = decoder.decode(data);
      return subcon._parse(new ByteBufferWrapper().wrap(stream2), context);
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      int size = _sizeof(context);
      ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
      subcon._build(obj, stream2, context);
      byte[] data = encoder.encode(stream2.toString());
      if (data.length != size)
        throw new RuntimeException("Wrong data length: " + data.length);
      _write_stream(stream, size, data);
    }

    @Override
    public int _sizeof(Container context) {
      return resizer.resize(
          subcon._sizeof(context));
    }
  }

  /**
   * Wraps the stream with a read-wrapper (for parsing) or a write-wrapper (for
   * building). The stream wrapper can buffer the data internally, reading it
   * from- or writing it to the underlying stream as needed. For example,
   * BitByteBufferWrapper reads whole bytes from the underlying stream, but
   * returns them as individual bits. See also Bitwise.
   * 
   * When the parsing or building is done, the stream's close method will be
   * invoked. It can perform any finalization needed for the stream wrapper, but
   * it must not close the underlying stream.
   * 
   * Note: Do not use pointers inside Restream
   * 
   * Example: Restream(BitField("foo", 16), stream_reader =
   * BitByteBufferWrapper, stream_writer = BitStreamWriter, resizer = lambda
   * size: size / 8, )
   */
  public static class Restream extends Subconstruct {
    BitStreamReader stream_reader;
    BitStreamWriter stream_writer;
    Resizer resizer;

    /**
     * Wraps the stream with a read-wrapper (for parsing) or a write-wrapper
     * (for building). The stream wrapper can buffer the data internally,
     * reading it from- or writing it to the underlying stream as needed. For
     * example, BitByteBufferWrapper reads whole bytes from the underlying
     * stream, but returns them as individual bits. See also Bitwise.<br/>
     * <br/>
     * When the parsing or building is done, the stream's close method will be
     * invoked. It can perform any finalization needed for the stream wrapper,
     * but it must not close the underlying stream.<br/>
     * <br/>
     * Note: Do not use pointers inside Restream
     * 
     * @param subcon
     *          the subcon
     * @param stream_reader
     *          the read-wrapper
     * @param stream_writer
     *          the write wrapper
     * @param resizer
     *          a function that takes the size of the subcon and "adjusts" or
     *          "resizes" it according to the encoding/decoding process.
     */
    public Restream(Construct subcon, BitStreamReader stream_reader,
        BitStreamWriter stream_writer, Resizer resizer) {
      super(subcon);
      this.stream_reader = stream_reader;
      this.stream_writer = stream_writer;
      this.resizer = resizer;
    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      stream_reader.init(stream);
      Object obj = subcon._parse(stream_reader, context);
      stream_reader.close();
      return obj;
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      ByteArrayOutputStream stream2 = stream_writer.init(stream);
      subcon._build(obj, stream2, context);
      stream_writer.close();
    }

    @Override
    public int _sizeof(Container context) {
      return resizer.resize(subcon._sizeof(context));
    }

  }

  /*
   * class Pointer(Subconstruct): """ Changes the stream position to a given
   * offset, where the construction should take place, and restores the stream
   * position when finished. See also Anchor, OnDemand and OnDemandPointer.
   * 
   * Notes: requires a seekable stream.
   * 
   * Parameters: offsetfunc: a function that takes the context and returns an
   * absolute stream position, where the construction would take place subcon -
   * the subcon to use at `offsetfunc()`
   * 
   * Example: Struct("foo", UBInt32("spam_pointer"), Pointer(lambda ctx:
   * ctx.spam_pointer, Array(5, UBInt8("spam")) ) ) """ __slots__ =
   * ["offsetfunc"] def __init__(self, offsetfunc, subcon):
   * Subconstruct.__init__(self, subcon) self.offsetfunc = offsetfunc def
   * _parse(self, stream, context): newpos = self.offsetfunc(context) origpos =
   * stream.tell() stream.seek(newpos) obj = self.subcon._parse(stream, context)
   * stream.seek(origpos) return obj def _build(self, obj, stream, context):
   * newpos = self.offsetfunc(context) origpos = stream.tell()
   * stream.seek(newpos) self.subcon._build(obj, stream, context)
   * stream.seek(origpos) def _sizeof(self, context): return 0
   * 
   * class Peek(Subconstruct): """ Peeks at the stream: parses without changing
   * the stream position. See also Union. If the end of the stream is reached
   * when peeking, returns None.
   * 
   * Notes: requires a seekable stream.
   * 
   * Parameters: subcon - the subcon to peek at perform_build - whether or not
   * to perform building. by default this parameter is set to False, meaning
   * building is a no-op.
   * 
   * Example: Peek(UBInt8("foo")) """ __slots__ = ["perform_build"] def
   * __init__(self, subcon, perform_build = False): Subconstruct.__init__(self,
   * subcon) self.perform_build = perform_build def _parse(self, stream,
   * context): pos = stream.tell() try: return self.subcon._parse(stream,
   * context) except FieldError: pass finally: stream.seek(pos) def _build(self,
   * obj, stream, context): if self.perform_build: self.subcon._build(obj,
   * stream, context) def _sizeof(self, context): return 0
   * 
   * class OnDemand(Subconstruct): """ Allows for on-demand (lazy) parsing. When
   * parsing, it will return a LazyContainer that represents a pointer to the
   * data, but does not actually parses it from stream until it's "demanded". By
   * accessing the 'value' property of LazyContainers, you will demand the data
   * from the stream. The data will be parsed and cached for later use. You can
   * use the 'has_value' property to know whether the data has already been
   * demanded. See also OnDemandPointer.
   * 
   * Notes: requires a seekable stream.
   * 
   * Parameters: subcon - advance_stream - whether or not to advance the stream
   * position. by default this is True, but if subcon is a pointer, this should
   * be False. force_build - whether or not to force build. If set to False, and
   * the LazyContainer has not been demaned, building is a no-op.
   * 
   * Example: OnDemand(Array(10000, UBInt8("foo")) """ __slots__ =
   * ["advance_stream", "force_build"] def __init__(self, subcon, advance_stream
   * = True, force_build = True): Subconstruct.__init__(self, subcon)
   * self.advance_stream = advance_stream self.force_build = force_build def
   * _parse(self, stream, context): obj = LazyContainer(self.subcon, stream,
   * stream.tell(), context) if self.advance_stream:
   * stream.seek(self.subcon._sizeof(context), 1) return obj def _build(self,
   * obj, stream, context): if not isinstance(obj, LazyContainer):
   * self.subcon._build(obj, stream, context) elif self.force_build or
   * obj.has_value: self.subcon._build(obj.value, stream, context) elif
   * self.advance_stream: stream.seek(self.subcon._sizeof(context), 1)
   */

  /*
   * #============================================================================
   * === # miscellaneous
   * #========================================================
   * =======================
   */

  /**
   * @param name
   *          the new name
   * @param subcon
   *          the subcon to reconfigure
   * @param setflags
   *          the flags to set (default is 0)
   * @param clearflags
   *          the flags to clear (default is 0)
   */
  static public Reconfig Reconfig(String name, Construct subcon) {
    return new Reconfig(name, subcon);
  }

  /**
   * @param name
   *          the new name
   * @param subcon
   *          the subcon to reconfigure
   * @param setflags
   *          the flags to set (default is 0)
   * @param clearflags
   *          the flags to clear (default is 0)
   */
  static public Reconfig Reconfig(String name, Construct subcon, int setflags,
      int clearflags) {
    return new Reconfig(name, subcon, setflags, clearflags);
  }

  /**
   * Reconfigures a subconstruct. Reconfig can be used to change the name and
   * set and clear flags of the inner subcon. Example: Reconfig("foo",
   * UBInt8("bar"))
   */
  static public class Reconfig<T extends Construct> extends Subconstruct<T> {

    /**
     * @param name
     *          the new name
     * @param subcon
     *          the subcon to reconfigure
     * @param setflags
     *          the flags to set (default is 0)
     * @param clearflags
     *          the flags to clear (default is 0)
     */
    public Reconfig(String name, T subcon, int setflags, int clearflags) {
      super(name, subcon);
      _set_flag(setflags);
      _clear_flag(clearflags);
    }

    public Reconfig(String name, T subcon) {
      this(name, subcon, 0, 0);
    }

    @Override
    public T get(){
      return subcon;
    }
//
//    @Override
//    public void set( Object val ){
//      subcon.set(val);
//    }
    
  }

  /**
   * a function that takes the context and return the computed value
   */
  public static interface ValueFunc<T> {
    T get(Container ctx);
  }

  /**
   * A computed value. Example: Struct("foo", UBInt8("width"), UBInt8("height"),
   * Value("total_pixels", lambda ctx: ctx.width * ctx.height), )
   * 
   * @param name
   *          the name of the value
   * @param func
   *          a function that takes the context and return the computed value
   */
  public static <T>Value Value(String name, ValueFunc<T> func) {
    return new Value<T>(name, func);
  };

  public static class Value<T> extends Construct implements ValueFunc<T> {
    public ValueFunc<T> func;

    /**
     * Us this consstructor if a class extends Value and implements ValueFunc, 
     * in its own constructor it needs to set super.func = this
     * 
     * @param name
     */
    public Value() {
      super();
      this.func = this;
      _set_flag(FLAG_DYNAMIC);
    }

    /**
     * @param name
     * @param func overrides unimplemented ValueFunc<T> at runtime
     */
    public Value(String name, ValueFunc<T> func) {
      super(name);
      this.func = func;
      _set_flag(FLAG_DYNAMIC);
    }

    public T get(Container ctx){
      throw new RuntimeException("unimplemented");
    }

    @Override
    public T get() {
      return (T)val;
    }
    
    @Override
    public Object _parse(ByteBufferWrapper stream,
        com.sirtrack.construct.lib.Containers.Container context) {
      return func.get(context);
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        com.sirtrack.construct.lib.Containers.Container context) {
      context.set(name, func.get(context));
    }

    @Override
    public int _sizeof(com.sirtrack.construct.lib.Containers.Container context) {
      return 0;
    }

  }

  /**
   * """ A do-nothing construct, useful as the default case for Switch, or to
   * indicate Enums. See also Switch and Enum.
   * 
   * Notes: this construct is a singleton. do not try to instatiate it, as it
   * will not work...
   * 
   * Example: Pass
   */
  static public final PassClass Pass = PassClass.getInstance();

  static private class PassClass extends Construct {
    private static PassClass instance;

    private PassClass(String name) {
      super(name);
    }

    public static synchronized com.sirtrack.construct.Core.PassClass getInstance() {
      if (instance == null)
        instance = new PassClass(null);
      return instance;
    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      return null;
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      // assert obj is None
    }

    @Override
    public int _sizeof(Container context) {
      return 0;
    }

  }
}
