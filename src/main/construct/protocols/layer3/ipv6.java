package construct.protocols.layer3;

import static construct.Core.*;
import static construct.Macros.*;
import static construct.Adapters.*;
import static construct.lib.Containers.*;
import static construct.lib.Binary.*;
import static construct.protocols.layer3.ipv4.*;

/**
 * Ethernet (TCP/IP protocol stack) 
*/
public class ipv6 {

    public static Adapter Ipv6Address( String name ) {
    	return IpAddressAdapter( Field( name, 16));
    }
  
  public static Construct ipv6_header = Struct( 
  		"ip_header",
  		EmbeddedBitStruct(
          OneOf(Bits("version", 4), ListContainer(6)),
          Bits("traffic_class", 8),
          Bits("flow_label", 20)
  		),
  		UBInt16("payload_length"),
      ProtocolEnum(UBInt8("protocol")),
      UBInt8("hoplimit"),
      Alias("ttl", "hoplimit"),
      Ipv6Address("source"),
      Ipv6Address("destination")
  );

  public static void main(String[] args) {
  	byte[] cap = ByteArray( hexStringToByteArray("6ff0000001020680"), "0123456789ABCDEF".getBytes(), "FEDCBA9876543210".getBytes() ); 
  	Container c = ipv6_header.parse(cap);
  	System.out.println(c);
  	byte[] ba = ipv6_header.build(c);
  	System.out.println( byteArrayToHexString(ba) );
  }
  
}
