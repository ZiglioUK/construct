/**
 * Transmission Control Protocol (TCP/IP protocol stack)
*/
package com.sirtrack.construct.protocols.layer4;

import static com.sirtrack.construct.Adapters.*;
import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Binary.*;
import static com.sirtrack.construct.lib.Containers.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.sirtrack.construct.Core.AdapterDecoder;
import com.sirtrack.construct.Core.AdapterEncoder;
import com.sirtrack.construct.Core.LengthFunc;
import com.sirtrack.construct.Core.ValueFunc;
import com.sirtrack.construct.lib.Containers.Container;


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
  						public Integer decode(Object obj, Container context) {
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
