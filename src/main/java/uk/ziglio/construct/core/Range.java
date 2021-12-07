package uk.ziglio.construct.core;

import static uk.ziglio.construct.lib.Containers.ListContainer;

import java.io.ByteArrayOutputStream;
import java.util.List;

import uk.ziglio.construct.Core;
import uk.ziglio.construct.errors.RangeError;
import uk.ziglio.construct.errors.SizeofError;
import uk.ziglio.construct.errors.TypeError;
import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

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
public class Range<T extends Construct> extends Subconstruct {

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
      e.printStackTrace();
      throw new RangeError(e.getMessage());
    }
  }

  @Override
  public int _sizeof(Container context) {
    throw new SizeofError("can't calculate size");
  }

}