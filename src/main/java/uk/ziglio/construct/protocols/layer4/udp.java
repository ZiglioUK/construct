/**
 * User Datagram Protocol (TCP/IP protocol stack)
*/
package uk.ziglio.construct.protocols.layer4;

import static uk.ziglio.construct.Adapters.ExprAdapter;
import static uk.ziglio.construct.Core.Struct;
import static uk.ziglio.construct.Core.Value;
import static uk.ziglio.construct.Macros.UBInt16;
import static uk.ziglio.construct.lib.Binary.byteArrayToHexString;
import static uk.ziglio.construct.lib.Binary.hexStringToByteArray;

import uk.ziglio.construct.Core.Construct;
import uk.ziglio.construct.lib.Containers.Container;


public class udp {
  public static Construct udp_header = Struct( 
  		"udp_header",
  		Value("header_length", ctx-> 8 ),
	    UBInt16("source"),
	    UBInt16("destination"), 
	    ExprAdapter( UBInt16("payload_length"),
	    		(obj, context)-> (Integer)obj + 8,
	    		(obj, context)->  (Integer)obj - 8 ),
			UBInt16("checksum")
	);

  public static void main(String[] args) {
  	byte[] cap = hexStringToByteArray("0bcc003500280689"); 
  	Container c = udp_header.parse(cap);
  	System.out.println(c);
  	byte[] ba = udp_header.build(c);
  	System.out.println( byteArrayToHexString(ba) );
  }
  
}
