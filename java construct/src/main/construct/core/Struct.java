package construct.core;

import java.io.InputStream;
import java.io.OutputStream;

import construct.lib.Container;

/**
    A sequence of named constructs, similar to structs in C. The elements are
    parsed and built in the order they are defined.
    See also Embedded.

    Parameters:
    * name - the name of the structure
    * subcons - a sequence of subconstructs that make up this structure.
    * nested - a keyword-only argument that indicates whether this struct
      creates a nested context. The default is True. This parameter is
      considered "advanced usage", and may be removed in the future.

    Example:
    Struct("foo",
        UBInt8("first_element"),
        UBInt16("second_element"),
        Padding(2),
        UBInt8("third_element"),
    )
 */
public class Struct extends Construct
{
  public Struct( String name )
  {
    super( name );
  }

  @Override
  public Object _parse( String stream, Container context )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  void _build( byte[] obj, StringBuilder stream, Container context )
  {
    // TODO Auto-generated method stub
    
  }

}
