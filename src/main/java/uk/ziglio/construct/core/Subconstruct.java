package uk.ziglio.construct.core;

import java.io.ByteArrayOutputStream;

import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Containers.Container;

/**
   * Abstract subconstruct (wraps an inner construct, inheriting its name and
   * flags).
   */
  public abstract class Subconstruct extends Construct {

    protected Construct subcon;

    /**
     * @param subcon
     *          the construct to wrap
     */
    public Subconstruct(Construct subcon) {
      super(subcon.name, subcon.conflags);
      this.subcon = subcon;
    }

    Subconstruct(String name, Construct subcon) {
      super(name, subcon.conflags);
      this.subcon = subcon;
    }

    public Subconstruct clone() throws CloneNotSupportedException {
      Subconstruct s = (Subconstruct) super.clone();
      s.subcon = subcon.clone();
      return s;
    }
    
//    @Override
//    public T get(){
//      return subcon;
//    }
    
    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      return subcon._parse(stream, context);
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      subcon._build(obj, stream, context);
    }

    @Override
    public int _sizeof(Container context) {
      return subcon._sizeof(context);
    }
  }