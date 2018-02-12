package com.sirtrack.construct.fields;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Packer;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.FieldError;
import com.sirtrack.construct.Core.ValueError;
import com.sirtrack.construct.interfaces.LengthConstruct;
import com.sirtrack.construct.interfaces.LengthFunc;
import com.sirtrack.construct.lib.ByteBufferWrapper;

public class Fields {

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

  
}
