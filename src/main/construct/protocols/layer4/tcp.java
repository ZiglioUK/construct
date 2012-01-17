/**
 * Transmission Control Protocol (TCP/IP protocol stack)
*/
package construct.protocols.layer4;

import static construct.Core.*;
import static construct.Macros.*;
import static construct.Adapters.*;
import static construct.lib.Containers.*;
import static construct.lib.Binary.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import construct.Core.AdapterDecoder;
import construct.Core.AdapterEncoder;
import construct.Core.LengthFunc;
import construct.Core.ValueFunc;
import construct.lib.Containers.Container;

public class tcp {
  public static Construct tcp_header = Struct( 
  		"tcp_header",
      UBInt16("source"),
      UBInt16("destination"),
      UBInt32("seq"),
      UBInt32("ack"),
      EmbeddedBitStruct(
  	    ExprAdapter( Nibble("header_length"),
  	    		new AdapterEncoder() {
  	    		  public Object encode(Object obj, Container context) {
  	    		  	return (Integer)obj / 4;
  						}
  					},
  					new AdapterDecoder() {
  						public Object decode(Object obj, Container context) {
  							return (Integer)obj * 4;
  						}
  					}
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
      Field( "options", new LengthFunc(){
      	public int length(Container ctx){
     	 		return (Integer)ctx.get("header_length") - 20;
     	 	}}
      )
   );

  public static void main(String[] args) {
  	byte[] cap = hexStringToByteArray("0db5005062303fb21836e9e650184470c9bc0000"); 
  	Container c = tcp_header.parse(cap);
  	System.out.println(c);
  	byte[] ba = tcp_header.build(c);
  	System.out.println( byteArrayToHexString(ba) );
  }
  
}
