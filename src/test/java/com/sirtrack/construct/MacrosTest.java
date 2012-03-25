package com.sirtrack.construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sirtrack.construct.Adapters.MappingError;
import com.sirtrack.construct.Core.Adapter;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.KeyFunc;
import com.sirtrack.construct.lib.Containers.Container;
import static com.sirtrack.construct.lib.Containers.*;

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
  	
  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
  	assertEquals( 'r', a.parse(ByteArray(4)));

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", "spam");
  	assertEquals( "spam", a.parse(ByteArray(7)));

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", Pass );
  	assertEquals( 7, a.parse(ByteArray(7)));

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
  	assertArrayEquals( ByteArray(4), a.build('r'));

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", 9);
  	assertArrayEquals( ByteArray(9), a.build("spam"));

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5, "_default_", Pass);
  	assertArrayEquals( ByteArray(9), a.build(9));
  	
  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
    exception.expect( MappingError.class );
  	a.build("spam");

  	a = Enum( UBInt8("enum"), 'q',3,'r',4,'t',5);
    exception.expect( MappingError.class );
    a.parse(ByteArray(7));
  }

  @Test
  public void PrefixedArrayTest(){
  	assertEquals( ListContainer(1,1,1), PrefixedArray(UBInt8("array"), UBInt8("count")).parse(ByteArray(3,1,1,1)));

  	assertEquals( ByteArray(3,1,1,1), PrefixedArray(UBInt8("array"), UBInt8("count")).build(ListContainer(1,1,1)));

    exception.expect( ArrayError.class );
  	assertEquals( ListContainer(1,1,1), PrefixedArray(UBInt8("array"), UBInt8("count")).parse(ByteArray(3,1,1)));

//    [PrefixedArray(UBInt8("array"), UBInt8("count")).build, [1,1,1], "\x03\x01\x01\x01", None],
  }
  
  @Test
  public void ifThenElseTest(){
  	Switch ifThenElse;
  	
  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object key(Container context){return true;}}, UBInt8("then"), UBInt16("else") );
  	assertEquals(1, ifThenElse.parse(ByteArray(1)));

  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object key(Container context){return false;}}, UBInt8("then"), UBInt16("else") );
  	assertEquals(1, ifThenElse.parse(ByteArray(0,1)));

  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object key(Container context){return true;}}, UBInt8("then"), UBInt16("else") );
  	assertArrayEquals(ByteArray(1), (byte[])ifThenElse.build(1));

  	ifThenElse = IfThenElse("ifthenelse", new KeyFunc(){ public Object key(Container context){return false;}}, UBInt8("then"), UBInt16("else") );
  	assertArrayEquals(ByteArray(0,1), (byte[])ifThenElse.build(1));
  }
}

