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

