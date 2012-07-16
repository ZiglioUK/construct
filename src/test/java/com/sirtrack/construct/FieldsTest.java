package com.sirtrack.construct;

import static com.sirtrack.construct.Core.ByteArray;
import static com.sirtrack.construct.Macros.SBInt16;
import static com.sirtrack.construct.Macros.SBInt32;
import static com.sirtrack.construct.Macros.SBInt8;
import static com.sirtrack.construct.Macros.SLInt16;
import static com.sirtrack.construct.Macros.SLInt32;
import static com.sirtrack.construct.Macros.SLInt8;
import static com.sirtrack.construct.Macros.UBInt16;
import static com.sirtrack.construct.Macros.UBInt32;
import static com.sirtrack.construct.Macros.UBInt8;
import static com.sirtrack.construct.Macros.ULInt16;
import static com.sirtrack.construct.Macros.ULInt32;
import static com.sirtrack.construct.Macros.ULInt8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sirtrack.construct.Core.FormatField;



public class FieldsTest {

	@Test
  public void parseTest() {
  
  	FormatField f;
  	
  	f = UBInt8("f");
  	assertEquals( 255, f.parse(ByteArray( 0xFF )));

  	f = UBInt16("f");
  	assertEquals( 65518, f.parse(ByteArray( 0xFF, 0xEE )));

  	f = UBInt32("f");
  	assertEquals( 4293844428L, f.parse(ByteArray( 0xFF, 0xEE, 0xDD, 0xCC )));

//  	unsupported for now
//  	f = UBInt64("f");
//  	assertEquals( 2^64-1, f.parse( ByteArray( 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFE ) ));

  	f = SBInt8("f");
  	assertEquals( -1, f.parse(ByteArray( 0xFF )));

  	f = SBInt16("f");
  	assertEquals( -18, f.parse(ByteArray( 0xFF, 0xEE )));

  	f = SBInt32("f");
  	assertEquals( -1122868, f.parse(ByteArray( 0xFF, 0xEE, 0xDD, 0xCC )));
  	
//  	unsupported for now
//  	f = SBInt64("f");
//  	assertEquals( ByteArray( 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF ), f.parse(-1));

  	f = ULInt8("f");
  	assertEquals( 255, f.parse(ByteArray( 0xFF )));

  	f = ULInt16("f");
  	assertEquals( 65518, f.parse(ByteArray( 0xEE, 0xFF )));

  	f = ULInt32("f");
  	assertEquals( 4293844428L, f.parse(ByteArray( 0xCC, 0xDD, 0xEE, 0xFF )));

//	unsupported for now
//	f = ULInt64("f");
//	assertEquals( ByteArray( 0x99, 0x00, 0xAA, 0xBB, 0xCC, 0xDD, 0xEE, 0xFF ), f.parse( 0xFFEEDDCCBBAA0099 ));

  	f = SLInt8("f");
  	assertEquals( -1, f.parse(ByteArray( 0xFF )));

  	f = SLInt16("f");
  	assertEquals( -18, f.parse(ByteArray( 0xEE, 0xFF )));

  	f = SLInt32("f");
  	assertEquals( -1122868, f.parse(ByteArray( 0xCC, 0xDD, 0xEE, 0xFF )));
  	
//  	unsupported for now
//  	f = SLInt64("f");
//  	assertEquals( -1, f.parse(ByteArray( 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF )));
  }

	@Test
  public void buildTest() {
  
  	FormatField f;
  	
  	f = UBInt8("f");
  	assertArrayEquals( ByteArray( 0xFF ), f.build(255));

  	f = UBInt16("f");
  	assertArrayEquals( ByteArray( 0xFF, 0xEE ), f.build(65518));

  	f = UBInt32("f");
  	assertArrayEquals( ByteArray( 0xFF, 0xEE, 0xDD, 0xCC ), f.build(4293844428L));

//  	unsupported for now
//  	f = UBInt64("f");
//  	assertArrayEquals( ByteArray( 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFE ), f.build( 2^64-1 ));

  	f = SBInt8("f");
  	assertArrayEquals( ByteArray( 0xFF ), f.build(-1));

  	f = SBInt16("f");
  	assertArrayEquals( ByteArray( 0xFF, 0xEE ), f.build(-18));

  	f = SBInt32("f");
  	assertArrayEquals( ByteArray( 0xFF, 0xEE, 0xDD, 0xCC ), f.build(-1122868));
  	
//  	unsupported for now
//  	f = SBInt64("f");
//  	assertArrayEquals( ByteArray( 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF ), f.build(-1));

  	f = ULInt8("f");
  	assertArrayEquals( ByteArray( 0xFF ), f.build(255));

  	f = ULInt16("f");
  	assertArrayEquals( ByteArray( 0xEE, 0xFF ), f.build(65518));

  	f = ULInt32("f");
  	assertArrayEquals( ByteArray( 0xCC, 0xDD, 0xEE, 0xFF ), f.build(4293844428L));

//	unsupported for now
//	f = ULInt64("f");
//	assertArrayEquals( ByteArray( 0x99, 0x00, 0xAA, 0xBB, 0xCC, 0xDD, 0xEE, 0xFF ), f.build( 0xFFEEDDCCBBAA0099 ));

  	f = SLInt8("f");
  	assertArrayEquals( ByteArray( 0xFF ), f.build(-1));

  	f = SLInt16("f");
  	assertArrayEquals( ByteArray( 0xEE, 0xFF ), f.build(-18));

  	f = SLInt32("f");
  	assertArrayEquals( ByteArray( 0xCC, 0xDD, 0xEE, 0xFF ), f.build(-1122868));
  	
//  	unsupported for now
//  	f = SLInt64("f");
//  	assertArrayEquals( ByteArray( 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF ), f.build(-1));
  }
  
//  /**
//	  * @return unsigned, native endianity 8-bit integer
//	  */
//	  static public FormatField UNInt8(String name){
//	   	return new FormatField( name, '=', 'B' );
//	  }
//  /**
//	  * @return unsigned, native endianity 16-bit integer
//	  */
//	  static public FormatField UNInt16(String name){
//	   	return new FormatField( name, '=', 'H' );
//	  }
//  /**
//	  * @return unsigned, native endianity 32-bit integer
//	  */
//	  static public FormatField UNInt32(String name){
//	   	return new FormatField( name, '=', 'L' );
//	  }
//  /**
//	  * @return unsigned, native endianity 64-bit integer
//	  */
//	  static public FormatField UNInt64(String name){
//	   	return new FormatField( name, '=', 'Q' );
//	  }
//  /**
//	  * @return signed, native endianity 8-bit integer
//	  */
//	  static public FormatField SNInt8(String name){
//	   	return new FormatField( name, '=', 'b' );
//	  }
//  /**
//	  * @return signed, native endianity 16-bit integer
//	  */
//	  static public FormatField SNInt16(String name){
//	   	return new FormatField( name, '=', 'h' );
//	  }
//  /**
//	  * @return signed, native endianity 32-bit integer
//	  */
//	  static public FormatField SNInt32(String name){
//	   	return new FormatField( name, '=', 'l' );
//	  }
//  /**
//	  * @return signed, native endianity 64-bit integer
//	  */
//	  static public FormatField SNInt64(String name){
//	   	return new FormatField( name, '=', 'q' );
//	  }
}
