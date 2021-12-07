package uk.ziglio.construct.core;

import java.io.ByteArrayOutputStream;

import uk.ziglio.construct.lib.ByteBufferWrapper;
import uk.ziglio.construct.lib.Resizer;
import uk.ziglio.construct.lib.BitStream.BitStreamReader;
import uk.ziglio.construct.lib.BitStream.BitStreamWriter;
import uk.ziglio.construct.lib.Containers.Container;

/**
 * Wraps the stream with a read-wrapper (for parsing) or a write-wrapper (for
 * building). The stream wrapper can buffer the data internally, reading it
 * from- or writing it to the underlying stream as needed. For example,
 * BitByteBufferWrapper reads whole bytes from the underlying stream, but
 * returns them as individual bits. See also Bitwise.
 * 
 * When the parsing or building is done, the stream's close method will be
 * invoked. It can perform any finalization needed for the stream wrapper, but
 * it must not close the underlying stream.
 * 
 * Note: Do not use pointers inside Restream
 * 
 * Example: Restream(BitField("foo", 16), stream_reader =
 * BitByteBufferWrapper, stream_writer = BitStreamWriter, resizer = lambda
 * size: size / 8, )
 */
public class Restream extends Subconstruct {
  BitStreamReader stream_reader;
  BitStreamWriter stream_writer;
  Resizer resizer;

  /**
   * Wraps the stream with a read-wrapper (for parsing) or a write-wrapper
   * (for building). The stream wrapper can buffer the data internally,
   * reading it from- or writing it to the underlying stream as needed. For
   * example, BitByteBufferWrapper reads whole bytes from the underlying
   * stream, but returns them as individual bits. See also Bitwise.<br/>
   * <br/>
   * When the parsing or building is done, the stream's close method will be
   * invoked. It can perform any finalization needed for the stream wrapper,
   * but it must not close the underlying stream.<br/>
   * <br/>
   * Note: Do not use pointers inside Restream
   * 
   * @param subcon
   *          the subcon
   * @param stream_reader
   *          the read-wrapper
   * @param stream_writer
   *          the write wrapper
   * @param resizer
   *          a function that takes the size of the subcon and "adjusts" or
   *          "resizes" it according to the encoding/decoding process.
   */
  public Restream(Construct subcon, BitStreamReader stream_reader,
      BitStreamWriter stream_writer, Resizer resizer) {
    super(subcon);
    this.stream_reader = stream_reader;
    this.stream_writer = stream_writer;
    this.resizer = resizer;
  }

  @Override
  public Object _parse(ByteBufferWrapper stream, Container context) {
    stream_reader.init(stream);
    Object obj = subcon._parse(stream_reader, context);
    stream_reader.close();
    return obj;
  }

  @Override
  public void _build(Object obj, ByteArrayOutputStream stream,
      Container context) {
    ByteArrayOutputStream stream2 = stream_writer.init(stream);
    subcon._build(obj, stream2, context);
    stream_writer.close();
  }

  @Override
  public int _sizeof(Container context) {
    return resizer.resize(subcon._sizeof(context));
  }

}