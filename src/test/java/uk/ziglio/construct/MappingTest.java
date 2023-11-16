package uk.ziglio.construct;

//from construct import FieldError, SizeofError

import static org.junit.Assert.*;
import static uk.ziglio.construct.Core.*;
import static uk.ziglio.construct.macros.Macros.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.ziglio.construct.Adapter;

public class MappingTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void FlagTest() {
    Adapter flag;
    
    flag = Flag("flag");
    assertTrue( (Boolean)(flag.parse( ByteArray(1))) );
    
    flag = Flag("flag", (byte)0, (byte)1, null );
    assertFalse( (Boolean)(flag.parse( ByteArray(1))) );
    
    flag = Flag("flag");
    assertFalse( (Boolean)(flag.parse( ByteArray(2))) );

    flag = Flag("flag", (byte)0, (byte)1, true );
    assertTrue( (Boolean)(flag.parse( ByteArray(2))) );

    flag = Flag("flag");
    assertArrayEquals( ByteArray(1), flag.build(true));

    flag = Flag("flag", (byte)0, (byte)1, null );
    assertArrayEquals( ByteArray(0), flag.build(true));
  }
}

