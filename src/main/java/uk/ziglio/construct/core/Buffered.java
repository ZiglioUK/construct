package uk.ziglio.construct.core;

import java.io.ByteArrayOutputStream;

import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Decoder;
import uk.ziglio.construct.lib.Encoder;
import uk.ziglio.construct.lib.Resizer;
import uk.ziglio.construct.lib.Containers.Container;

/*
   * #============================================================================
   * === # stream manipulation
   * #==================================================
   * =============================
   */
  /**
   * Creates an in-memory buffered stream, which can undergo encoding and
   * decoding prior to being passed on to the subconstruct. See also Bitwise.
   * 
   * Note: Do not use pointers inside Buffered
   * 
   * Example: Buffered(BitField("foo", 16), encoder = decode_bin, decoder =
   * encode_bin, resizer = lambda size: size / 8, )
   */
  public class Buffered extends Subconstruct {
    public Encoder encoder;
    public Decoder decoder;
    public Resizer resizer;

    /**
     * Creates an in-memory buffered stream, which can undergo encoding and
     * decoding prior to being passed on to the subconstruct. See also Bitwise.<br/>
     * <br/>
     * Note: Do not use pointers inside Buffered
     * 
     * @param subcon
     *          the subcon which will operate on the buffer
     * @param encoder
     *          a function that takes a string and returns an encoded string
     *          (used after building)
     * @param decoder
     *          a function that takes a string and returns a decoded string
     *          (used before parsing)
     * @param resizer
     *          a function that takes the size of the subcon and "adjusts" or
     *          "resizes" it according to the encoding/decoding process.
     */
    public Buffered(Construct subcon, Encoder encoder, Decoder decoder, Resizer resizer) {
      super(subcon);
      this.encoder = encoder;
      this.decoder = decoder;
      this.resizer = resizer;
    }

//    @Override
//    public T get(){
//      return subcon;
//    }
    
//    @Override
//    public void set( Object val ){
//      subcon.set(val);
//    }

    @Override
    public Object _parse(ByteBufferWrapper stream, Container context) {
      Boolean debug = context.get("debug");
      byte[] data = _read_stream(stream, _sizeof(context));
      if( debug != null && debug==true) {
        for( byte b : data ){
          System.out.print( String.format("%02x ", b ));
        }
        System.out.print( ": " );
      }
      
      byte[] stream2 = decoder.decode(data);
//      if( debug ){
//      System.out.print( Arrays.toString(stream2) + ": ");
//      }

      Object val = subcon._parse(new ByteBufferWrapper().wrap(stream2), context);

      if( debug != null && debug ){
        System.out.println(val);
      }
      return val;
    }

    @Override
    public void _build(Object obj, ByteArrayOutputStream stream,
        Container context) {
      int size = _sizeof(context);
      ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
      subcon._build(obj, stream2, context);
      byte[] data = encoder.encode(stream2.toString());
      if (data.length != size)
        throw new RuntimeException("Wrong data length: " + data.length);
      _write_stream(stream, size, data);
    }

    @Override
    public int _sizeof(Container context) {
      return resizer.resize(
          subcon._sizeof(context));
    }
  }