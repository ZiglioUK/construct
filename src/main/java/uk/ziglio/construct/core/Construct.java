package uk.ziglio.construct.core;

import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import uk.ziglio.construct.Core;
import uk.ziglio.construct.errors.FieldError;
import uk.ziglio.construct.errors.SizeofError;
import uk.ziglio.construct.errors.ValueError;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

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
  public abstract class Construct implements Cloneable {

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
    protected void _set_flag(int flag) {
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
    public boolean _is_flag(int flag) {
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

    /*
     * #============================================================================
     * === # abstract constructs
     * #==================================================
     * =============================
     */
    public static byte[] _read_stream(ByteBufferWrapper stream, int length) {
      if (length < 0)
        throw new FieldError("length must be >= 0 " + length);
    
      int len = stream.remaining();
      if (len < length)
        throw new FieldError("expected " + length + " found " + len);
      
      byte[] out = new byte[length];
      stream.get(out, 0, length);
      
      return out;
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
      return parse( data, false );
    }

    /**
     * Parse an in-memory buffer.
     * 
     * Strings, buffers, memoryviews, and other complete buffers can be parsed
     * with this method.
     * 
     * @param data
     */
    public <T> T parse(byte[] data, boolean debug ) {
      return (T) parse_stream(new ByteBufferWrapper().wrap(data), debug );
    }

    /**
     * @param hex
     *          a string representation of hex bytes: 65535 = "FFFF"
     * @return
     */
    public <T> T parse(String hex) {
      return (T) parse( hex, false);
    }

    /**
     * @param hex
     *          a string representation of hex bytes: 65535 = "FFFF"
     * @return
     */
    public <T> T parse(String hex, boolean debug ) {
      byte[] data = hexStringToByteArray(hex);
      return (T) parse(data, debug );
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
      return parse_stream( stream, false );
    }

    /**
     * Parse a stream.
     * 
     * Files, pipes, sockets, and other streaming sources of data are handled by
     * this method.
     */
    public Object parse_stream(ByteBufferWrapper stream, boolean debug ) {
      Container c = Core.Container( "debug", debug );
      return _parse(stream, c );
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