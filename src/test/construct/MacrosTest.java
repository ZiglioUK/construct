package construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static construct.Macros.Bitwise;
import static construct.Macros.Field;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import construct.Core.Construct;

public class MacrosTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void BitIntegerAdapterTest() {
  	Construct bw;
    
    bw = Bitwise( Field("bitwise",8) );
    assertArrayEquals( new byte[]{1,1,1,1,1,1,1,1}, (byte[]) bw.parse( new byte[]{ (byte) 0xFF } ));
    assertEquals( (byte)0xFF, bw.build(new byte[]{1,1,1,1,1,1,1,1})[0]);
    
/*    
    [Bitwise(Field("bitwise", lambda ctx: 8)).parse, "\xff", "\x01" * 8, None],
    [Bitwise(Field("bitwise", lambda ctx: 8)).build, "\x01" * 8, "\xff", None],
*/
  }
}

