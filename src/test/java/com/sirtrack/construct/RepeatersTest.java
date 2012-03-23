package com.sirtrack.construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static com.sirtrack.construct.Adapters.*;
import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Containers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.CountFunc;
import com.sirtrack.construct.lib.Containers.Container;

public class RepeatersTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void MetaArrayTest(){
  	MetaArray ma = MetaArray( new CountFunc(){ public int count(Container context){
  			return 3;
  		}
  	},	UBInt8("metaarray"));
  	
  	assertEquals( ListContainer(1,2,3), ma.parse(ByteArray(1,2,3)));

  	assertArrayEquals( ByteArray(1,2,3), ma.build(ListContainer( 1,2,3 )));
  	
  	exception.expect( ArrayError.class );
  	assertArrayEquals( ByteArray(1,2,3), ma.build(ListContainer( 1,2 )));

  	exception.expect( ArrayError.class );
  	assertEquals( ListContainer(1,2,3), ma.parse(ByteArray(1,2)));
  }

  @Test
  public void RangeTest() {
  	Range c;
  	byte[] ba;
  	
  	c = Range(3, 7, UBInt8("foo"));
  	assertEquals( ListContainer(1,2,3), (List)c.parse(ByteArray(1,2,3)));
  	assertEquals( ListContainer(1,2,3,4,5,6), (List)c.parse(ByteArray(1,2,3,4,5,6)));
  	assertEquals( ListContainer(1,2,3,4,5,6,7), (List)c.parse(ByteArray(1,2,3,4,5,6,7)));
  	assertEquals( ListContainer(1,2,3,4,5,6,7), (List)c.parse(ByteArray(1,2,3,4,5,6,7,8,9)));

  	ba = c.build( ListContainer( 1,2,3,4 ));
  	assertArrayEquals( ByteArray(1,2,3,4), ba );

  	exception.expect( RangeError.class );
  	c.build( ListContainer( 1,2,3,4,5,6,7,8 ));

  	exception.expect( RangeError.class );
  	c.build( ListContainer( 1,2 ));
  	
  }
/*

class TestStrictRepeater(unittest.TestCase):

    def setUp(self):
        self.c = StrictRepeater(4, UBInt8("foo"))

    def test_trivial(self):
        pass

    def test_parse(self):
        self.assertEqual(self.c.parse("\x01\x02\x03\x04"), [1, 2, 3, 4])
        self.assertEqual(self.c.parse("\x01\x02\x03\x04\x05\x06"),
            [1, 2, 3, 4])

    def test_build(self):
        self.assertEqual(self.c.build([5, 6, 7, 8]), "\x05\x06\x07\x08")

    def test_build_oversized(self):
        self.assertRaises(ArrayError, self.c.build, [5, 6, 7, 8, 9])

    def test_build_undersized(self):
        self.assertRaises(ArrayError, self.c.build, [5, 6, 7])

class TestGreedyRepeater(unittest.TestCase):

    def setUp(self):
        self.c = GreedyRepeater(UBInt8("foo"))

    def test_trivial(self):
        pass

    def test_empty_parse(self):
        self.assertRaises(RangeError, self.c.parse, "")

    def test_parse(self):
        self.assertEqual(self.c.parse("\x01"), [1])
        self.assertEqual(self.c.parse("\x01\x02\x03"), [1, 2, 3])
        self.assertEqual(self.c.parse("\x01\x02\x03\x04\x05\x06"),
            [1, 2, 3, 4, 5, 6])

    def test_empty_build(self):
        self.assertRaises(RangeError, self.c.build, [])

    def test_build(self):
        self.assertEqual(self.c.build([1, 2]), "\x01\x02")

class TestOptionalGreedyRepeater(unittest.TestCase):

    def setUp(self):
        self.c = OptionalGreedyRepeater(UBInt8("foo"))

    def test_trivial(self):
        pass

    def test_empty_parse(self):
        self.assertEqual(self.c.parse(""), [])

    def test_parse(self):
        self.assertEqual(self.c.parse("\x01\x02"), [1, 2])

    def test_empty_build(self):
        self.assertEqual(self.c.build([]), "")

    def test_build(self):
        self.assertEqual(self.c.build([1, 2]), "\x01\x02")
 */
}

