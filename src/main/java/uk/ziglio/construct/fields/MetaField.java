package uk.ziglio.construct.fields;

import java.io.ByteArrayOutputStream;

import uk.ziglio.construct.Core.Construct;
import uk.ziglio.construct.interfaces.LengthFunc;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

public class MetaField extends Construct {

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


