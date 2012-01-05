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
import static construct.lib.Containers.*;

public class ContstructTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();
/*
    #
    # constructs
    #
    [MetaArray(lambda ctx: 3, UBInt8("metaarray")).parse, "\x01\x02\x03", [1,2,3], None],
    [MetaArray(lambda ctx: 3, UBInt8("metaarray")).parse, "\x01\x02", None, ArrayError],
    [MetaArray(lambda ctx: 3, UBInt8("metaarray")).build, [1,2,3], "\x01\x02\x03", None],
    [MetaArray(lambda ctx: 3, UBInt8("metaarray")).build, [1,2], None, ArrayError],
    
    [Range(3, 5, UBInt8("range")).parse, "\x01\x02\x03", [1,2,3], None],
    [Range(3, 5, UBInt8("range")).parse, "\x01\x02\x03\x04", [1,2,3,4], None],
    [Range(3, 5, UBInt8("range")).parse, "\x01\x02\x03\x04\x05", [1,2,3,4,5], None],
    [Range(3, 5, UBInt8("range")).parse, "\x01\x02", None, RangeError],
    [Range(3, 5, UBInt8("range")).build, [1,2,3], "\x01\x02\x03", None],
    [Range(3, 5, UBInt8("range")).build, [1,2,3,4], "\x01\x02\x03\x04", None],
    [Range(3, 5, UBInt8("range")).build, [1,2,3,4,5], "\x01\x02\x03\x04\x05", None],
    [Range(3, 5, UBInt8("range")).build, [1,2], None, RangeError],
    [Range(3, 5, UBInt8("range")).build, [1,2,3,4,5,6], None, RangeError],
    
    [RepeatUntil(lambda obj, ctx: obj == 9, UBInt8("repeatuntil")).parse, "\x02\x03\x09", [2,3,9], None],
    [RepeatUntil(lambda obj, ctx: obj == 9, UBInt8("repeatuntil")).parse, "\x02\x03\x08", None, ArrayError],
    [RepeatUntil(lambda obj, ctx: obj == 9, UBInt8("repeatuntil")).build, [2,3,9], "\x02\x03\x09", None],
    [RepeatUntil(lambda obj, ctx: obj == 9, UBInt8("repeatuntil")).build, [2,3,8], None, ArrayError],
*/
  @Test
  public void structTest1() {
  	  Struct struct = Struct( "struct", UBInt8("a"), UBInt16("b") );
  	  Container ca = (Container)struct.parse( new byte[]{1,0,2} );
  	  Container cb = Container( P("a", 1), P("b", 2) );
      assertTrue( ca.equals(cb) );
  }

  @Test
  public void structTest2() {
  	  Struct struct = Struct( "struct", UBInt8("a"), UBInt16("b"), 
  	  									Struct( "foo", UBInt8("c"), UBInt8("d") ));
  	  Container ca = (Container)struct.parse( new byte[]{1,0,2,3,4} );
  	  Container cb = Container( P("a",1), P("b",2), P("foo", Container( P("c",3), P("d",4))));
      assertTrue( ca.equals(cb) );
  }

  /*
    [Struct("struct", UBInt8("a"), UBInt16("b"), Embedded(Struct("foo", UBInt8("c"), UBInt8("d")))).parse, "\x01\x00\x02\x03\x04", Container(a=1,b=2,c=3,d=4), None],
*/
  @Test
  public void structTest4() {
  	  Struct struct = Struct( "struct", UBInt8("a"), UBInt16("b"));
  	  byte[] ba = struct.build( Container( P("a",1), P("b", 2)));
  	  assertArrayEquals( new byte[]{1,0,2}, ba );
  }
   
   /*[Struct("struct", UBInt8("a"), UBInt16("b")).build, Container(a=1,b=2), "\x01\x00\x02", None],
    [Struct("struct", UBInt8("a"), UBInt16("b"), Struct("foo", UBInt8("c"), UBInt8("d"))).build, Container(a=1,b=2,foo=Container(c=3,d=4)), "\x01\x00\x02\x03\x04", None],
    [Struct("struct", UBInt8("a"), UBInt16("b"), Embedded(Struct("foo", UBInt8("c"), UBInt8("d")))).build, Container(a=1,b=2,c=3,d=4), "\x01\x00\x02\x03\x04", None],
    
    [Sequence("sequence", UBInt8("a"), UBInt16("b")).parse, "\x01\x00\x02", [1,2], None],
    [Sequence("sequence", UBInt8("a"), UBInt16("b"), Sequence("foo", UBInt8("c"), UBInt8("d"))).parse, "\x01\x00\x02\x03\x04", [1,2,[3,4]], None],
    [Sequence("sequence", UBInt8("a"), UBInt16("b"), Embedded(Sequence("foo", UBInt8("c"), UBInt8("d")))).parse, "\x01\x00\x02\x03\x04", [1,2,3,4], None],
    [Sequence("sequence", UBInt8("a"), UBInt16("b")).build, [1,2], "\x01\x00\x02", None],
    [Sequence("sequence", UBInt8("a"), UBInt16("b"), Sequence("foo", UBInt8("c"), UBInt8("d"))).build, [1,2,[3,4]], "\x01\x00\x02\x03\x04", None],
    [Sequence("sequence", UBInt8("a"), UBInt16("b"), Embedded(Sequence("foo", UBInt8("c"), UBInt8("d")))).build, [1,2,3,4], "\x01\x00\x02\x03\x04", None],
    
    [Switch("switch", lambda ctx: 5, {1:UBInt8("x"), 5:UBInt16("y")}).parse, "\x00\x02", 2, None],
    [Switch("switch", lambda ctx: 6, {1:UBInt8("x"), 5:UBInt16("y")}).parse, "\x00\x02", None, SwitchError],
    [Switch("switch", lambda ctx: 6, {1:UBInt8("x"), 5:UBInt16("y")}, default = UBInt8("x")).parse, "\x00\x02", 0, None],
    [Switch("switch", lambda ctx: 5, {1:UBInt8("x"), 5:UBInt16("y")}, include_key = True).parse, "\x00\x02", (5, 2), None],
    [Switch("switch", lambda ctx: 5, {1:UBInt8("x"), 5:UBInt16("y")}).build, 2, "\x00\x02", None],
    [Switch("switch", lambda ctx: 6, {1:UBInt8("x"), 5:UBInt16("y")}).build, 9, None, SwitchError],
    [Switch("switch", lambda ctx: 6, {1:UBInt8("x"), 5:UBInt16("y")}, default = UBInt8("x")).build, 9, "\x09", None],
    [Switch("switch", lambda ctx: 5, {1:UBInt8("x"), 5:UBInt16("y")}, include_key = True).build, ((5, 2),), "\x00\x02", None],
    [Switch("switch", lambda ctx: 5, {1:UBInt8("x"), 5:UBInt16("y")}, include_key = True).build, ((89, 2),), None, SwitchError],
    
    [Select("select", UBInt32("a"), UBInt16("b"), UBInt8("c")).parse, "\x07", 7, None],
    [Select("select", UBInt32("a"), UBInt16("b")).parse, "\x07", None, SelectError],
    [Select("select", UBInt32("a"), UBInt16("b"), UBInt8("c"), include_name = True).parse, "\x07", ("c", 7), None],
    [Select("select", UBInt32("a"), UBInt16("b"), UBInt8("c")).build, 7, "\x00\x00\x00\x07", None],
    [Select("select", UBInt32("a"), UBInt16("b"), UBInt8("c"), include_name = True).build, (("c", 7),), "\x07", None],
    [Select("select", UBInt32("a"), UBInt16("b"), UBInt8("c"), include_name = True).build, (("d", 7),), None, SelectError],
    
    [Peek(UBInt8("peek")).parse, "\x01", 1, None],
    [Peek(UBInt8("peek")).parse, "", None, None],
    [Peek(UBInt8("peek")).build, 1, "", None],
    [Peek(UBInt8("peek"), perform_build = True).build, 1, "\x01", None],
    [Struct("peek", Peek(UBInt8("a")), UBInt16("b")).parse, "\x01\x02", Container(a=1,b=0x102), None],
    [Struct("peek", Peek(UBInt8("a")), UBInt16("b")).build, Container(a=1,b=0x102), "\x01\x02", None],
    
    [Value("value", lambda ctx: "moo").parse, "", "moo", None],
    [Value("value", lambda ctx: "moo").build, None, "", None],
    
    [Anchor("anchor").parse, "", 0, None],
    [Anchor("anchor").build, None, "", None],
    
    [LazyBound("lazybound", lambda: UBInt8("foo")).parse, "\x02", 2, None],
    [LazyBound("lazybound", lambda: UBInt8("foo")).build, 2, "\x02", None],
    
    [Pass.parse, "", None, None],
    [Pass.build, None, "", None],

    [Terminator.parse, "", None, None],
    [Terminator.parse, "x", None, TerminatorError],
    [Terminator.build, None, "", None],
    
    [Pointer(lambda ctx: 2, UBInt8("pointer")).parse, "\x00\x00\x07", 7, None],
    [Pointer(lambda ctx: 2, UBInt8("pointer")).build, 7, "\x00\x00\x07", None],
    
    [OnDemand(UBInt8("ondemand")).parse("\x08").read, (), 8, None],
    [Struct("ondemand", UBInt8("a"), OnDemand(UBInt8("b")), UBInt8("c")).parse, 
        "\x07\x08\x09", Container(a=7,b=LazyContainer(None, None, None, None),c=9), None],
    [Struct("ondemand", UBInt8("a"), OnDemand(UBInt8("b"), advance_stream = False), UBInt8("c")).parse, 
        "\x07\x09", Container(a=7,b=LazyContainer(None, None, None, None),c=9), None],
    
    [OnDemand(UBInt8("ondemand")).build, 8, "\x08", None],
    [Struct("ondemand", UBInt8("a"), OnDemand(UBInt8("b")), UBInt8("c")).build, 
        Container(a=7,b=8,c=9), "\x07\x08\x09", None],
    [Struct("ondemand", UBInt8("a"), OnDemand(UBInt8("b"), force_build = False), UBInt8("c")).build, 
        Container(a=7,b=LazyContainer(None, None, None, None),c=9), "\x07\x00\x09", None],
    [Struct("ondemand", UBInt8("a"), OnDemand(UBInt8("b"), force_build = False, advance_stream = False), UBInt8("c")).build, 
        Container(a=7,b=LazyContainer(None, None, None, None),c=9), "\x07\x09", None],
    
    [Struct("reconfig", Reconfig("foo", UBInt8("bar"))).parse, "\x01", Container(foo=1), None],
    [Struct("reconfig", Reconfig("foo", UBInt8("bar"))).build, Container(foo=1), "\x01", None],
    
    [Buffered(UBInt8("buffered"), lambda x:x, lambda x:x, lambda x:x).parse, 
        "\x07", 7, None],
    [Buffered(GreedyRange(UBInt8("buffered")), lambda x:x, lambda x:x, lambda x:x).parse, 
        "\x07", None, SizeofError],
    [Buffered(UBInt8("buffered"), lambda x:x, lambda x:x, lambda x:x).build, 
        7, "\x07", None],
    [Buffered(GreedyRange(UBInt8("buffered")), lambda x:x, lambda x:x, lambda x:x).build, 
        [7], None, SizeofError],
    
    [Restream(UBInt8("restream"), lambda x:x, lambda x:x, lambda x:x).parse,
        "\x07", 7, None],
    [Restream(GreedyRepeater(UBInt8("restream")), lambda x:x, lambda x:x, lambda x:x).parse,
        "\x07", [7], None],
    [Restream(UBInt8("restream"), lambda x:x, lambda x:x, lambda x:x).parse,
        "\x07", 7, None],
    [Restream(GreedyRepeater(UBInt8("restream")), lambda x:x, lambda x:x, lambda x:x).parse,
        "\x07", [7], None],
 */
}

