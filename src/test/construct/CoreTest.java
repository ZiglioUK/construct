package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static construct.Core.*;

public class CoreTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test 
  public void testDataLength(){
  	assertEquals( 2, Construct.getDataLength("ab"));
  	assertEquals( 3, Construct.getDataLength( new byte[]{1,2,3}));
  	assertEquals( 1, Construct.getDataLength( 255 ));
  	assertEquals( 2, Construct.getDataLength( 256 ));
  	assertEquals( 4, Construct.getDataLength( 0x01ABCDEF ));
  }

  @Test
  public void testStaticField() {
    StaticField sf = new StaticField("staticfield", 2);
    
    assertArrayEquals( "ab".getBytes(), (byte[])sf.parse("ab") );

    assertArrayEquals( new byte[]{ 'a','b' }, sf.build("ab") );

    exception.expect( FieldError.class );
    sf.parse("a");

    exception.expect( FieldError.class );
    sf.build("a");

    assertEquals( 2, sf.sizeof() );
  }

  @Test
  public void testFormatField(){

    FormatField ff = new FormatField("formatfield", '<', 'L');

    assertEquals( new Integer(0x78563412), ff.parse(new byte[]{0x12, 0x34, 0x56, 0x78}) );

    assertArrayEquals( new byte[]{0x12, 0x34, 0x56, 0x78}, ff.build(0x78563412) );

    assertEquals(4, ff.sizeof());

    exception.expect( FieldError.class );
    ff.parse(new byte[]{0x12, 0x34, 0x56});

    exception.expect( FieldError.class );
    ff.build(9^9999);

  }
}

