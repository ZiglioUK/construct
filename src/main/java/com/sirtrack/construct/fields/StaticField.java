package com.sirtrack.construct.fields;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.interfaces.LengthConstruct;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Containers.Container;

/**
 * A fixed-size byte field.
 */
public class StaticField extends Construct implements LengthConstruct {
  int length;

  public StaticField(String name ) {
    super(name);
    
//    if (!StaticField.class.isAnnotationPresent(len.class)) 
//      throw new RuntimeException("@len is missing");
//      
//      // getAnnotation returns Annotation type
//      this.length = StaticField.class.getAnnotation(len.class).value();  
  }

  /**
   * @param name
   *          field name
   * @param length
   *          number of bytes in the field
   */
  public StaticField(String name, int length) {
    super(name);
    this.length = length;
  }

  @Override
  public void setLength(int length) {
    this.length = length;
  }
  
  @Override
  public Object _parse(ByteBufferWrapper stream, Container context) {
    return _read_stream(stream, length);
  }

  @Override
  public void _build(Object obj, ByteArrayOutputStream stream, Container context) {
    _write_stream(stream, length, obj);
  }

  @Override
  public int _sizeof(Container context) {
    return length;
  }


  /*
   * public int _sizeof( Container context ){
@Override
public Construct clone() {
// TODO Auto-generated method stub
return null;
} return length; }
   */
}

