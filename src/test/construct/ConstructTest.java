package construct;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.Core.RangeError;

import static construct.Core.*;
import static construct.Macros.*;
import static construct.lib.Containers.*;

public class ConstructTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void rangeTest(){
  	Range range;
  	
  	range = Range(3,5, UBInt8("range"));
  	assertEquals( ListContainer(1,2,3), range.parse( ByteArray(1,2,3)));
  	assertEquals( ListContainer(1,2,3,4), range.parse( ByteArray(1,2,3,4)));
  	assertEquals( ListContainer(1,2,3,4,5), range.parse( ByteArray(1,2,3,4,5)));

  	assertArrayEquals( ByteArray(1,2,3), range.build( ListContainer(1,2,3)));
  	assertArrayEquals( ByteArray(1,2,3,4), range.build( ListContainer(1,2,3,4)));
  	assertArrayEquals( ByteArray(1,2,3,4,5), range.build( ListContainer(1,2,3,4,5)));

  	exception.expect( RangeError.class );
  	range.build( ListContainer(1,2,3,4,5,6));

  	exception.expect( RangeError.class );
  	range.build( ListContainer(1,2));
  			
  	exception.expect( RangeError.class );
  	range.parse( ByteArray(1,2));
  	
  /*
    [Range(3, 5, UBInt8("range")).build, [1,2,3,4,5,6], None, RangeError],
   */
  }
  
  @Test
  public void structTest() {
  	  Struct struct, foo;
  	  Container ca, cb;
  	  byte[] ba;
  	  
  	  struct = Struct( "struct", UBInt8("a"), UBInt16("b") );
  	  ca = struct.parse( ByteArray(1,0,2) );
  	  cb = Container( "a", 1, "b", 2 );
      assertTrue( ca.equals(cb) );

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), 
  	  									Struct( "foo", UBInt8("c"), UBInt8("d") ));
  	  ca = struct.parse( ByteArray(1,0,2,3,4) );
  	  cb = Container( "a",1, "b",2, "foo", Container( "c",3,"d",4));
      assertTrue( ca.equals(cb) );

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"));
  	  ba = struct.build( Container( "a",1, "b", 2));
  	  assertArrayEquals( ByteArray(1,0,2), ba );

  	  foo = Struct( "foo", UBInt8("c"), UBInt8("d") );
  	  struct = Struct( "struct", UBInt8("a"), UBInt16("b"), foo );
  	  ba = struct.build( Container( "a",1, "b", 2, "foo", Container("c", 3, "d",4)));
  	  assertArrayEquals( ByteArray(1,0,2,3,4), ba );
  	  
  	  struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded( Struct("foo", UBInt8("c"), UBInt8("d"))));
  	  ca = struct.parse( ByteArray(1,0,2,3,4) );
  	  cb = Container( "a", 1, "b", 2, "c", 3, "d", 4 );
  	  assertEquals( cb, ca );

  	  struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded( Struct("foo", UBInt8("c"), UBInt8("d"))));
  	  ba = struct.build( Container( "a", 1, "b", 2, "c", 3, "d", 4 ));
  	  assertArrayEquals( ByteArray(1,0,2,3,4), ba );
	  
  }
  
  @Test
  public void reconfigTest(){
  	Container c1 = Container("foo",1);
  	Object c2 = Struct("reconfig", Reconfig("foo", UBInt8("bar"))).parse(ByteArray(1));
  	assertEquals( c1, c2 );
  	
  	byte[] ba = Struct("reconfig", Reconfig("foo", UBInt8("bar"))).build( Container("foo", 1));
  	assertArrayEquals( ByteArray(1), ba);
  }
  
}

