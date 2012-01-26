package com.sirtrack.construct.protocols.layer3;

import static com.sirtrack.construct.Adapters.*;
import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Binary.*;
import static com.sirtrack.construct.lib.Containers.*;
import static com.sirtrack.construct.protocols.layer3.ipv4.*;

/**
 * Ethernet (TCP/IP protocol stack) 
*/
public class ipv6 {

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
