package construct.core;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import construct.exception.FieldError;
import construct.exception.SizeofError;
import construct.exception.ValueError;
import construct.lib.Container;
import java.util.Scanner;

/**
    A field that uses ``struct`` to pack and unpack data.

    See ``struct`` documentation for instructions on crafting format strings.
 */
public class FormatField extends StaticField
{
  int length;
//    __slots__ = ["length"]
  Packer packer;
    
    /**
     * @param name name of the field
     * @param endianness: format endianness string; one of "<", ">", or "="
     * @param format: a single format character
     */
    public FormatField( String name, char endianity, String format )
    {
      super( name, 0 );
      
      if( endianity != '>' && endianity != '<' && endianity != '=' )
        throw new ValueError("endianity must be be '=', '<', or '>' " + endianity );

      packer = new Packer( endianity, format );

      this.name = name;
      this.length = packer.length();

    }
   
    @Override
//    public String _parse( InputStream stream, Container context )
    public Object _parse( String stream, Container context )
    {
      try
      {
//        return packer.unpack( _read_stream(stream, length) )[0];
        return (packer.unpack( stream )[0]);
      }
      catch( Exception e )
      {
        throw new FieldError(e);
      }
    }

    @Override
//    void _build( String obj, OutputStream stream, Container context)
    void _build( String obj, String stream, Container context)
    {
        _write_stream( stream, length, obj.getBytes() );
//        def _build(self, obj, stream, context):
//          try:
//              _write_stream(stream, self.length, self.packer.pack(obj))
//          except Exception, ex:
//              raise FieldError(ex)
    }
}
