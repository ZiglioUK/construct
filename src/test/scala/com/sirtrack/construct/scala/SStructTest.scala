package com.sirtrack.construct.scala
import com.sirtrack.construct.Core.Struct
import com.sirtrack.construct.Core.ByteArray
import com.sirtrack.construct.Core.FormatField
//import com.sirtrack.construct.Core.{ Container=> C }
import com.sirtrack.construct.Macros._
import org.junit._
import org.junit.Assert._
import com.sirtrack.construct.lib.Containers.Container

@Test
class SStructTest {
  
//  /**
//
//  /**
// * embeds a struct into the enclosing struct.
// * @param subcon the struct to embed
// * @return
// */
//  case class Embedded( subcon: Construct ) extends Reconfig( subcon.name, subcon, subcon.FLAG_EMBED, 0 );
  
  object C{
//    implicit def javaToScalaInt(d: java.lang.Integer) = d.intValue
//    implicit def scalaToJavaInteger(d: Int ) = d.asInstanceOf[java.lang.Integer]
    def apply(pairs: Any*) = new Container(pairs:_*)
  } 

  @Test
  def structTest() {
      var struct = Struct( "struct", UBInt8("a"), UBInt16("b") )
      var ca = struct.parse( ByteArray(1,0,2) ) : Container
      var cb = C( "a", 1, "b", 2 )
      assertTrue( ca.equals(cb) )

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), 
               Struct( "foo", UBInt8("c"), UBInt8("d") ))
      ca = struct.parse( ByteArray(1,0,2,3,4) )
      cb = C( "a",1, "b",2, "foo", C( "c",3: Integer,"d",4: Integer))
      assertTrue( ca.equals(cb) )

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"))
      var ba = struct.build( C( "a", 1, "b", 2))
      assertArrayEquals( ByteArray(1,0,2), ba )

      var foo = Struct( "foo", UBInt8("c"), UBInt8("d") )
      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), foo )
      ba = struct.build( C( "a",1, "b", 2, "foo", C("c", 3, "d",4)))
      assertArrayEquals( ByteArray(1,0,2,3,4), ba )
      
      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded( Struct("foo", UBInt8("c"), UBInt8("d"))))
      ca = struct.parse( ByteArray(1,0,2,3,4) )
      cb = C( "a", 1, "b", 2, "c", 3, "d", 4 )
      assertEquals( cb, ca )

      struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded( Struct("foo", UBInt8("c"), UBInt8("d"))))
      ba = struct.build( C( "a", 1, "b", 2, "c", 3, "d", 4 ))
      assertArrayEquals( ByteArray(1,0,2,3,4), ba )
  }

  @Test
  def sstructTest() {
      var struct = new SStruct( "struct", UBInt8("a"), UBInt16("b") )
      var ca = struct.parse( ByteArray(1,0,2) ) : Container
      var cb = C( "a", 1, "b", 2 )
      assertTrue( ca.equals(cb) )

      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), 
               SStruct( "foo", UBInt8("c"), UBInt8("d") ))
      ca = struct.parse( ByteArray(1,0,2,3,4) )
      cb = C( "a",1, "b",2, "foo", C( "c",3: Integer,"d",4: Integer))
      assertTrue( ca.equals(cb) )

      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"))
      var ba = struct.build( C( "a", 1, "b", 2))
      assertArrayEquals( ByteArray(1,0,2), ba )

      var foo = SStruct( "foo", UBInt8("c"), UBInt8("d") )
      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), foo )
      ba = struct.build( C( "a",1, "b", 2, "foo", C("c", 3, "d",4)))
      assertArrayEquals( ByteArray(1,0,2,3,4), ba )
      
      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), Embedded( SStruct("foo", UBInt8("c"), UBInt8("d"))))
      ca = struct.parse( ByteArray(1,0,2,3,4) )
      cb = C( "a", 1, "b", 2, "c", 3, "d", 4 )
      assertEquals( cb, ca )

      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), Embedded( SStruct("foo", UBInt8("c"), UBInt8("d"))))
      ba = struct.build( C( "a", 1, "b", 2, "c", 3, "d", 4 ))
      assertArrayEquals( ByteArray(1,0,2,3,4), ba )
  }
 

  @Test
  def fieldSStructTest() {
    
  /*
      * @return unsigned, big endian 8-bit integer
      */
      case class UBInt8 extends FormatField( "a", '>', 'B')
      /**
      * @return unsigned, big endian 16-bit integer
      */
      case class UBInt16 extends FormatField( "b", '>', 'H' )
    
//      struct = Struct( "struct", UBInt8("a"), UBInt16("b") );
//      var ca = struct.parse( ByteArray(1,0,2) ) : Container
       val cb = C( "a", 1, "b", 2 )

       case class ca( a: UBInt8, b:UBInt16 ) extends SStruct("ca", a, b)
       assertTrue( ca( UBInt8(), UBInt16() ).parse(ByteArray(1,0,2)).equals(cb))
      

//      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), 
//               SStruct( "foo", UBInt8("c"), UBInt8("d") ))
//      ca = struct.parse( ByteArray(1,0,2,3,4) )
//      cb = C( "a",1, "b",2, "foo", C( "c",3: Integer,"d",4: Integer))
//      assertTrue( ca.equals(cb) )
//
//      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"))
//      var ba = struct.build( C( "a", 1, "b", 2))
//      assertArrayEquals( ByteArray(1,0,2), ba )
//
//      var foo = SStruct( "foo", UBInt8("c"), UBInt8("d") )
//      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), foo )
//      ba = struct.build( C( "a",1, "b", 2, "foo", C("c", 3, "d",4)))
//      assertArrayEquals( ByteArray(1,0,2,3,4), ba )
//      
//      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), Embedded( SStruct("foo", UBInt8("c"), UBInt8("d"))))
//      ca = struct.parse( ByteArray(1,0,2,3,4) )
//      cb = C( "a", 1, "b", 2, "c", 3, "d", 4 )
//      assertEquals( cb, ca )
//
//      struct = SStruct( "struct", UBInt8("a"), UBInt16("b"), Embedded( SStruct("foo", UBInt8("c"), UBInt8("d"))))
//      ba = struct.build( C( "a", 1, "b", 2, "c", 3, "d", 4 ))
//      assertArrayEquals( ByteArray(1,0,2,3,4), ba )
 }
  
}