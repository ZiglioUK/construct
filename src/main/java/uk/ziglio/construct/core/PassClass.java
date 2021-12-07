package uk.ziglio.construct.core;

import java.io.ByteArrayOutputStream;

import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

public class PassClass extends Construct {
  private static PassClass instance;

  private PassClass(String name) {
    super(name);
  }

  public static synchronized uk.ziglio.construct.core.PassClass getInstance() {
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