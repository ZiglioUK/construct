package uk.ziglio.construct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.core.KeyFunc;
import uk.ziglio.construct.core.MetaArray;
import uk.ziglio.construct.core.PassClass;
import uk.ziglio.construct.core.Range;
import uk.ziglio.construct.core.Reconfig;
import uk.ziglio.construct.core.Sequence;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.core.Subconstruct;
import uk.ziglio.construct.core.Switch;
import uk.ziglio.construct.core.Value;
import uk.ziglio.construct.errors.SwitchError;
import uk.ziglio.construct.interfaces.CountFunc;
import uk.ziglio.construct.interfaces.ValueFunc;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;
import uk.ziglio.construct.lib.Decoder;
import uk.ziglio.construct.lib.Encoder;
import uk.ziglio.construct.lib.Resizer;

public class Core {

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
   * === # arrays and repeaters
   * #================================================
   * ===============================
   */

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
  public static MetaArray<Construct> MetaArray(CountFunc countfunc, Construct subcon) {
    return new MetaArray<Construct>(countfunc, subcon);
  }

  public static <T extends Construct>Range<T> Range(int mincount, int maxcount, T subcon) {
    return new Range<T>(mincount, maxcount, subcon);
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
        uk.ziglio.construct.lib.Containers.Container context) {
      throw new SwitchError("no default case defined");

    }

    @Override
    public int _sizeof(uk.ziglio.construct.lib.Containers.Container context) {
      throw new SwitchError("no default case defined");
    }

  };

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
  static public class Buffered extends Subconstruct {
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
    public Buffered(Construct subcon, Encoder encoder, Decoder decoder, Resizer resizer) {
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
      Boolean debug = context.get("debug");
      byte[] data = _read_stream(stream, _sizeof(context));
      if( debug != null && debug==true) {
        for( byte b : data ){
          System.out.print( String.format("%02x ", b ));
        }
        System.out.print( ": " );
      }
      
      byte[] stream2 = decoder.decode(data);
//      if( debug ){
//      System.out.print( Arrays.toString(stream2) + ": ");
//      }

      Object val = subcon._parse(new ByteBufferWrapper().wrap(stream2), context);

      if( debug != null && debug ){
        System.out.println(val);
      }
      return val;
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
  static public Reconfig<Construct> Reconfig(String name, Construct subcon) {
    return new Reconfig<Construct>(name, subcon);
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
  static public Reconfig<Construct> Reconfig(String name, Construct subcon, int setflags,
      int clearflags) {
    return new Reconfig<Construct>(name, subcon, setflags, clearflags);
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
  public static <T>Value<T> Value(String name, ValueFunc<T> func) {
    return new Value<T>(name, func);
  };

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
}
