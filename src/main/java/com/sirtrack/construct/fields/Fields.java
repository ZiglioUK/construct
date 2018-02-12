package com.sirtrack.construct.fields;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.LengthConstruct;
import com.sirtrack.construct.Packer;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.FieldError;
import com.sirtrack.construct.Core.ValueError;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Containers.Container;

public class Fields {

  /*
   * ============================================================================
   * === * Fields
   * ================================================================
   * ===============
   */

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
//        e.printStackTrace();
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
    return ctx -> (Integer) ctx.get(name);
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


}
