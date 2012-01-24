/**
 * User Datagram Protocol (TCP/IP protocol stack)
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
import com.sirtrack.construct.Core.ValueFunc;
import com.sirtrack.construct.lib.Containers.Container;


public class udp {
  public static Construct udp_header = Struct( 
  		"udp_header",
  		Value("header_length", new ValueFunc(){
	    	public Object get(Container ctx) {
	    		return 8;
	    }}),
	    UBInt16("source"),
	    UBInt16("destination"), 
	    ExprAdapter( UBInt16("payload_length"),
	    		new AdapterEncoder() {
	    		  public Object encode(Object obj, Container context) {
	    		  	return (Integer)obj + 8;
						}
					},
					new AdapterDecoder() {
						public Object decode(Object obj, Container context) {
							return (Integer)obj - 8;
						}
					}
			),
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
