package com.sirtrack.construct;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;

import com.sirtrack.construct.Core.RangeError;
import com.sirtrack.construct.lib.Containers.Container;

import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Containers.*;


public class ConstructTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void rangeTest() {
    Range range;

    range = Range(3, 5, UBInt8("range"));
    assertEquals(ListContainer(1, 2, 3), range.parse(ByteArray(1, 2, 3)));
    assertEquals(ListContainer(1, 2, 3, 4), range.parse(ByteArray(1, 2, 3, 4)));
    assertEquals(ListContainer(1, 2, 3, 4, 5),
        range.parse(ByteArray(1, 2, 3, 4, 5)));

    assertArrayEquals(ByteArray(1, 2, 3), range.build(ListContainer(1, 2, 3)));
    assertArrayEquals(ByteArray(1, 2, 3, 4),
        range.build(ListContainer(1, 2, 3, 4)));
    assertArrayEquals(ByteArray(1, 2, 3, 4, 5),
        range.build(ListContainer(1, 2, 3, 4, 5)));

    exception.expect(RangeError.class);
    range.build(ListContainer(1, 2, 3, 4, 5, 6));

    exception.expect(RangeError.class);
    range.build(ListContainer(1, 2));

    exception.expect(RangeError.class);
    range.parse(ByteArray(1, 2));

    /*
     * [Range(3, 5, UBInt8("range")).build, [1,2,3,4,5,6], None, RangeError],
     */
  }

  @Test
  public void structTest() {
    Struct struct, foo;
    Container ca, cb;
    byte[] ba;

    struct = Struct("struct", UBInt8("a"), UBInt16("b"));
    ca = struct.parse(ByteArray(1, 0, 2));
    cb = Container("a", 1, "b", 2);
    assertTrue(ca.equals(cb));

    struct = Struct("struct", UBInt8("a"), UBInt16("b"),
        Struct("foo", UBInt8("c"), UBInt8("d")));
    ca = struct.parse(ByteArray(1, 0, 2, 3, 4));
    cb = Container("a", 1, "b", 2, "foo", Container("c", 3, "d", 4));
    assertTrue(ca.equals(cb));

    struct = Struct("struct", UBInt8("a"), UBInt16("b"));
    ba = struct.build(Container("a", 1, "b", 2));
    assertArrayEquals(ByteArray(1, 0, 2), ba);

    foo = Struct("foo", UBInt8("c"), UBInt8("d"));
    struct = Struct("struct", UBInt8("a"), UBInt16("b"), foo);
    ba = struct.build(Container("a", 1, "b", 2, "foo",
        Container("c", 3, "d", 4)));
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4), ba);

    struct = Struct("struct", UBInt8("a"), UBInt16("b"),
        Embedded(Struct("foo", UBInt8("c"), UBInt8("d"))));
    ca = struct.parse(ByteArray(1, 0, 2, 3, 4));
    cb = Container("a", 1, "b", 2, "c", 3, "d", 4);
    assertEquals(cb, ca);

    struct = Struct("struct", UBInt8("a"), UBInt16("b"),
        Embedded(Struct("foo", UBInt8("c"), UBInt8("d"))));
    ba = struct.build(Container("a", 1, "b", 2, "c", 3, "d", 4));
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4), ba);
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
    
    class Foo extends Struct {
      public Foo(String name ){super(name);}
      public UBInt8 c;
      public UBInt8 d;
    }

    class S extends Struct {
      public UBInt8 a;
      public UBInt16 b;
      public Foo foo;
    }

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

    class Foo extends Struct {
      public Foo(String name ){super(name);}
      public UBInt8 c;
      public UBInt8 d;
    }

    class S extends Struct {
      public UBInt8 a;
      public UBInt16 b;
      public Foo foo;
    }

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

  @Test
  public void sizeofTest() {
    LengthFunc foo = new LengthFunc() {

      @Override
      public int length(Container ctx) {
        return (Integer) ((Container) ctx.get("_")).get("length")
            + (Integer) ctx.get("inner_length");
      }
    };

    Struct pstring = Struct("pstring", UBInt8("length"),
        Struct("inner", UBInt8("inner_length"), Field("data", foo)));

    Container obj = pstring.parse("03020A0B0C0D0E"); // "\x03\x02helloXXX"
    // TODO I should really implement a ByteArray class with equals() and
    // toString() methods
    // assertEquals( (Container)obj, Container( "length", 3, "inner", Container(
    // "inner_length", 2, "data", ByteArray(0xA,0xB,0xC,0xD,0xE))) );
    assertEquals(3, obj.get("length"));
    Container inner = obj.get("inner");
    assertEquals(2, inner.get("inner_length"));
    byte[] data = inner.get("data");
    assertArrayEquals(ByteArray(0xA, 0xB, 0xC, 0xD, 0xE), data);

    int size = pstring._sizeof(Container("inner_length", 2, "_",
        Container("length", 3)));
    assertEquals(7, size);
  }

  @Test
  public void sequenceTest() {
    Sequence s;
    s = Sequence("sequence", UBInt8("a"), UBInt16("b"));
    assertEquals(ListContainer(1, 2), s.parse(ByteArray(1, 0, 2)));

    s = Sequence("sequence", UBInt8("a"), UBInt16("b"),
        Sequence("foo", UBInt8("c"), UBInt8("d")));
    assertEquals(ListContainer(1, 2, ListContainer(3, 4)),
        s.parse(ByteArray(1, 0, 2, 3, 4)));

    s = Sequence("sequence", UBInt8("a"), UBInt16("b"),
        Embedded(Sequence("foo", UBInt8("c"), UBInt8("d"))));
    assertEquals(ListContainer(1, 2, 3, 4), s.parse(ByteArray(1, 0, 2, 3, 4)));

    s = Sequence("sequence", UBInt8("a"), UBInt16("b"));
    assertArrayEquals(ByteArray(1, 0, 2), s.build(ListContainer(1, 2)));

    s = Sequence("sequence", UBInt8("a"), UBInt16("b"),
        Sequence("foo", UBInt8("c"), UBInt8("d")));
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4),
        s.build(ListContainer(1, 2, ListContainer(3, 4))));

    s = Sequence("sequence", UBInt8("a"), UBInt16("b"),
        Embedded(Sequence("foo", UBInt8("c"), UBInt8("d"))));
    assertArrayEquals(ByteArray(1, 0, 2, 3, 4),
        s.build(ListContainer(1, 2, 3, 4)));
  }

  @Test
  public void switchTest() {
    Switch switchstruct;

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 5;
      }
    }, 1, UBInt8("x"), 5, UBInt16("y"));
    assertEquals(2, switchstruct.parse(ByteArray(0, 2)));

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 6;
      }
    }, Container(1, UBInt8("x"), 5, UBInt16("y")), UBInt8("x"), false);
    assertEquals(0, switchstruct.parse(ByteArray(0, 2)));

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 5;
      }
    }, Container(1, UBInt8("x"), 5, UBInt16("y")), NoDefault, true);
    assertEquals(Container(5, 2), switchstruct.parse(ByteArray(0, 2)));

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 5;
      }
    }, 1, UBInt8("x"), 5, UBInt16("y"));
    assertArrayEquals(ByteArray(0, 2), switchstruct.build(2));

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 6;
      }
    }, Container(1, UBInt8("x"), 5, UBInt16("y")), UBInt8("x"), false);
    assertArrayEquals(ByteArray(9), switchstruct.build(9));

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 6;
      }
    }, Container(1, UBInt8("x"), 5, UBInt16("y")), NoDefault, true);
    assertArrayEquals(ByteArray(0, 2), switchstruct.build(ListContainer(5, 2)));

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 6;
      }
    }, Container(1, UBInt8("x"), 5, UBInt16("y")), NoDefault, true);
    exception.expect(SwitchError.class);
    switchstruct.build(ListContainer(89, 2));

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 6;
      }
    }, 1, UBInt8("x"), 5, UBInt16("y"));
    exception.expect(SwitchError.class);
    switchstruct.build(9);

    switchstruct = Switch("switch", new KeyFunc() {
      public Object get(Container context) {
        return 6;
      }
    }, Container(1, UBInt8("x"), 5, UBInt16("y")));
    exception.expect(SwitchError.class);
    switchstruct.parse(ByteArray(0, 2));
  }

  @Test
  public void reconfigTest() {
    Container c1 = Container("foo", 1);
    Object c2 = Struct("reconfig", Reconfig("foo", UBInt8("bar"))).parse(
        ByteArray(1));
    assertEquals(c1, c2);

    byte[] ba = Struct("reconfig", Reconfig("foo", UBInt8("bar"))).build(
        Container("foo", 1));
    assertArrayEquals(ByteArray(1), ba);
  }

  @Test
  public void valueTest() {
    Value val = Value("value", new ValueFunc() {
      public Object get(Container ctx) {
        return "moo";
      }
    });
    assertEquals("moo", val.parse(""));
    assertArrayEquals(new byte[0], val.build(null));
  }

  @Test
  public void RestreamTest() {

  }
  /*
   * [Restream(UBInt8("restream"), lambda x:x, lambda x:x, lambda x:x).parse,
   * "\x07", 7, None], [Restream(GreedyRepeater(UBInt8("restream")), lambda x:x,
   * lambda x:x, lambda x:x).parse, "\x07", [7], None],
   * [Restream(UBInt8("restream"), lambda x:x, lambda x:x, lambda x:x).parse,
   * "\x07", 7, None], [Restream(GreedyRepeater(UBInt8("restream")), lambda x:x,
   * lambda x:x, lambda x:x).parse, "\x07", [7], None],
   */
}
