package uk.ziglio.construct.core;

import static uk.ziglio.construct.lib.Containers.ListContainer;

import java.io.ByteArrayOutputStream;
import java.util.List;

import uk.ziglio.construct.Core;
import uk.ziglio.construct.errors.ArrayError;
import uk.ziglio.construct.interfaces.CountFunc;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

/**
 * An array (repeater) of a meta-count. The array will iterate exactly
 * `countfunc()` times. Will raise ArrayError if less elements are found. See
 * also Array, Range and RepeatUntil.
 * 
 * Example: MetaArray(lambda ctx: 5, UBInt8("foo"))
 */
public class MetaArray<T extends Construct> extends Subconstruct {

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
    List<Object> obj = ListContainer();
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
      throw new ArrayError( this.toString() + ": expected " + count + ", found " + c, e );
    }
    val = obj;
    return obj;
  }

  @Override
  public void _build(Object object, ByteArrayOutputStream stream, Container context) {

    if( !(object instanceof List ))
      throw new RuntimeException("Must build a List");

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