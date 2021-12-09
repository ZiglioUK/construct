package uk.ziglio.construct;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.ziglio.construct.Adapters.PaddingAdapter;
import static uk.ziglio.construct.Core.ByteArray;
import static uk.ziglio.construct.Core.Container;
import static uk.ziglio.construct.Macros.Field;

import org.junit.Ignore;
import org.junit.Test;

import uk.ziglio.construct.Macros.Embedded;
import uk.ziglio.construct.Macros.UBInt16;
import uk.ziglio.construct.Macros.UBInt8;
import uk.ziglio.construct.annotations.len;
import uk.ziglio.construct.core.Struct;
import uk.ziglio.construct.fields.StaticField;
import uk.ziglio.construct.lib.Containers.Container;

public class StaticConstructTest {

  public static class Foo extends Struct {
    public Foo(String name ){super(name);}
    public UBInt8 c;
    public UBInt8 d;
  }

  public static class S extends Struct {
    public UBInt8 a;
    public UBInt16 b;
    public Foo foo;
  }

  @Test 
  public void lenAnnotationTest() {
//    assertArrayEquals( ByteArray(0xab, 0xcd), (byte[])PaddingAdapter( Field("paddingadapter", 2) ).parse("abcd"));

    class S extends Struct {
      
      @len(2)
      public StaticField a;
    }

    S s = new S();
    byte[] in = ByteArray(0xab, 0xcd);
    Container ca = s.parse("abcd");
    byte[] a = (byte[])s.a.get();
    assertArrayEquals( ByteArray(0xab, 0xcd), a );
    
    byte[] out = s.build(ca);
    assertArrayEquals( in, out );
    
    byte[] out2 = s.build();
    assertArrayEquals( in, out2 );
  }
  
  @Test
  public void fieldStructTest1() {
    Container ca, cb;
    byte[] ba;

    // struct = Struct( "struct", UBInt8("a"), UBInt16("b") );
    class S extends Struct {
      public UBInt8 a;
      public UBInt16 b;
    }

    S s = new S();

    assertEquals("a", s.a.name);
    assertEquals("b", s.b.name);

    ca = s.parse(ByteArray(1, 0, 2));
    cb = Container("a", 1, "b", 2);
    assertTrue(ca.equals(cb));

    Integer a = s.a.get();
    
    assertTrue(1 == a);
    assertTrue(2 == s.b.get());
  }


  @Test
  public void fieldStructTest2() {
    Container ca, cb;
    byte[] ba;

    // struct = Struct( "struct", UBInt8("a"), UBInt16("b"),
    // Struct( "foo", UBInt8("c"), UBInt8("d") ));
    
    S s = new S();
    
    ca = s.parse(ByteArray(1, 0, 2, 3, 4));
    cb = Container("a", 1, "b", 2, "foo", Container("c", 3, "d", 4));
    assertTrue(ca.equals(cb));

    assertTrue(1 == s.a.get());
    assertTrue(2 == s.b.get());
    assertTrue(3 == s.foo.c.get());
    assertTrue(4 == s.foo.d.get());
  }

  @Test
  public void fieldStructTest3() {
    Container ca, cb;
    byte[] ba;

    //
    // struct = Struct( "struct", UBInt8("a"), UBInt16("b"));
    
    class S extends Struct {
      public UBInt8 a;
      public UBInt16 b;
    }

    S s = new S();
    
    // TODO assign values to fields using set()
    ba = s.build( Container( "a",1, "b", 2));
    assertArrayEquals( ByteArray(1,0,2), ba );
  }

  @Test
  public void fieldStructTest4() {
    Container ca, cb;
    byte[] ba;

    // foo = Struct( "foo", UBInt8("c"), UBInt8("d") );
    // struct = Struct( "struct", UBInt8("a"), UBInt16("b"), foo );

     ba = new S().build( Container( "a",1, "b", 2, "foo", Container("c", 3,"d",4)));
     assertArrayEquals( ByteArray(1,0,2,3,4), ba );
  }

  @Test
  @Ignore // there's no way to implement Embedded in a statically typed manner
  public void fieldStructTest5() {
    Container ca, cb;
    byte[] ba;

    // struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded(
    // Struct("foo", UBInt8("c"), UBInt8("d"))));

    class Foo extends Struct {
      public Foo(String name){super(name);}
      public UBInt8 c;
      public UBInt16 d;
    }
    
    class Emb extends Embedded {
      public Emb(){ 
        super( 
          new Foo("foo"));
      } 
    }
    
    class S extends Struct {
      public UBInt8 a;
      public UBInt16 b;
      public Emb e;
    }

     ca = new S().parse( ByteArray(1,0,2,3,4) );
     cb = Container( "a", 1, "b", 2, "c", 3, "d", 4 );
     assertEquals( cb, ca );
    
    // struct = Struct( "struct", UBInt8("a"), UBInt16("b"), Embedded(
    // Struct("foo", UBInt8("c"), UBInt8("d"))));
    // ba = struct.build( Container( "a", 1, "b", 2, "c", 3, "d", 4 ));
    // assertArrayEquals( ByteArray(1,0,2,3,4), ba );
  }

  
}
