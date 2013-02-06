package com.sirtrack.construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sirtrack.construct.Core.LengthFunc;
import com.sirtrack.construct.Core.MetaField;
import com.sirtrack.construct.lib.Containers.Container;

import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;

public class CoreTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test 
  public void testDataLength(){
  	assertEquals( 2, Construct.getDataLength("ab"));
  	assertEquals( 3, Construct.getDataLength( ByteArray(1,2,3)));
  	assertEquals( 1, Construct.getDataLength( 255 ));
  	assertEquals( 2, Construct.getDataLength( 256 ));
  	assertEquals( 4, Construct.getDataLength( 0x01ABCDEF ));
  }

  @Test
  public void testStaticField() {
    StaticField sf = new StaticField("staticfield", 2);
    
    assertArrayEquals( ByteArray(0xab, 0xcd), (byte[])sf.parse("abcd") );

    assertArrayEquals( ByteArray( 'a','b' ), sf.build("ab") );

    exception.expect( FieldError.class );
    sf.parse("ab");

    exception.expect( FieldError.class );
    sf.build("a");

    assertEquals( 2, sf.sizeof() );
  }

  @Test
  public void testFormatField(){

    FormatField ff = new FormatField("formatfield", '<', 'L');

    assertEquals( new Integer(0x78563412), ff.parse(ByteArray(0x12, 0x34, 0x56, 0x78)) );

    assertArrayEquals( ByteArray(0x12, 0x34, 0x56, 0x78), ff.build(0x78563412) );

    assertEquals(4, ff.sizeof());

    exception.expect( FieldError.class );
    ff.parse(ByteArray(0x12, 0x34, 0x56));

    exception.expect( FieldError.class );
    ff.build(9^9999);
  }

  @Test
  public void testMetaField(){
  	MetaField mf = MetaField("metafield", new LengthFunc(){
  		public int length(Container context){
  			return 2;
  		}
  	});

  	assertArrayEquals( ByteArray(0xab, 0xcd), (byte[])mf.parse("abcd") );
    assertArrayEquals( ByteArray('a','b'), mf.build("ab") );
    assertEquals( 2, mf.sizeof());
    
    exception.expect( FieldError.class );
  	mf.build("ab");

  	exception.expect( FieldError.class );
  	mf.parse("ab");
 }

  @Test
  public void testMetaFieldStruct(){
  	MetaField mf = MetaField("data", LengthField("length"));
  	Construct s = Struct("foo", UBInt8("length"), mf );
  	Container c;
  	
  	c = s.parse(ByteArray( 3, 'A', 'B', 'C'));
  	assertEquals(3, c.get("length"));
  	assertArrayEquals( "ABC".getBytes(), (byte[])c.get("data"));

  	c = s.parse(ByteArray( 4, 'A', 'B', 'C', 'D'));
  	assertEquals(4, c.get("length"));
  	assertArrayEquals( "ABCD".getBytes(), (byte[])c.get("data"));
 
  	Container context = Container("length", 4);
  	assertEquals(4, mf.sizeof(context));
  	
    exception.expect( SizeofError.class );
  	mf.sizeof();
  }
}

