package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static construct.Core.*;
import static construct.Macros.*;
import static construct.Adapters.*;
import static construct.lib.Containers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RepeatersTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void RangeTest() {
  	Range c;
  	byte[] ba;
  	
  	c = Range(3, 7, UBInt8("foo"));
  	assertArrayEquals( new Object[]{1,2,3}, (Object[])c.parse(new byte[]{1,2,3}));
  	assertArrayEquals( new Object[]{1,2,3,4,5,6}, (Object[])c.parse(new byte[]{1,2,3,4,5,6}));
  	assertArrayEquals( new Object[]{1,2,3,4,5,6,7}, (Object[])c.parse(new byte[]{1,2,3,4,5,6,7}));
  	assertArrayEquals( new Object[]{1,2,3,4,5,6,7}, (Object[])c.parse(new byte[]{1,2,3,4,5,6,7,8,9}));

  	ba = c.build( ListContainer( 1,2,3,4 ));
  	assertArrayEquals( new byte[]{1,2,3,4}, ba );

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

