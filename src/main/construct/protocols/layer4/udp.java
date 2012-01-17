/**
 * User Datagram Protocol (TCP/IP protocol stack)
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
import construct.Core.ValueFunc;
import construct.lib.Containers.Container;

public class udp {
  static Construct udp_header = Struct( 
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
