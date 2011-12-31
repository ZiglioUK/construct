package construct.core;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import construct.exception.FieldError;
import construct.exception.SizeofError;
import construct.lib.Container;

/**
 * A fixed-size byte field.
 */
public class StaticField extends Construct
{
    int length;
//    __slots__ = ["length"]
    
    /**
     * @param name field name
     * @param length number of bytes in the field
     */
    public StaticField( String name, int length )
    {
      super( name );
      this.length = length;
    }
   
//    public String _parse( InputStream stream, Object context )
    public Object _parse( String stream, Object context )
    {
      return _read_stream( stream, length );
    }

//    public String _parse( InputStream stream )
//    public String _parse( String stream )
//    {
//      return _parse( stream, null );
//    }

    public Object _parse( String text )
    {
//      InputStream is;
//      try
//      {
//        is = new ByteArrayInputStream(text.getBytes("UTF-8"));
//      } catch( UnsupportedEncodingException e )
//      {
//        throw new FieldError( e.getMessage() );
//      }
//      return _parse( is );
        return _parse( text, null );
    }
//  def _build(self, obj, stream, context):
//  _write_stream(stream, self.length, obj)
//def _sizeof(self, context):
//  return self.length

    @Override
//    void _build( String obj, OutputStream stream, Container context)
    void _build( byte[] obj, StringBuilder stream, Container context)
    {
        _write_stream( stream, length, obj );
    }

    @Override
//    public String _parse( InputStream stream, Container context )
    public Object _parse( String stream, Container context )
    {
      return _read_stream( stream, length );
    }

    public int _sizeof( Container context )
    {
      return length;
    }

}
