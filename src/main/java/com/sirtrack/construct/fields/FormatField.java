package com.sirtrack.construct.fields;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Packer;
import com.sirtrack.construct.Core.FieldError;
import com.sirtrack.construct.Core.ValueError;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Containers.Container;

/**
 * A field that uses ``struct`` to pack and unpack data.
 * 
 * See ``struct`` documentation for instructions on crafting format strings.
 */
public class FormatField<T extends Number> extends StaticField {
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
//      e.printStackTrace();
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

