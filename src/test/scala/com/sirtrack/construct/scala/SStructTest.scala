package com.sirtrack.construct.scala
import com.sirtrack.construct.Core.Struct
import com.sirtrack.construct.Core.ByteArray
import com.sirtrack.construct.Core.{ Container=> C }
import com.sirtrack.construct.Macros._
import org.junit._
import org.junit.Assert._
import com.sirtrack.construct.lib.Containers.Container

@Test
class SStructTest {
  
//  /**
//  * @return unsigned, big endian 8-bit integer
//  */
//  case class UBInt8(name: String ) extends FormatField(name, '>', 'B')
//
//  /**
//  * @return unsigned, big endian 16-bit integer
//  */
//  case class UBInt16(name: String) extends FormatField( name, '>', 'H' )
//
//  /**
// * embeds a struct into the enclosing struct.
// * @param subcon the struct to embed
// * @return
// */
//  case class Embedded( subcon: Construct ) extends Reconfig( subcon.name, subcon, subcon.FLAG_EMBED, 0 );

    @Test
    def testOK() {
      assertTrue(true)
    }

  @Test
  def structTest() {
      var struct = Struct( "struct", UBInt8("a"), UBInt16("b") )
      var ca = struct.parse( ByteArray(1,0,2) ) : Container
      var cb = C( "a", 1:Integer, "b", 2:Integer )
      assertTrue( ca.equals(cb) )

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), 
               Struct( "foo", UBInt8("c"), UBInt8("d") ))
      ca = struct.parse( ByteArray(1,0,2,3,4) )
      cb = C( "a",1:Integer, "b",2:Integer, "foo", C( "c",3: Integer,"d",4: Integer))
      assertTrue( ca.equals(cb) )

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"))
      var ba = struct.build( C( "a", 1:Integer, "b", 2:Integer))
      assertArrayEquals( ByteArray(1,0,2), ba )

      var foo = Struct( "foo", UBInt8("c"), UBInt8("d") )
      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), foo )
      ba = struct.build( C( "a",1:Integer, "b", 2:Integer, "foo", C("c", 3:Integer, "d",4:Integer)))
      assertArrayEquals( ByteArray(1:Integer,0,2:Integer,3,4), ba )
      
      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded( Struct("foo", UBInt8("c"), UBInt8("d"))))
      ca = struct.parse( ByteArray(1:Integer,0,2:Integer,3,4) )
      cb = C( "a", 1:Integer, "b", 2:Integer, "c", 3:Integer, "d", 4:Integer )
      assertEquals( cb, ca )

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded( Struct("foo", UBInt8("c"), UBInt8("d"))))
      ba = struct.build( C( "a", 1:Integer, "b", 2:Integer, "c", 3:Integer, "d", 4:Integer ))
      assertArrayEquals( ByteArray(1:Integer,0,2:Integer,3,4), ba )
  }

  @Test
  def sstructTest() {
      var struct = new SStruct( "struct", UBInt8("a"), UBInt16("b") )
      var ca = struct.parse( ByteArray(1,0,2) ) : Container
      var cb = C( "a", 1:Integer, "b", 2:Integer )
      assertTrue( ca.equals(cb) )

      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), 
               SStruct( "foo", UBInt8("c"), UBInt8("d") ))
      ca = struct.parse( ByteArray(1,0,2,3,4) )
      cb = C( "a",1:Integer, "b",2:Integer, "foo", C( "c",3: Integer,"d",4: Integer))
      assertTrue( ca.equals(cb) )

      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"))
      var ba = struct.build( C( "a", 1:Integer, "b", 2:Integer))
      assertArrayEquals( ByteArray(1,0,2), ba )

      var foo = SStruct( "foo", UBInt8("c"), UBInt8("d") )
      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), foo )
      ba = struct.build( C( "a",1:Integer, "b", 2:Integer, "foo", C("c", 3:Integer, "d",4:Integer)))
      assertArrayEquals( ByteArray(1:Integer,0,2:Integer,3,4), ba )
      
      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), Embedded( SStruct("foo", UBInt8("c"), UBInt8("d"))))
      ca = struct.parse( ByteArray(1:Integer,0,2:Integer,3,4) )
      cb = C( "a", 1:Integer, "b", 2:Integer, "c", 3:Integer, "d", 4:Integer )
      assertEquals( cb, ca )

      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), Embedded( SStruct("foo", UBInt8("c"), UBInt8("d"))))
      ba = struct.build( C( "a", 1:Integer, "b", 2:Integer, "c", 3:Integer, "d", 4:Integer ))
      assertArrayEquals( ByteArray(1:Integer,0,2:Integer,3,4), ba )
  }
}