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

public class BitTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Test
  public void test_parse() {
  	Construct struct = BitStruct("foo",
                        BitField("a", 3),
                        Flag("b"),
                        Padding(3),
                        Nibble("c"),
                        BitField("d", 5)
    									);

  	Container c1 = Container( P("a", 7), P("b", false), P("c",8), P("d", 31 ));
  	Container c2 = (Container)struct.parse(new byte[]{(byte)0xe1, 0x1f});
  	assertTrue( c1.equals(c2) );
  }
  
  @Test
  public void test_parse_nested() {
  
  	Construct struct = BitStruct("foo",
        BitField("a", 3),
        Flag("b"),
        Padding(3),
        Nibble("c"),
        Struct("bar",
            Nibble("d"),
            Bit("e")
        )
    );

  	Container c1 = Container( P("a", 7), P("b", false), P("bar", Container( P("d", 15 ), P("e", 1))), P("c",8) );
  	Container c2 = (Container)struct.parse(new byte[]{(byte)0xe1, 0x1f});
  	assertTrue( c1.equals(c2) );
  }
}

