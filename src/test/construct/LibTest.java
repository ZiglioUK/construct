package construct;

import static construct.lib.Binary.*;
import static construct.Macros.ByteArray;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.Core.ValueError;

public class LibTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void test_int_to_bin(){
	  assertArrayEquals( ByteArray( 0x01, 0x00, 0x00, 0x01, 0x01 ), int_to_bin(19, 5) );
  }

  @Test
  public void test_int_to_bin_signed(){
	  assertArrayEquals( ByteArray( 0x01, 0x00, 0x00, 0x01, 0x01 ), int_to_bin( -13, 5)  );
  }

  @Test
  public void test_bin_to_int(){
	  assertEquals( 19, bin_to_int( ByteArray( 0x01, 0x00, 0x00, 0x01, 0x01 )) );
  }

  @Test
  public void test_bin_to_int_signed(){
	  assertEquals( -13, bin_to_int( ByteArray( 0x01, 0x00, 0x00, 0x01, 0x01 ), true ) );
  }

  @Test
  public void test_swap_bytes(){
	  assertArrayEquals( ByteArray( 0xa, 0xb, 0xc, 0xd ), swap_bytes( ByteArray( 0xd, 0xc, 0xb, 0xa ), 4 ));
  }

  @Test
  public void test_encode_bin(){
	  assertArrayEquals( ByteArray( 0, 1, 1, 0,
			                         0, 0, 0, 1, // 0x61 ascii for 'a'
			                         0, 1, 1, 0,
			                         0, 0, 1, 0  // 0x62 ascii for 'b'
			                         ), encode_bin("ab".getBytes()));
  }

  @Test
  public void test_decode_bin(){
	  assertArrayEquals( "ab".getBytes(), decode_bin(ByteArray( 
			                        		 0, 1, 1, 0,
			                        		 0, 0, 0, 1,
			                        		 0, 1, 1, 0,
			                        		 0, 0, 1, 0)));
  }

  @Test
  public void test_decode_bin_length(){
	  exception.expect( ValueError.class );
	  decode_bin( ByteArray(0,0));
  }
  
}