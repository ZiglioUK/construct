package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static construct.Core.*;
import static construct.Macros.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.Adapters.MappingError;
import construct.Core.Adapter;
import construct.Core.Construct;

public class MacrosTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void BitIntegerAdapterTest() {
  	Construct bw;
    
    bw = Bitwise( Field("bitwise",8) );
    assertArrayEquals( ByteArray(1,1,1,1,1,1,1,1), (byte[]) bw.parse( ByteArray( 0xFF )));
    assertEquals( (byte)0xFF, bw.build(ByteArray(1,1,1,1,1,1,1,1))[0]);
    
/*    
    [Bitwise(Field("bitwise", lambda ctx: 8)).parse, "\xff", "\x01" * 8, None],
    [Bitwise(Field("bitwise", lambda ctx: 8)).build, "\x01" * 8, "\xff", None],
*/
  }
  
  @Test 
  public void EnumTest(){
  	Adapter a;
  	
  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5));
  	assertEquals( 'r', a.parse(ByteArray(4)));

  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5, "_default_", "spam"));
  	assertEquals( "spam", a.parse(ByteArray(7)));

  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5, "_default_", Pass ));
  	assertEquals( 7, a.parse(ByteArray(7)));

  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5));
  	assertArrayEquals( ByteArray(4), a.build('r'));

  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5, "_default_", 9));
  	assertArrayEquals( ByteArray(9), a.build("spam"));

  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5, "_default_", Pass));
  	assertArrayEquals( ByteArray(9), a.build(9));
  	
  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5));
    exception.expect( MappingError.class );
  	a.build("spam");

  	a = Enum( UBInt8("enum"), Container('q',3,'r',4,'t',5));
    exception.expect( MappingError.class );
    a.parse(ByteArray(7));
  }
}

