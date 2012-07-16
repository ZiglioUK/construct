package com.sirtrack.construct.protocols.layer2;

import static com.sirtrack.construct.Adapters.*;
import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Binary.*;
import static com.sirtrack.construct.lib.Containers.*;

import java.util.Arrays;

import com.sirtrack.construct.Adapter;

/**
 * Ethernet (TCP/IP protocol stack) 
*/
public class ethernet {

    public static Adapter MacAddress( String name ) {
    	return MacAddressAdapter( Field( name, 6));
    }
  
    public static Adapter MacAddressAdapter( Construct field ) {
  	return new Adapter( field ){
      public Object encode(Object obj, Container context) {
				String hexStr = (String)obj;
				hexStr = hexStr.replace("-", "");
				return hexStringToByteArray(hexStr);
      }
      public Object decode( Object obj, Container context) {
      	StringBuilder sb = new StringBuilder();
      	for( byte b : (byte[])obj ){
      		if (sb.length() > 0)
            sb.append('-');
      		sb.append(String.format("%02x", b));
      	}
      	return sb.toString();
      }

  	};
  }
	
  public static Construct ethernet_header = Struct("ethernet_header",
      MacAddress("destination"),
      MacAddress("source"),
      Enum(UBInt16("type"), 
          "IPv4",  0x0800,
          "ARP",  0x0806,
          "RARP",  0x8035,
          "X25",  0x0805,
          "IPX",  0x8137,
          "IPv6",  0x86DD,
          "_default_",  Pass
   ));

  public static void main(String[] args) {
//  	byte[] cap = hexStringToByteArray("0011508c283c0002e34260090800"); 
  	byte[] cap = hexStringToByteArray("0011508c283c001150886b570800"); 
  	Container c = ethernet_header.parse(cap);
  	System.out.println(c);
  	byte[] ba = ethernet_header.build(c);
  	System.out.println( byteArrayToHexString(ba) );
  }
}
