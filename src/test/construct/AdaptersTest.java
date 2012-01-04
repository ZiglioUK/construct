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

  @Test
  public void BitIntegerAdapterTest() {
    Adapter ba;

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8 );
    assertEquals( 255, ba.parse( new byte[]{1,1,1,1,1,1,1,1} ));

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, false, true );
    assertEquals( -1, ba.parse( new byte[]{1,1,1,1,1,1,1,1} ));

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, true, false, 4 );
    assertEquals( 0x0f, ba.parse( new byte[]{1,1,1,1,0,0,0,0} ));

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8 );
    assertArrayEquals( new byte[]{1,1,1,1,1,1,1,1}, ba.build(255) );

//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8).build, 255, "\x01" * 8, None],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8).build, -1, None, BitIntegerError],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8, signed = True).build, -1, "\x01" * 8, None],
//  [BitIntegerAdapter(Field("bitintegeradapter", 8), 8, swapped = True, bytesize = 4).build, 
//      0x0f, "\x01" * 4 + "\x00" * 4, None],

    
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

}

