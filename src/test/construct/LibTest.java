package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static construct.lib.Binary.bin_to_int;
import static construct.lib.Binary.decode_bin;
import static construct.lib.Binary.encode_bin;
import static construct.lib.Binary.int_to_bin;
import static construct.lib.Binary.swap_bytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.exception.ValueError;

public class LibTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void test_int_to_bin(){
	  assertArrayEquals( new byte[]{ 0x01, 0x00, 0x00, 0x01, 0x01 }, int_to_bin(19, 5) );
  }

  @Test
  public void test_int_to_bin_signed(){
	  assertArrayEquals( new byte[]{ 0x01, 0x00, 0x00, 0x01, 0x01 }, int_to_bin( -13, 5)  );
  }

  @Test
  public void test_bin_to_int(){
	  assertEquals( 19, bin_to_int( new byte[]{ 0x01, 0x00, 0x00, 0x01, 0x01 }) );
  }

  @Test
  public void test_bin_to_int_signed(){
	  assertEquals( -13, bin_to_int( new byte[]{ 0x01, 0x00, 0x00, 0x01, 0x01 }, true ) );
  }

  @Test
  public void test_swap_bytes(){
	  assertArrayEquals( new byte[]{ 0xa, 0xb, 0xc, 0xd }, swap_bytes( new byte[]{ 0xd, 0xc, 0xb, 0xa }, 4 ));
  }

  @Test
  public void test_encode_bin(){
	  assertArrayEquals( new byte[]{ 0, 1, 1, 0,
			                         0, 0, 0, 1, // 0x61 ascii for 'a'
			                         0, 1, 1, 0,
			                         0, 0, 1, 0  // 0x62 ascii for 'b'
			                         }, encode_bin("ab"));
  }

  @Test
  public void test_decode_bin(){
	  assertEquals( "ab", decode_bin(new byte[]{ 
			                        		 0, 1, 1, 0,
			                        		 0, 0, 0, 1,
			                        		 0, 1, 1, 0,
			                        		 0, 0, 1, 0}));
  }

  @Test
  public void test_decode_bin_length(){
	  exception.expect( ValueError.class );
	  decode_bin( new byte[]{0,0});
  }
  
}