package com.sirtrack.construct;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.Subconstruct;
import com.sirtrack.construct.interfaces.AdapterDecoder;
import com.sirtrack.construct.interfaces.AdapterEncoder;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Containers.Container;

/**
 * """ Abstract adapter: calls _decode for parsing and _encode for building. """
 * 
 */
public abstract class Adapter<V,T> extends Subconstruct implements AdapterEncoder<V,T>, AdapterDecoder<T,V> {

	/**
   * @param name
   * @param subcon
   *          the construct to wrap
   */
  public Adapter(Construct subcon ) {
    super(subcon);
  }

@Override
  public Object _parse( ByteBufferWrapper stream, Container context) {
    val = decode( (T) subcon._parse( stream, context ), context);
    return val;
  }

  @Override
  public void _build( Object obj, ByteArrayOutputStream stream, Container context) {
    subcon._build(encode( (V) obj, context), stream, context);
  }

  @Override
  public V get(){
    return (V) val;
  }

//  @Override
//  public void set( V val ){
//    set(val);
//  }
  
}
