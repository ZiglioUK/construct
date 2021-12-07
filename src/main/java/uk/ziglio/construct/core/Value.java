package uk.ziglio.construct.core;

import java.io.ByteArrayOutputStream;

import uk.ziglio.construct.interfaces.ValueFunc;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

public class Value<T> extends Construct implements ValueFunc<T> {
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
      uk.ziglio.construct.lib.Containers.Container context) {
    return func.get(context);
  }

  @Override
  public void _build(Object obj, ByteArrayOutputStream stream,
      uk.ziglio.construct.lib.Containers.Container context) {
    context.set(name, func.get(context));
  }

  @Override
  public int _sizeof(uk.ziglio.construct.lib.Containers.Container context) {
    return 0;
  }

}