package construct.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.management.RuntimeErrorException;

import construct.exception.FieldError;
import construct.exception.SizeofError;
import construct.lib.Container;

/**
    The mother of all constructs.

    This object is generally not directly instantiated, and it does not
    directly implement parsing and building, so it is largely only of interest
    to subclass implementors.

    The external user API:

     * parse()
     * parse_stream()
     * build()
     * build_stream()
     * sizeof()

    Subclass authors should not override the external methods. Instead,
    another API is available:

     * _parse()
     * _build()
     * _sizeof()

    There is also a flag API:

     * _set_flag()
     * _clear_flag()
     * _inherit_flags()
     * _is_flag()

    And stateful copying:

     * __getstate__()
     * __setstate__()

    Attributes and Inheritance
    ==========================

    All constructs have a name and flags. The name is used for naming struct
    members and context dictionaries. Note that the name can either be a
    string, or None if the name is not needed. A single underscore ("_") is a
    reserved name, and so are names starting with a less-than character ("<").
    The name should be descriptive, short, and valid as a Python identifier,
    although these rules are not enforced.

    The flags specify additional behavioral information about this construct.
    Flags are used by enclosing constructs to determine a proper course of
    action. Flags are inherited by default, from inner subconstructs to outer
    constructs. The enclosing construct may set new flags or clear existing
    ones, as necessary.

    For example, if FLAG_COPY_CONTEXT is set, repeaters will pass a copy of
    the context for each iteration, which is necessary for OnDemand parsing.
 */
public abstract class Construct
{
  char[] conflags;
  public String name;

  public Construct( String name ) 
  {
    if( name.equals( "_" ) || name.startsWith("<") )
      throw new FieldError( "reserved name " + name ); //  raise ValueError
          
    this.name = name;
  }

  public Construct( String name, char[] flags ) 
  {
    if( name.equals( "_" ) || name.startsWith("<") )
      throw new FieldError( "reserved name " + name ); //  raise ValueError
          
    this.name = name;
    this.conflags = flags;
  }
  
//  public String _read_stream( InputStream stream, int length )
  public String _read_stream( String stream, int length )
  {
    if( length < 0 )
        throw new FieldError("length must be >= 0 " + length); 
//    try
    {
//      byte[] data = new byte[length];
//      int len = stream.read( data, 0, length );
      byte[] data = stream.getBytes();
      int len = stream.length();
      if( len != length )
        throw new FieldError( "expected " + length + " found " + len );
      return new String( data );
    } 
//    catch( IOException e )
//    {
//      throw new FieldError( e.getMessage() );
//    }
    
//        len(data) != length:
//        raise FieldError("expected %d, found %d" % (length, len(data)))
  }
  
//  public void _write_stream( OutputStream stream, int length, byte[] data)
  public void _write_stream( StringBuilder stream, int length, byte[] data)
  {
    if( length < 0 )
        throw new FieldError( "length must be >= 0 " + length );
    if( data.length != length )
      throw new FieldError( "expected " + length + " found " + data.length );
    
//    try
    {
//      stream.write(data);
      stream.append( new String(data) );
    } 
//    catch( IOException e )
//    {
//      throw new FieldError( e.getMessage() );
//    }
  };

  /**
    Parse an in-memory buffer.

    Strings, buffers, memoryviews, and other complete buffers can be
    parsed with this method.
   * @param data
   */
  public Object parse( String data )
  {
//    return parse_stream( new ByteArrayInputStream(data.getBytes()) );
    return parse_stream( data );
  }

  /**
    Parse a stream.

    Files, pipes, sockets, and other streaming sources of data are handled
    by this method.
   */
//  public String parse_stream( InputStream stream )
  public Object parse_stream( String stream )
  {
    return _parse( stream, new Container());
  }
  
//  abstract public String _parse( InputStream stream, Container context );
  abstract public Object _parse( String stream, Container context );
  
  /**
   * Build an object in memory.
   * @param obj
   * @return
   */
  public String build( String obj )
  {
//    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    StringBuilder stream = new StringBuilder();
    //    build_stream( obj, stream );
    build_stream( obj, stream );
    
//    return new String( stream.toByteArray() );
    return stream.toString();
  }

  /**
   * Build an object directly into a stream.
   * @param obj
   * @param stream
   */
//  public void build_stream( String obj, OutputStream stream)
  public void build_stream( String obj, StringBuilder stream)
  {
    _build(obj, stream, new Container());
  }
  
//  abstract void _build( String obj, OutputStream stream, Container context);
  abstract void _build( String obj, StringBuilder stream, Container context);

    /**
     *     Calculate the size of this object, optionally using a context.
    Some constructs have no fixed size and can only know their size for a
    given hunk of data; these constructs will raise an error if they are
    not passed a context.
     * @param context contextual data
     * @return the length of this construct
     */
    public int sizeof( Container context )
    {
      if( context == null )
      {
        context = new Container();
      }
      try
      {
        return _sizeof(context);
      }
      catch( Exception e )
      {
        throw new SizeofError(e);
      }
    }

    public int sizeof()
    {
      return sizeof( null );
    }
    
    /*abstract*/ public int _sizeof( Container context )
    {
      throw new SizeofError("Raw Constructs have no size!");
    }
}
