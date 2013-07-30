package com.sirtrack.construct.scala
import com.sirtrack.construct.Core.Struct
import com.sirtrack.construct.Core.ByteArray
import com.sirtrack.construct.Core.FormatField
import com.sirtrack.construct.Macros._
import org.junit._
import org.junit.Assert._
import com.sirtrack.construct.lib.Containers.Container
//import com.sirtrack.construct.Core.{ Container=> C }

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
  object C {
    //  implicit def javaToScalaInt(d: java.lang.Integer) = d.intValue
    //  implicit def scalaToJavaInteger(d: Int ) = d.asInstanceOf[java.lang.Integer]
    def apply(pairs: Any*) = new Container(pairs: _*)
  }

  @Test
  def structTest() {
    var struct = Struct("struct", UBInt8("a"), UBInt16("b"))
    var ca = struct.parse(ByteArray(1, 0, 2)): Container
    var cb = C("a", 1, "b", 2)
    assertTrue(ca.equals(cb))

    struct = Struct("struct", UBInt8("a"), UBInt16("b"),
      Struct("foo", UBInt8("c"), UBInt8("d")))
    ca = struct.parse(ByteArray(1, 0, 2, 3, 4))
    cb = C("a", 1, "b", 2, "foo", C("c", 3: Integer, "d", 4: Integer))
    assertTrue(ca.equals(cb))

    struct = Struct("struct", UBInt8("a"), UBInt16("b"))
    var ba = struct.build(C("a", 1, "b", 2))
    assertArrayEquals(ByteArray(1, 0, 2), ba)

    var foo = Struct("foo", UBInt8("c"), UBInt8("d"))
    struct = Struct("struct", UBInt8("a"), UBInt16("b"), foo)
    ba = struct.build(C("a", 1, "b", 2, "foo", C("c", 3, "d", 4)))
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4), ba)

    struct = Struct("struct", UBInt8("a"), UBInt16("b"), Embedded(Struct("foo", UBInt8("c"), UBInt8("d"))))
    ca = struct.parse(ByteArray(1, 0, 2, 3, 4))
    cb = C("a", 1, "b", 2, "c", 3, "d", 4)
    assertEquals(cb, ca)

    struct = Struct("struct", UBInt8("a"), UBInt16("b"), Embedded(Struct("foo", UBInt8("c"), UBInt8("d"))))
    ba = struct.build(C("a", 1, "b", 2, "c", 3, "d", 4))
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4), ba)
  }

  @Test
  def sstructTest() {
    var struct = new SStruct("struct", UBInt8("a"), UBInt16("b"))
    var ca = struct.parse(ByteArray(1, 0, 2)): Container
    var cb = C("a", 1, "b", 2)
    assertTrue(ca.equals(cb))

    struct = SStruct("struct", UBInt8("a"), UBInt16("b"),
      SStruct("foo", UBInt8("c"), UBInt8("d")))
    ca = struct.parse(ByteArray(1, 0, 2, 3, 4))
    cb = C("a", 1, "b", 2, "foo", C("c", 3: Integer, "d", 4: Integer))
    assertTrue(ca.equals(cb))

    struct = SStruct("struct", UBInt8("a"), UBInt16("b"))
    var ba = struct.build(C("a", 1, "b", 2))
    assertArrayEquals(ByteArray(1, 0, 2), ba)

    var foo = SStruct("foo", UBInt8("c"), UBInt8("d"))
    struct = SStruct("struct", UBInt8("a"), UBInt16("b"), foo)
    ba = struct.build(C("a", 1, "b", 2, "foo", C("c", 3, "d", 4)))
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4), ba)

    struct = SStruct("struct", UBInt8("a"), UBInt16("b"), Embedded(SStruct("foo", UBInt8("c"), UBInt8("d"))))
    ca = struct.parse(ByteArray(1, 0, 2, 3, 4))
    cb = C("a", 1, "b", 2, "c", 3, "d", 4)
    assertEquals(cb, ca)

    struct = SStruct("struct", UBInt8("a"), UBInt16("b"), Embedded(SStruct("foo", UBInt8("c"), UBInt8("d"))))
    ba = struct.build(C("a", 1, "b", 2, "c", 3, "d", 4))
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4), ba)
  }

  def getFields(o: Any): Map[String, Any] = {
    val fieldsAsPairs = for (field <- o.getClass.getDeclaredFields) yield {
      field.setAccessible(true)
      (field.getName, field.get(o))
    }
    Map(fieldsAsPairs: _*)
  }

    /*
      * @return unsigned, big endian 8-bit integer
      */
  class UBInt8(name: String) extends FormatField(name, '>', 'B')
  object UBInt8 {
    def apply(name: String) = new UBInt8(name)
  }

  /**
   * @return unsigned, big endian 16-bit integer
   */
  case class UBInt16(n: String) extends FormatField(n, '>', 'H')

  // http://stackoverflow.com/questions/2224251/reflection-on-a-scala-case-class
  trait CaseClassReflector /*extends com.sirtrack.construct.Core.Construct*/ {
      
      def subcons: Seq[com.sirtrack.construct.Core.Construct]
      
      val fieldsAsPairs = for (field <- getClass.getDeclaredFields) yield {
        field.setAccessible(true)
        val clazz = field.getType
        val name = field.getName
        val ctor = clazz.getConstructors()(0)
        val inst = ctor.newInstance(name)
        field.set( this, inst )

        //        field.get(a).asInstanceOf[Construct]
//        field.set(clazz, (field: CaseClassReflector).apply)
        //        println( + " " +  + " " + field.get(this))
        //        (field.getName, field.get(this)) 
      }
      //      Map(fieldsAsPairs :_*)
  }
  
  abstract class CCStruct{
      for (field <- getClass.getDeclaredFields) yield {
        field.setAccessible(true)
        val clazz = field.getType
        val name = field.getName
        val ctor = clazz.getConstructors()(0)
        val inst = ctor.newInstance(name)
        field.set( this, inst )

        //        field.get(a).asInstanceOf[Construct]
//        field.set(clazz, (field: CaseClassReflector).apply)
        //        println( + " " +  + " " + field.get(this))
        //        (field.getName, field.get(this)) 
      }
  }
  
//  @Test
//  def CaseClassReflectorTest() {
//    class Ca extends CCStruct {
//      var a: UBInt8
//    }
//    
//    val ca = new Ca()
//    printf(ca.a.name)
//  }

  @Test
  def fieldSStructTest() {

    //      struct = Struct( "struct", UBInt8("a"), UBInt16("b") );
    //      var ca = struct.parse( ByteArray(1,0,2) ) : Container
    val cb = C("a", 1, "b", 2)

    //val ca = Ca(UBInt8("a"), UBInt16("b"))
//    case class ca( a: UBInt8, b:UBInt16 ) extends SStruct("ca", a, b)
//    assertTrue(ca.parse(ByteArray(1, 0, 2)).equals(cb))

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