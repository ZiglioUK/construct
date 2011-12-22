package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.core.FormatField;
import construct.core.StaticField;
import construct.exception.FieldError;
import junit.framework.TestCase;

public class CoreTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testStaticField() {
    StaticField sf = new StaticField("staticfield", 2);
    
    assertEquals( "ab", sf._parse("ab") );

    assertEquals( "ab", sf.build("ab") );

    exception.expect( FieldError.class );
    sf.parse("a");

    exception.expect( FieldError.class );
    sf.build("a");

    assertEquals( 2, sf.sizeof() );
  }

  @Test
  public void testFormatField(){

    FormatField ff = new FormatField("formatfield", '<', "L");

    assertEquals( 0x78563412, ff.parse("\\x12\\x34\\x56\\x78") );

//    def test_build(self):
//        self.assertEqual(self.ff.build(0x78563412), "\x12\x34\x56\x78")
//
//    def test_parse_too_short(self):
//        self.assertRaises(FieldError, self.ff.parse, "\x12\x34\x56")
//
//    def test_build_too_long(self):
//        self.assertRaises(FieldError, self.ff.build, 9e9999)
//
//    def test_sizeof(self):
//        self.assertEqual(self.ff.sizeof(), 4)
  }
}

