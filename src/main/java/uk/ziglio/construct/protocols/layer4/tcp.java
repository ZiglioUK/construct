/**
 * Transmission Control Protocol (TCP/IP protocol stack)
*/
package uk.ziglio.construct.protocols.layer4;

import static uk.ziglio.construct.Adapters.ExprAdapter;
import static uk.ziglio.construct.Core.Struct;
import static uk.ziglio.construct.Macros.EmbeddedBitStruct;
import static uk.ziglio.construct.Macros.Field;
import static uk.ziglio.construct.Macros.Flag;
import static uk.ziglio.construct.Macros.Nibble;
import static uk.ziglio.construct.Macros.Padding;
import static uk.ziglio.construct.Macros.UBInt16;
import static uk.ziglio.construct.Macros.UBInt32;
import static uk.ziglio.construct.lib.Binary.byteArrayToHexString;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import uk.ziglio.construct.core.Construct;
import uk.ziglio.construct.lib.Containers.Container;


public class tcp {
  public static Construct tcp_header = Struct( 
  		"tcp_header",
      UBInt16("source"),
      UBInt16("destination"),
      UBInt32("seq"),
      UBInt32("ack"),
      EmbeddedBitStruct(
  	    ExprAdapter( Nibble("header_length"),
  	    		(obj, context)-> (Integer)obj / 4,
  	    		(obj, context)->  (Integer)obj * 4
  	    		),
          Padding(3),
          Struct("flags",
              Flag("ns"),
              Flag("cwr"),
              Flag("ece"),
              Flag("urg"),
              Flag("ack"),
              Flag("psh"),
              Flag("rst"),
              Flag("syn"),
              Flag("fin")
          )
  	  ),
      UBInt16("window"),
      UBInt16("checksum"),
      UBInt16("urgent"),
      Field( "options", ctx -> (Integer)ctx.get("header_length") - 20)
   );

  public static void main(String[] args) {
  	byte[] cap = hexStringToByteArray("0db5005062303fb21836e9e650184470c9bc0000"); 
  	Container c = tcp_header.parse(cap);
  	System.out.println(c);
  	byte[] ba = tcp_header.build(c);
  	System.out.println( byteArrayToHexString(ba) );
  }
  
}
