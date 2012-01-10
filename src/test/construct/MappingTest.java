package construct;

//from construct import FieldError, SizeofError

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static construct.Core.*;
import static construct.Macros.*;

public class MappingTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void FlagTest() {
    Adapter flag;
    
    flag = Flag("flag");
    assertTrue( (Boolean)(flag.parse( new byte[]{1})) );
    
    flag = Flag("flag", (byte)0, (byte)1, null );
    assertFalse( (Boolean)(flag.parse( new byte[]{1})) );
    
    flag = Flag("flag");
    assertFalse( (Boolean)(flag.parse( new byte[]{2})) );

    flag = Flag("flag", (byte)0, (byte)1, true );
    assertTrue( (Boolean)(flag.parse( new byte[]{2})) );

    flag = Flag("flag");
    assertArrayEquals( new byte[]{1}, flag.build(true));

    flag = Flag("flag", (byte)0, (byte)1, null );
    assertArrayEquals( new byte[]{0}, flag.build(true));
  }
}

