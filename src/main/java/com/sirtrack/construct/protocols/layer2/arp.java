package com.sirtrack.construct.protocols.layer2;

import static com.sirtrack.construct.Adapters.IpAddressAdapter;
import static com.sirtrack.construct.Core.Equals;
import static com.sirtrack.construct.Core.LengthField;
import static com.sirtrack.construct.Core.Struct;
import static com.sirtrack.construct.Macros.Enum;
import static com.sirtrack.construct.Macros.Field;
import static com.sirtrack.construct.Macros.IfThenElse;
import static com.sirtrack.construct.Macros.Rename;
import static com.sirtrack.construct.Macros.UBInt16;
import static com.sirtrack.construct.Macros.UBInt8;
import static com.sirtrack.construct.lib.Binary.byteArrayToHexString;
import static com.sirtrack.construct.lib.Binary.hexStringToByteArray;
import static com.sirtrack.construct.protocols.layer2.ethernet.MacAddressAdapter;

import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.Switch;
import com.sirtrack.construct.lib.Containers.Container;

/**
 * Ethernet (TCP/IP protocol stack)
 */
public class arp {
	static Switch HwAddress(String name){
    return IfThenElse( 
    		name, 
    		Equals("hardware_type", "ETHERNET"),
        MacAddressAdapter( Field( "data", LengthField("hwaddr_length"))),
        Field( "data", LengthField("hwaddr_length"))
     );
	};

	static Switch ProtoAddress(String name){
    return IfThenElse( 
    		name, 
    		Equals("protocol_type", "IP"),
        IpAddressAdapter( Field("data", LengthField("protoaddr_length"))),
        Field("data", LengthField("protoaddr_length"))
        );
	};

	static Construct arp_header = Struct( 
			"arp_header",
	    Enum(UBInt16("hardware_type"),
	        "ETHERNET", 1,
	        "EXPERIMENTAL_ETHERNET", 2,
	        "ProNET_TOKEN_RING", 4,
	        "CHAOS", 5,
	        "IEEE802", 6,
	        "ARCNET", 7,
	        "HYPERCHANNEL", 8,
	        "ULTRALINK", 13,
	        "FRAME_RELAY", 15,
	        "FIBRE_CHANNEL", 18,
	        "IEEE1394", 24,
	        "HIPARP", 28,
	        "ISO7816_3", 29,
	        "ARPSEC", 30,
	        "IPSEC_TUNNEL", 31,
	        "INFINIBAND", 32
	    ),
	    Enum(UBInt16("protocol_type"),
	        "IP", 0x0800
	    ),
	    UBInt8("hwaddr_length"),
	    UBInt8("protoaddr_length"),
	    Enum(UBInt16("opcode"),
	        "REQUEST", 1,
	        "REPLY", 2,
	        "REQUEST_REVERSE", 3,
	        "REPLY_REVERSE", 4,
	        "DRARP_REQUEST", 5,
	        "DRARP_REPLY", 6,
	        "DRARP_ERROR", 7,
	        "InARP_REQUEST", 8,
	        "InARP_REPLY", 9,
	        "ARP_NAK", 10
	    ),
	    HwAddress("source_hwaddr"),
	    ProtoAddress("source_protoaddr"),
	    HwAddress("dest_hwaddr"),
	    ProtoAddress("dest_protoaddr")
	);

	Construct rarp_header = Rename("rarp_header", arp_header);

  public static void main(String[] args) {
  	byte[] cap1 = hexStringToByteArray("00010800060400010002e3426009c0a80204000000000000c0a80201"); 
  	Container c1 = arp_header.parse(cap1);
  	System.out.println(c1);
  	byte[] ba1 = arp_header.build(c1);
  	System.out.println( byteArrayToHexString(ba1) );

  	byte[] cap2 = hexStringToByteArray("00010800060400020011508c283cc0a802010002e3426009c0a80204"); 
  	Container c2 = arp_header.parse(cap2);
  	System.out.println(c2);
  	byte[] ba2 = arp_header.build(c2);
  	System.out.println( byteArrayToHexString(ba2) );
  }
}