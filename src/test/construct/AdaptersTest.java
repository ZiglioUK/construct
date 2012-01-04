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
import static construct.Adapters.*;
import static construct.Macros.*;
import construct.exception.FieldError;
import junit.framework.TestCase;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.exception.ValueError;

public class AdaptersTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8).parse, "\x01" * 8, 255, None],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8, signed = True).parse, "\x01" * 8, -1, None],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8, swapped = True, bytesize = 4).parse, 
//      "\x01" * 4 + "\x00" * 4, 0x0f, None],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8).build, 255, "\x01" * 8, None],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8).build, -1, None, BitIntegerError],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8, signed = True).build, -1, "\x01" * 8, None],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8, swapped = True, bytesize = 4).build, 
//      0x0f, "\x01" * 4 + "\x00" * 4, None],

  @Test
  public void BitIntegerAdapterTest() {
    Adapter ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8 );
    assertEquals( 255, ba.parse( new byte[]{1,1,1,1,1,1,1,1} ));
    
//    StaticField sf = new StaticField("staticfield", 2);
//    
//    assertEquals( "ab", sf._parse("ab") );
//
//    assertEquals( "ab", sf.build("ab") );
//
//    exception.expect( FieldError.class );
//    sf.parse("a");
//
//    exception.expect( FieldError.class );
//    sf.build("a");
//
//    assertEquals( 2, sf.sizeof() );
  }

  @Test
  public void testFormatField(){

    FormatField ff = new FormatField("formatfield", '<', 'L');

    assertEquals( 0x78563412, ff.parse( new byte[]{ 0x12, 0x34, 0x56, 0x78 }));

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

