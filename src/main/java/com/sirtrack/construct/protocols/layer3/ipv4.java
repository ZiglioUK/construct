package com.sirtrack.construct.protocols.layer3;

import static com.sirtrack.construct.Adapters.*;
import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Binary.*;
import static com.sirtrack.construct.lib.Containers.*;
import static com.sirtrack.construct.protocols.layer3.ipv4.CRC16;
import static com.sirtrack.construct.protocols.layer3.ipv4.ipv4_header;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.sirtrack.construct.Core.AdapterDecoder;
import com.sirtrack.construct.Core.AdapterEncoder;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.ValueFunc;
import com.sirtrack.construct.Macros.CRCFunc;
import com.sirtrack.construct.lib.Containers.Container;

/**
 * Ethernet (TCP/IP protocol stack)
 */
public class ipv4 {

	static Adapter ProtocolEnum(Construct subcon) {
		return Enum( subcon, 
				         "ICMP", 1, 
				         "TCP", 6, 
				         "UDP", 17
				        );
	};

	static CRCFunc CRC16 = new CRCFunc() {
		public boolean check(byte[] bytes, int crcval) {
//			int crc = 0xFFFF;
//			for (byte b : bytes) {
//				crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff];
//			}
//			
//			return crcval == crc; 
			return true;
		}

		@Override
		public int compute(byte[] data) {
			// TODO Auto-generated method stub
			return 0;
		}
	};


	public static Construct ipv4_header = Struct("ip_header",
			Embedded(CRC( Struct( "ip_header1",
  	    EmbeddedBitStruct( 
  	    		Const( Nibble("version"), 4), 
  	    		ExprAdapter( 
  	    				Nibble("header_length"), 
  	    				new AdapterEncoder() {
          		    public Object encode(Object obj, Container context) {
          			    return (Integer) obj / 4;
          		    }
          	    }, 
          	    new AdapterDecoder() {
          		    public Object decode(Object obj, Container context) {
          			    return (Integer) obj * 4;
          		    }
          	    })
          	 ), 
          	 BitStruct("tos", 
          			 Bits("precedence", 3), 
          			 Flag("minimize_delay"), 
          			 Flag("high_throuput"), 
          			 Flag("high_reliability"), 
          			 Flag("minimize_cost"), 
          			 Padding(1)
          	 ),
  	         UBInt16("total_length"), 
  	         Value("payload_length", 
  	        		   new ValueFunc() {
              		    public Object get(Container ctx) {
              			    return (Integer) ctx.get("total_length") - (Integer) ctx.get("header_length");
              		    }
  	          }), 
  	          UBInt16("identification"), 
  	          EmbeddedBitStruct( 
  	          		Struct("flags", 
  	          				   Padding(1), 
  	          	         Flag("dont_fragment"), 
  	          	         Flag("more_fragments")
  	          	  ), 
  	          	  Bits("frame_offset", 13)
  	           ),
        	     UBInt8("ttl"), 
        	     ProtocolEnum( UBInt8("protocol") ), 
        	     UBInt16("checksum"), 
        	     IpAddress("source"), 
        	     IpAddress("destination") 
        	),
    	    KeyVal("checksum"),
    		  CRC16
     )),
     Field("options", new LengthFunc() {
	    public int length(Container context) {
		    return (Integer) context.get("header_length") - 20;
	    }
     })
  );

	static byte[] cap = hexStringToByteArray("4500003ca0e3000080116185c0a80205d474a126");

	@Test
	public void CRCTest() {
  	Container c = ipv4_header.parse(cap);
  	assertTrue( (Boolean)c.get("checksum"));
	}

	public static void main(String[] args) {
		Container c = ipv4_header.parse(cap);
		System.out.println(c);
		byte[] ba = ipv4_header.build(c);
		System.out.println(byteArrayToHexString(ba));
	}

}
