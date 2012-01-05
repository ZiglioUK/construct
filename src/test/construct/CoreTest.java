package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static construct.Core.*;
import construct.exception.FieldError;
import junit.framework.TestCase;

public class CoreTest  
{
  @Rule
  static public ExpectedException exception = ExpectedException.none();

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
    
    assertEquals( "ab", new String((byte[])sf.parse("ab")) );

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

    assertEquals( new Integer(0x78563412), (Integer)ff.parse(new byte[]{0x12, 0x34, 0x56, 0x78}) );

    assertArrayEquals( new byte[]{0x12, 0x34, 0x56, 0x78}, ff.build(0x78563412) );

//    exception.expect( FieldError.class );
//    ff.parse( "\\x12\\x34\\x56" );
//
//    def test_build_too_long(self):
//        self.assertRaises(FieldError, self.ff.build, 9e9999)
//
//    def test_sizeof(self):
//        self.assertEqual(self.ff.sizeof(), 4)
  }
}

