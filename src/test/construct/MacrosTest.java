package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static construct.Adapters.BitIntegerAdapter;
import static construct.Core.*;
import static construct.Macros.*;
import static construct.Macros.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.Adapters.BitIntegerError;
import construct.Adapters.MappingError;
import construct.Core.Construct;

public class MacrosTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void BitIntegerAdapterTest() {
  	Construct bw;
    
    bw = Bitwise( Field("bitwise",8) );
    assertArrayEquals( new byte[]{1,1,1,1,1,1,1,1}, (byte[]) bw.parse( new byte[]{ (byte) 0xFF } ));
    assertEquals( (byte)0xFF, bw.build(new byte[]{1,1,1,1,1,1,1,1})[0]);
    
/*    
    [Bitwise(Field("bitwise", lambda ctx: 8)).parse, "\xff", "\x01" * 8, None],
    [Bitwise(Field("bitwise", lambda ctx: 8)).build, "\x01" * 8, "\xff", None],
*/
  }
  
  @Test 
  public void EnumTest(){
  	Adapter a;
  	
  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5)));
  	assertEquals( 'r', a.parse(new byte[]{4}));

  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5), P("_default_", "spam")));
  	assertEquals( "spam", a.parse(new byte[]{7}));

  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5), P("_default_", Pass )));
  	assertEquals( 7, a.parse(new byte[]{7}));

  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5)));
  	assertArrayEquals( new byte[]{4}, a.build('r'));

  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5), P("_default_", 9)));
  	assertArrayEquals( new byte[]{9}, a.build("spam"));

  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5), P("_default_", Pass)));
  	assertArrayEquals( new byte[]{9}, a.build(9));
  	
  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5)));
    exception.expect( MappingError.class );
  	a.build("spam");

  	a = Enum( UBInt8("enum"), Container(P('q',3), P('r',4), P('t',5)));
    exception.expect( MappingError.class );
    a.parse(new byte[]{7});
  }
}

