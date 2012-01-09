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
import static construct.lib.Containers.*;
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

    exception.expect( BitIntegerError.class );
    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8 );
    assertEquals( null, ba.build(-1) );

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, false, true );
    assertArrayEquals( new byte[]{1,1,1,1,1,1,1,1}, ba.build(-1) );

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, true, false, 4 );
    assertArrayEquals( new byte[]{1,1,1,1,0,0,0,0}, ba.build(0x0f) );

  }

  @Test
  public void MappingAdapterTest(){
  	Adapter ma;
  	
  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), null, null);
  	assertEquals( "y", ma.parse(new byte[]{3}));
  	
  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), "foo", null );
  	assertEquals( "foo", ma.parse(new byte[]{4}));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), Pass(), null );
  	assertEquals( 4, ma.parse(new byte[]{4}));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), null, null);
  	assertArrayEquals( new byte[]{3}, ma.build("y"));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), null, 17);
  	assertArrayEquals( new byte[]{17}, ma.build("foo"));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), null, Pass());
  	assertArrayEquals( new byte[]{4}, ma.build(4));
  	
  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), null, null);
    exception.expect( MappingError.class );
  	ma.build("z");

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( P(2,"x"), P(3,"y")), Container( P("x",2), P("y",3)), null, null);
    exception.expect( MappingError.class );
  	ma.parse(new byte[]{4});

  }

  @Test
  public void PaddingAdapterTest(){
  	assertArrayEquals( "abcd".getBytes(), (byte[])PaddingAdapter( Field("paddingadapter", 4) ).parse("abcd"));

    exception.expect( PaddingError.class );
  	assertArrayEquals( "abcd".getBytes(), (byte[])PaddingAdapter( Field("paddingadapter", 4), (byte)0x00, true ).parse("abcd"));

  	assertArrayEquals( new byte[]{0,0,0,0}, (byte[])PaddingAdapter( Field("paddingadapter", 4), (byte)0x00, true ).parse( new byte[]{0,0,0,0}));

  	assertArrayEquals( new byte[]{0,0,0,0}, (byte[])PaddingAdapter( Field("paddingadapter", 4) ).build("abcd"));
  	
  }
}

